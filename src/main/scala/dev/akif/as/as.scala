package dev.akif.as

import e.scala.*

infix trait as[Raw, Refined](using narrow: Raw <:< Refined, widen: Refined =:= Raw):
    self =>
    def undefinedValue: Raw

    def validation: Validation[Raw]

    lazy val undefined: Refined = narrow(undefinedValue)

    given instance: (Raw as Refined) = this

    lazy val typeName: String = self.getClass.getSimpleName.replaceAll("\\$", "")

    def apply(raw: Raw): E or Refined =
        val causes = validation(raw)
        if causes.isEmpty then
            narrow(raw).toEOr
        else
            E.name("invalid-data").causes(causes).data("type" -> typeName).data("value" -> raw).toEOr

    def unsafe(raw: Raw): Refined =
        apply(raw).fold(e => throw e.toException, identity)

    def applyOrUndefined(raw: Raw): Refined =
        apply(raw).getOrElse(undefined)

    extension (refined: Refined)
        def value: Raw = widen(refined)

extension [Raw](raw: Raw)
    def as[Refined](using r: Raw as Refined): E or Refined =
        r.apply(raw)

    def asUnsafe[Refined](using r: Raw as Refined): Refined =
        r.unsafe(raw)

    def asOrUndefined[Refined](using r: Raw as Refined): Refined =
        r.applyOrUndefined(raw)
