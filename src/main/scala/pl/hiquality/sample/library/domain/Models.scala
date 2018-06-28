package pl.hiquality.sample.library.domain

import scala.collection.immutable.ListSet

case class Isbn(value: String) extends AnyVal
case class Title(value: String) extends AnyVal
case class Author(value: String) extends AnyVal

case class Book(isbn: Isbn, title: Title, authors: ListSet[Author])
