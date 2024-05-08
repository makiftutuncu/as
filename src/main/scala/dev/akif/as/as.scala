package dev.akif.as

import e.scala.*

/**
 * This trait provides functionality to refine a raw type to be validated and used as a refined type
 *
 * ```scala
 * import dev.akif.as.*
 *
 * opaque type AuthorId <: Long = Long
 *
 * object AuthorId extends (Long as AuthorId):
 *     override val undefinedValue: Long = 0L
 *
 *     override val validation: Validation[Long] =
 *         Validation.min(lowerLimit = 0L, inclusive = false)
 * ```
 *
 * @tparam Raw     raw type to refine
 * @tparam Refined refined type
 * @param narrow evidence provided by compiler to narrow raw type to refined type
 * @param widen  evidence provided by compiler to widen refined type to raw type
 */
infix trait as[Raw, Refined](using narrow: Raw <:< Refined, widen: Refined =:= Raw):
    self =>
    /** Provides a raw value that is considered as undefined */
    def undefinedValue: Raw

    /** Provides a way to validate raw values to be considered as refined values */
    def validation: Validation[Raw]

    /** Provides a refined value that is considered as undefined */
    lazy val undefined: Refined = narrow(undefinedValue)

    /** Given instance of this type refinement so it is available when refined type is referred */
    given instance: (Raw as Refined) = this

    /** Provides name of the refined type */
    lazy val typeName: String = self.getClass.getSimpleName.replaceAll("\\$", "")

    /**
     * Validates and refines given raw value to be used as a refined value
     *
     * @param raw raw value to refine
     * @return an [[e.scala.EOr]] with either an [[e.scala.E]] containing validation errors or a refined value
     */
    def apply(raw: Raw): E or Refined =
        val causes = validation(raw)
        if causes.isEmpty then
            narrow(raw).toEOr
        else
            E.name("invalid-data").causes(causes).data("type" -> typeName).data("value" -> raw).toEOr

    /**
     * Validates and refines given raw value to be used as a refined value or throws an exception
     *
     * @param raw raw value to refine
     * @return a refined value
     * @throws e.scala.EException if given raw value is not valid
     */
    def unsafe(raw: Raw): Refined =
        apply(raw).fold(e => throw e.toException, identity)

    /**
     * Validates and refines given raw value to be used as a refined value or returns [[this.undefined]] value
     *
     * @param raw raw value to refine
     * @return a refined value or [[this.undefined]] value
     */
    def applyOrUndefined(raw: Raw): Refined =
        apply(raw).getOrElse(undefined)

    extension (refined: Refined)
        /**
         * Widens given refined value to be used as a raw value
         *
         * @return a raw value
         */
        def value: Raw = widen(refined)

extension [Raw](raw: Raw)
    /**
     * Refines this raw value to be used as a refined value
     *
     * @tparam Refined refined type
     * @param r evidence provided by compiler to refine raw type to refined type
     * @return an [[e.scala.EOr]] with either an [[e.scala.E]] containing validation errors or a refined value
     */
    def as[Refined](using r: Raw as Refined): E or Refined =
        r.apply(raw)

    /**
     * Refines this raw value to be used as a refined value or throws an exception
     *
     * @tparam Refined refined type
     * @param r evidence provided by compiler to refine raw type to refined type
     * @return a refined value
     * @throws e.scala.EException if given raw value is not valid
     */
    def asUnsafe[Refined](using r: Raw as Refined): Refined =
        r.unsafe(raw)

    /**
     * Refines this raw value to be used as a refined value or returns [[as.undefined]] value
     *
     * @tparam Refined refined type
     * @param r evidence provided by compiler to refine raw type to refined type
     * @return a refined value or [[as.undefined]] value
     */
    def asOrUndefined[Refined](using r: Raw as Refined): Refined =
        r.applyOrUndefined(raw)
