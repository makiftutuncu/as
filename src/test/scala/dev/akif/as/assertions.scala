package dev.akif.as

import e.scala.{E, or}
import munit.Assertions.{assertEquals, fail}
import munit.Location

extension [A](obtained: A)
    infix def shouldBe[B](expected: B)(using Location, B <:< A): Unit =
        assertEquals(obtained, expected)

extension [A](eor: E or A)
    infix def shouldBeError(expected: E)(using Location): Unit =
        eor.fold(
            obtained => assertEquals(obtained, expected),
            value => fail(s"Expected error $expected but got value $value")
        )

    infix def shouldBeValue(expected: A)(using Location): Unit =
        eor.fold(
            e => fail(s"Expected value $expected but got error $e"),
            obtained => assertEquals(obtained, expected)
        ) 
