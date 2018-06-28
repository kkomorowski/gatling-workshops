package pl.hiquality.sample.library.mappers

import io.circe.{Decoder, Encoder}
import pl.hiquality.sample.library.domain.{Author, Book, Isbn, Title}

object Json {
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
