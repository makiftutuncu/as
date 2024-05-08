package dev.akif.as

import e.scala.*
import munit.FunSuite

class ValidationTest extends FunSuite:
    private val invalidAuthorId = E.name("invalid-data").data("type" -> "AuthorId")
    private val invalidAuthorName = E.name("invalid-data").data("type" -> "AuthorName")
    private val invalidBookId = E.name("invalid-data").data("type" -> "BookId")
    private val invalidBookName = E.name("invalid-data").data("type" -> "BookName")
    private val invalidLowercaseNonEmptyText = E.name("invalid-data").data("type" -> "LowercaseNonEmptyText")
    private val emptyError = E.message("Value is empty")
    private val nonLowercaseError = E.message("Value must be lowercase")
    private val tooShortError = E.message("Value must be longer than 2 characters")

    test("AuthorId must be positive and less than 100"):
        0L.as[AuthorId] shouldBeError invalidAuthorId.cause(E.message("Value is less than or equal to 0")).data("value" -> "0")
        101L.as[AuthorId] shouldBeError invalidAuthorId.cause(E.message("Value is greater than or equal to 100")).data("value" -> "101")
        1L.as[AuthorId] shouldBeValue 1L

    test("AuthorName must not be blank"):
        "".as[AuthorName] shouldBeError invalidAuthorName.cause(E.message("Value is blank")).data("value" -> "")
        " ".as[AuthorName] shouldBeError invalidAuthorName.cause(E.message("Value is blank")).data("value" -> " ")
        "Akif".as[AuthorName] shouldBeValue "Akif"

    test("BookId must be positive"):
        0L.as[BookId] shouldBeError invalidBookId.cause(E.message("Value is less than or equal to 0")).data("value" -> "0")
        1L.as[BookId] shouldBeValue 1L

    test("BookName must not be blank"):
        "".as[BookName] shouldBeError invalidBookName.cause(E.message("Value is blank")).data("value" -> "")
        " ".as[BookName] shouldBeError invalidBookName.cause(E.message("Value is blank")).data("value" -> " ")
        "Scala".as[BookName] shouldBeValue "Scala"

    test("LowercaseNonEmptyText must be non-empty, lowercase and longer than 2 characters"):
        "".as[LowercaseNonEmptyText] shouldBeError invalidLowercaseNonEmptyText.causes(emptyError, tooShortError).data("value" -> "")
        "Ak".as[LowercaseNonEmptyText] shouldBeError invalidLowercaseNonEmptyText.causes(nonLowercaseError, tooShortError).data("value" -> "Ak")
        "Akif".as[LowercaseNonEmptyText] shouldBeError invalidLowercaseNonEmptyText.cause(nonLowercaseError).data("value" -> "Akif")
        "ak".as[LowercaseNonEmptyText] shouldBeError invalidLowercaseNonEmptyText.cause(tooShortError).data("value" -> "ak")
        "akif".as[LowercaseNonEmptyText] shouldBeValue "akif"
