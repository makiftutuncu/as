package dev.akif.as

import e.scala.E

import scala.math.Ordering.Implicits.infixOrderingOps

opaque type Validation[A] = A => List[E]

object Validation:
    extension [A](self: Validation[A])
        def apply(value: A): List[E] = self(value)

        infix def and(other: Validation[A]): Validation[A] =
            (value: A) =>
                self(value) ++ other(value)

    def valid[A]: Validation[A] = _ => List.empty

    def make[A](predicate: A => Boolean, failureMessage: A => String): Validation[A] =
        (value: A) =>
            if predicate(value) then List.empty else List(E.message(failureMessage(value)))

    def min[N](lowerLimit: N, inclusive: Boolean = true)(using numeric: Numeric[N]): Validation[N] =
        (value: N) =>
            val validator =
                if inclusive then make[N](_ >= lowerLimit, _ => s"Value is less than $lowerLimit")
                else make[N](_ > lowerLimit, _ => s"Value is less than or equal to $lowerLimit")
            validator(value)

    def max[N](upperLimit: N, inclusive: Boolean = true)(using numeric: Numeric[N]): Validation[N] =
        (value: N) =>
            val validator =
                if inclusive then make[N](_ <= upperLimit, _ => s"Value is greater than $upperLimit")
                else make[N](_ < upperLimit, _ => s"Value is greater than or equal to $upperLimit")
            validator(value)

    val nonEmptyText: Validation[String] =
        (value: String) =>
            val validator = make[String](
                predicate = _.nonEmpty,
                failureMessage = _ => "Value is empty"
            )
            validator(value)

    def all[A](validations: Validation[A]*): Validation[A] =
        (value: A) =>
            validations.reduce(_ and _)(value)
