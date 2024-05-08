package dev.akif.as

import e.scala.E

import scala.math.Ordering.Implicits.infixOrderingOps

/**
 * A type alias for a validation function that takes a raw value and returns a list of [[e.scala.E]] errors
 *
 * @tparam A type of the raw value to validate
 */
type Validation[A] = A => List[E]

/**
 * Provides common validation functions to be used as [[Validation]]
 */
object Validation:
    extension [A](self: Validation[A])
        /**
         * Applies this validation to given value
         *
         * @param value value to validate
         * @return a [[List]] of [[e.scala.E]] errors
         */
        def apply(value: A): List[E] = self(value)

        /**
         * Combines this [[Validation]] with another [[Validation]] where validation errors of both are appended
         *
         * @param other [[Validation]] with which to combine
         * @return a new [[Validation]] that, when applied, returns validation errors from both this and the other [[Validation]]
         */
        infix def and(other: Validation[A]): Validation[A] =
            (value: A) =>
                self(value) ++ other(value)

    /**
     * A [[Validation]] that always returns an empty list of errors
     *
     * @tparam A type of the value to validate
     * @return an empty list of errors
     */
    def valid[A]: Validation[A] = _ => List.empty

    /**
     * Makes a custom [[Validation]] that runs given predicate and returns a single error with given message when failed
     *
     * @param predicate      predicate to run for validation
     * @param failureMessage message to use when validation fails
     * @tparam A type of the value to validate
     * @return a [[Validation]] that runs given predicate and returns a single error with given message when failed
     */
    def make[A](predicate: A => Boolean, failureMessage: A => String): Validation[A] =
        (value: A) =>
            if predicate(value) then List.empty else List(E.message(failureMessage(value)))

    /**
     * Makes a [[Validation]] that checks that given numerical value conforms to given lower limit
     *
     * @param lowerLimit lower limit to check
     * @param inclusive  whether to include lower limit or not
     * @param numeric    evidence provided by the compiler for proving type is numeric
     * @tparam N type of the numerical value
     * @return a [[Validation]] that checks if given numerical value conforms to given lower limit
     */
    def min[N](lowerLimit: N, inclusive: Boolean = true)(using numeric: Numeric[N]): Validation[N] =
        (value: N) =>
            val validator =
                if inclusive then make[N](_ >= lowerLimit, _ => s"Value is less than $lowerLimit")
                else make[N](_ > lowerLimit, _ => s"Value is less than or equal to $lowerLimit")
            validator(value)

    /**
     * Makes a [[Validation]] that checks that given numerical value conforms to given upper limit
     *
     * @param upperLimit upper limit to check
     * @param inclusive  whether to include upper limit or not
     * @param numeric    evidence provided by the compiler for proving type is numeric
     * @tparam N type of the numerical value
     * @return a [[Validation]] that checks if given numerical value conforms to given upper limit
     */
    def max[N](upperLimit: N, inclusive: Boolean = true)(using numeric: Numeric[N]): Validation[N] =
        (value: N) =>
            val validator =
                if inclusive then make[N](_ <= upperLimit, _ => s"Value is greater than $upperLimit")
                else make[N](_ < upperLimit, _ => s"Value is greater than or equal to $upperLimit")
            validator(value)

    /**
     * A [[Validation]] that checks that given String value is not empty
     */
    val nonEmptyText: Validation[String] =
        (value: String) =>
            val validator = make[String](
                predicate = _.nonEmpty,
                failureMessage = _ => "Value is empty"
            )
            validator(value)

    /**
     * A [[Validation]] that runs all given validations and returns all errors
     *
     * @param validations validations to run
     * @tparam A type of the value to validate
     * @return a [[Validation]] that runs all given validations and returns all errors
     */
    def all[A](validations: Validation[A]*): Validation[A] =
        (value: A) =>
            validations.reduce(_ and _)(value)
