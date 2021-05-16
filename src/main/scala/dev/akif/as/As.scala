package dev.akif.as

import e.scala.{E, EOr}

infix trait as[-Raw, +Refined <: Raw](_undefined: Raw, _construct: Raw => Refined = identity[Raw]):
  self =>
  val validation: Validator[Raw] = Validator.empty

  val undefined: Refined = _construct(_undefined)

  lazy val typeName: String = self.getClass.getSimpleName.replaceAll("\\$", "")

  def apply(raw: Raw): EOr[Refined] =
    validation.unapply(raw) match
      case None =>
        EOr.Success(_construct(raw))

      case Some(message) =>
        EOr.Failure(
          E(
            name    = Some("validation"),
            message = Some(validation(raw)),
            data    = Map("type" -> typeName, "raw" -> s"$raw")
          )
        )

  def applyUnsafe(raw: Raw): Refined = apply(raw).fold(e => throw e.toException, identity)

  def applyOrUndefined(raw: Raw): Refined = apply(raw).getOrElse(undefined)

  def applyOption(raw: Raw): Option[Refined] = apply(raw).fold(_ => None, s => Some(s))
