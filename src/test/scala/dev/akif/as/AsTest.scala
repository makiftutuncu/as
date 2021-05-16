package dev.akif.as

import e.scala.{E, EOr}
import munit.FunSuite

class AsTest extends FunSuite:
  test("Type is refined") {
    assertNoDiff(
      """
        |error:
        |Found:    (1L : Long)
        |Required: dev.akif.as.AuthorId
        |""".stripMargin,
      compileErrors("val id: AuthorId = 1L").split("\n").take(3).mkString("\n")
    )

    assertNoDiff(
      """
        |error:
        |Found:    dev.akif.as.BookId
        |Required: dev.akif.as.AuthorId
        |""".stripMargin,
      compileErrors("val id: AuthorId = BookId.applyUnsafe(2L)").split("\n").take(3).mkString("\n")
    )
  }

  test("Validations work") {
    assertEquals(
      EOr[AuthorId](E(name = Some("validation"), message = Some("Value must be positive."), data = Map("type" -> "AuthorId", "raw" -> "0"))),
      AuthorId(0)
    )

    assertEquals(
      EOr[AuthorId](E(name = Some("validation"), message = Some("Value must be less than 100."), data = Map("type" -> "AuthorId", "raw" -> "101"))),
      AuthorId(101)
    )

    assertEquals(
      EOr[AuthorName](E(name = Some("validation"), message = Some("Value must not be blank."), data = Map("type" -> "AuthorName", "raw" -> " "))),
      AuthorName(" ")
    )

    assertEquals(
      EOr[BookId](E(name = Some("validation"), message = Some("Value must be positive."), data = Map("type" -> "BookId", "raw" -> "0"))),
      BookId(0)
    )

    assertEquals(EOr(AuthorId.applyUnsafe(1L)), AuthorId(1L))

    assertEquals(EOr(AuthorName.applyUnsafe("Akif")), AuthorName("Akif"))

    assertEquals(EOr(BookId.applyUnsafe(123L)), BookId(123L))

    assertEquals(EOr(BookName.applyUnsafe("")), BookName(""))
  }
