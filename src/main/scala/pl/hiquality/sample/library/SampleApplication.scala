package pl.hiquality.sample.library

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import pl.hiquality.sample.library.domain.{Author, Book, Isbn, Title}

import scala.collection.immutable.ListSet
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

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

    import akka.http.scaladsl.server.Directives._
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import pl.hiquality.sample.library.mappers.Json._

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
