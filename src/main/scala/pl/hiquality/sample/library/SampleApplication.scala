package pl.hiquality.sample.library

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import io.circe.{Decoder, Encoder}
import pl.hiquality.sample.library.Model.{Author, Book, Isbn, Title}

import scala.collection.immutable.ListSet
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn


object Model {
  case class Isbn(value: String) extends AnyVal
  case class Title(value: String) extends AnyVal
  case class Author(value: String) extends AnyVal

  case class Book(isbn: Isbn, title: Title, authors: ListSet[Author])

  object Implicits {
    import io.circe.generic.extras.semiauto._
    implicit val isbnEncoder: Encoder[Isbn] = deriveUnwrappedEncoder
    implicit val isbnDecoder: Decoder[Isbn] = deriveUnwrappedDecoder
    implicit val titleEncoder: Encoder[Title] = deriveUnwrappedEncoder
    implicit val titleDecoder: Decoder[Title] = deriveUnwrappedDecoder
    implicit val authorEncoder: Encoder[Author] = deriveUnwrappedEncoder
    implicit val authorDecoder: Decoder[Author] = deriveUnwrappedDecoder
    implicit val bookEncoder: Encoder[Book] = Encoder.forProduct3("isbn", "title", "authors")(Book.unapply(_).get)
    implicit val bookDecoder: Decoder[Book] = Decoder.forProduct3("isbn", "title", "authors")(Book.apply)
  }
}

object SampleApplication  {

  import scala.concurrent.ExecutionContext.Implicits.global

  var library: List[Book] = Book(Isbn("12345"), Title("Pan Tadeusz"), ListSet(Author("Adam Mickiewicz"))) :: Nil

  def findBookByIsbn(isbn: Isbn): Future[Option[Book]] = Future {
    library.find(book => book.isbn == isbn)
  }

  def listBooks: Future[List[Book]] = Future {
    library
  }

  def addBook(book: Book): Future[Done] = {
    library = book :: library
    Future(Done)
  }

  def main(args: Array[String]): Unit = {

    import Model.Implicits._
    import akka.http.scaladsl.server.Directives._
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

    implicit val system: ActorSystem = ActorSystem("actor-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route =
      get {
        path("book") {
          onSuccess(listBooks) { books =>
            complete(books)
          }
        } ~
        pathPrefix("book" / Remaining) { i =>
          onSuccess(findBookByIsbn(Isbn(i))) {
            case Some(book) => complete(book)
            case None => complete(StatusCodes.NotFound)
          }
        }
      } ~
      post {
        path("book") {
          entity(as[Book]) { book =>
            onComplete(addBook(book)) { _ =>
              complete(book)
            }
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
