package dev.akif.as

private val positive: Validator[Long] = validate(_ > 0, "Value must be positive.")

private val nonBlank: Validator[String] = validate(!_.isBlank, "Value must not be blank.")

opaque type AuthorId = Long
object AuthorId extends (Long as AuthorId)(-1L):
  override val validation: Validator[Long] =
    positive and validate(_ < 100, "Value must be less than 100.")

opaque type AuthorName = String
object AuthorName extends (String as AuthorName)(""):
  override val validation: Validator[String] =
    nonBlank

opaque type BookId = Long
object BookId extends (Long as BookId)(-1L):
  override val validation: Validator[Long] =
    positive

opaque type BookName = String
object BookName extends (String as BookName)(""):
  override val validation: Validator[String] = Validator.empty

case class Author(id: AuthorId, name: AuthorName)

case class Book(id: BookId, author: AuthorId, name: BookName)
