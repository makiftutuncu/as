package dev.akif.as

import Validation.*

private val positive: Validation[Long] = min(lowerLimit = 0, inclusive = false)

private val nonBlank: Validation[String] = make(!_.isBlank, _ => "Value is blank")

opaque type AuthorId <: Long = Long

object AuthorId extends (Long as AuthorId):
    override val undefinedValue: Long = 0L

    override val validation: Validation[Long] =
        positive and max(upperLimit = 100, inclusive = false)

opaque type AuthorName <: String = String

object AuthorName extends (String as AuthorName):
    override val undefinedValue: String = ""

    override val validation: Validation[String] =
        nonBlank

opaque type BookId <: Long = Long

object BookId extends (Long as BookId):
    override val undefinedValue: Long = 0L

    override val validation: Validation[Long] =
        positive

opaque type BookName <: String = String

object BookName extends (String as BookName):
    override val undefinedValue: String = ""

    override def validation: Validation[String] =
        nonBlank

case class Author(id: AuthorId, name: AuthorName)

case class Book(id: BookId, author: AuthorId, name: BookName)

opaque type LowercaseNonEmptyText <: String = String

object LowercaseNonEmptyText extends (String as LowercaseNonEmptyText):
    override val undefinedValue: String = " "

    override val validation: Validation[String] =
        all(
            nonEmptyText,
            make(_.forall(_.isLower), _ => "Value must be lowercase"),
            make(_.length > 2, _ => "Value must be longer than 2 characters")
        )
