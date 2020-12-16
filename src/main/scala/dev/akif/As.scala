package dev.akif

import e.scala.{E, EOr}

trait Refined[+T] {
  val get: T
}

abstract class As[I, +O <: Refined[I]](emptyValue: I, construct: I => O) {
  val validate: PartialFunction[I, String]

  val empty: O = construct(emptyValue)

  def make(value: I): EOr[O] =
    validate.unapply(value) match {
      case None          => EOr(construct(value))
      case Some(message) => EOr(E(name = Some("validation"), message = Some(message)).data("value", value))
    }

  def makeOrEmpty(a: I): O = make(a).getOrElse(empty)

  def makeUnsafe(value: I): O =
    make(value).fold(
      e       => throw e.toException,
      refined => refined
    )

  def apply(value: I): EOr[O] = make(value)
}
