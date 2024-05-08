package dev.akif.as

import e.scala.*
import munit.FunSuite

class AsTest extends FunSuite:
  private val invalidLowercaseNonEmptyText = E.name("invalid-data").data("type" -> "LowercaseNonEmptyText")
  private val emptyError = E.message("Value is empty")
  private val nonLowercaseError = E.message("Value must be lowercase")
  private val tooShortError = E.message("Value must be longer than 2 characters")

  test("Assigning raw value to refined type does not compile"):
    assertNoDiff(
      compileErrors("val id: AuthorId = 1L").split("\n").take(3).mkString("\n").trim,
      """error:
        |Found:    (1L : Long)
        |Required: dev.akif.as.AuthorId""".stripMargin
    )

  test("Assigning a different refined type to a refined type with same raw type does not compile"):
    assertNoDiff(
      compileErrors("val id: AuthorId = 2L.asUnsafe[BookId]").split("\n").take(3).mkString("\n").trim,
      """error:
        |Found:    dev.akif.as.BookId
        |Required: dev.akif.as.AuthorId""".stripMargin
    )

  test("Assigning a refined value to raw type compiles"):
    assertNoDiff(compileErrors("val id: Long = 3L.asUnsafe[BookId]"), "")

  test("Refined type has an undefined value"):
    assertEquals(LowercaseNonEmptyText.undefined.value, " ")

  test("Constructing with an invalid value fails"):
    val e1 = invalidLowercaseNonEmptyText.causes(emptyError, tooShortError).data("value" -> "")
    val e2 = invalidLowercaseNonEmptyText.cause(tooShortError).data("value" -> "ak")
    val e3 = invalidLowercaseNonEmptyText.cause(nonLowercaseError).data("value" -> "Akif")

    LowercaseNonEmptyText("") shouldBeError e1
    "".as[LowercaseNonEmptyText] shouldBeError e1

    LowercaseNonEmptyText("ak") shouldBeError e2
    "ak".as[LowercaseNonEmptyText] shouldBeError e2

    LowercaseNonEmptyText("Akif") shouldBeError e3
    "Akif".as[LowercaseNonEmptyText] shouldBeError e3

  test("Constructing unsafely with an invalid value throws exception"):
    val e1 = invalidLowercaseNonEmptyText.causes(emptyError, tooShortError).data("value" -> "")
    val e2 = invalidLowercaseNonEmptyText.cause(tooShortError).data("value" -> "ak")
    val e3 = invalidLowercaseNonEmptyText.cause(nonLowercaseError).data("value" -> "Akif")

    intercept[EException](LowercaseNonEmptyText.unsafe("")).e shouldBe e1
    intercept[EException]("".asUnsafe[LowercaseNonEmptyText]).e shouldBe e1

    intercept[EException](LowercaseNonEmptyText.unsafe("ak")).e shouldBe e2
    intercept[EException]("ak".asUnsafe[LowercaseNonEmptyText]).e shouldBe e2

    intercept[EException](LowercaseNonEmptyText.unsafe("Akif")).e shouldBe e3
    intercept[EException]("Akif".asUnsafe[LowercaseNonEmptyText]).e shouldBe e3

  test("Constructing safely with an invalid value returns undefined value"):
    LowercaseNonEmptyText.applyOrUndefined("") shouldBe LowercaseNonEmptyText.undefined
    "".asOrUndefined[LowercaseNonEmptyText] shouldBe LowercaseNonEmptyText.undefined

    LowercaseNonEmptyText.applyOrUndefined("ak") shouldBe LowercaseNonEmptyText.undefined
    "ak".asOrUndefined[LowercaseNonEmptyText] shouldBe LowercaseNonEmptyText.undefined

    LowercaseNonEmptyText.applyOrUndefined("Akif") shouldBe LowercaseNonEmptyText.undefined
    "Akif".asOrUndefined[LowercaseNonEmptyText] shouldBe LowercaseNonEmptyText.undefined

  test("Constructing with a valid value succeeds"):
    LowercaseNonEmptyText("akif") shouldBeValue "akif"
    "akif".as[LowercaseNonEmptyText] shouldBeValue "akif"

    LowercaseNonEmptyText.unsafe("akif").value shouldBe "akif"
    "akif".asUnsafe[LowercaseNonEmptyText].value shouldBe "akif"

    LowercaseNonEmptyText.applyOrUndefined("akif").value shouldBe "akif"
    "akif".asOrUndefined[LowercaseNonEmptyText].value shouldBe "akif"
