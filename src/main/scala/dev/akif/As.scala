package dev.akif

import e.scala.E

abstract class As[-Value, +Refined](emptyValue: Value,
                                    constructor: Value => Refined,
                                    validation: PartialFunction[Value, String]) extends (Value => Either[E, Refined]) {
  val empty: Refined = constructor(emptyValue)

  override def apply(value: Value): Either[E, Refined] =
    validation.unapply(value).fold[Either[E, Refined]](Right(constructor(value))) { message =>
      Left(E("validation", message).data("value", value))
    }

  def getOrEmpty(a: Value): Refined = apply(a).getOrElse(empty)
}
