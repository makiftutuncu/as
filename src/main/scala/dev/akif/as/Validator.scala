package dev.akif.as

type Validator[-A] = PartialFunction[A, String]
val Validator = PartialFunction

extension [A](v: Validator[A]) def and(next: Validator[A]): Validator[A] = v orElse next

def validate[A](predicate: A => Boolean, message: String): Validator[A] =
  case a: A if !predicate(a) => message
