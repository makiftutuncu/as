# As

As is a no-macro, no-reflection, opinionated type refinement library for Scala. It is powered by [e](https://github.com/makiftutuncu/e) to handle invalid value errors.

## Installation
As is not published anywhere yet. So, feel free to include `As.scala` in your code and you're good to go.

## Overview

Assume we have following `Author` and `Book` types.

```scala
case class Author(id: Long, name: String)

case class Book(id: Long, authorId: Long, name: String)
```

`Long` and `String` are primitives and may not contain valid values for ids or names. Even if implement types for id and name to guard against invalid values, author's id is different than a book's id and they should not be of same type.

To overcome this, we can have separate **refined** types.

Here is how we can create one, refining `Long` as `AuthorId`:

1. Make a `sealed abstract case class`
2. Extend `Refined` with type of underlying value (`Long` in this case)
3. Mark the constructor as private
4. Create a companion object extending `UnderlyingType As RefinedType` (`Long As AuthorId` in this case)
5. Fill out the parameters and implement validation

```scala
// AuthorId.scala
sealed abstract case class AuthorId private(override val get: Long) extends Refined[Long]

object AuthorId extends (Long As AuthorId)(emptyValue = 0L, construct = new AuthorId(_) {}) {
   override val validate: PartialFunction[Long, String] = {
      case v if v <= 0 => "Id must be positive!"
   }
}
```

Things to note here:

1. Even though `AuthorId` type is a case class (having `equals`, `hashCode`, `toString`, `apply`, `unapply` methods generated)
   1. it is marked `sealed abstract` so it does not have generated `copy` method so a valid instance cannot be copied with an invalid value.
   2. its constructor is private, meaning that there is no way of creating a new concrete instance of this class outside the file it is defined.
   3. underlying value is called `get` (coming from `Refined`) so it makes sense semantically while accessing the value.
2. Companion object extends abstract class `Long As AuthorId` (which is a syntactic sugar for `As[Long, AuthorId]`) passing some values to its constructor, see [As.scala](src/main/scala/dev/akif/As.scala).
   1. First value is the empty value of this type (`0L` for the case of `Long`)
   2. Second value is a constructor lambda. Since concrete object creation is forbidden as explained above, we provide a way to create an anonymous instance.
3. Validate value is a partial function from the value to a `String` error message in case the value is invalid.

Let's create other types too.

```scala
// AuthorName.scala
sealed abstract case class AuthorName private(override val get: String) extends Refined[String]

object AuthorName extends (String As AuthorName)(emptyValue = "", construct = new AuthorName(_) {}) {
   override val validate: PartialFunction[String, String] = {
      case s if s.isBlank          => "Name must not be blank!"
      case s if s.trim.length > 64 => "Name must have less than 64 characters!"
   }
}

// BookId.scala
sealed abstract case class BookId private(override val get: Long) extends Refined[Long]

object BookId extends (Long As BookId)(emptyValue = 0L, construct = new BookId(_) {}) {
   override val validate: PartialFunction[Long, String] = {
      case v if v <= 0 => "Id must be positive!"
   }
}

// BookName.scala
sealed abstract case class BookName private(override val get: String) extends Refined[String]

object BookName extends (String As BookName)(emptyValue = "", construct = new BookName(_) {}) {
   override val validate: PartialFunction[String, String] = {
      case s if s.isBlank          => "Name must not be blank!"
      case s if s.trim.length > 64 => "Name must have less than 64 characters!"
   }
}
```

Now let's change the original example.

```scala
case class Author(id: AuthorId, name: AuthorName)

case class Book(id: BookId, authorId: AuthorId, name: BookName)
```

Now our types are fine-tuned so that they will have correct values and we are forced to check everything at compile time. We need to use `make` or `apply` methods to create a refined value. `make` is straightforward. However, because of how `apply` is defined, creating a refined type by applying an `A` to an `A As B` returns an `EOr[B]`. In other words, object construction can fail and this is reflected to types. Since we have `EOr`s in the construction, it's easy to combine them, even in for comprehensions.

```scala
import e.scala.EOr

// Failure({"name":"validation","message":"Id must be positive!","data":{"value":"-1"}})
val authorId1 = AuthorId(-1L)

// Success(AuthorId(1))
val authorId2 = AuthorId.make(1L)

// Success(Author(AuthorId(1)))
val maybeAuthor: EOr[Author] =
  for {
    id   <- AuthorId(1L)
    name <- AuthorName("Mehmet Akif Tütüncü")
  } yield {
    Author(id, name)
  }
```

In cases where you need an unwrapped value, you can use `makeOrEmpty` or `makeUnsafe` methods.

`makeOrEmpty` method will return you the empty value if validation fails.

`makeUnsafe` method will throw the validation error as an exception, hence the name unsafe.

```scala
// Book(BookId(0),AuthorId(0),BookName(Type Refinement in Scala))
val book: Book = Book(
  BookId.empty,
  AuthorId.makeOrEmpty(-1L),
  BookName.makeUnsafe("Type Refinement in Scala")
)
```

Accessing values is trivial, we are accessing the underlying value we defined in the case class constructor, conveniently named `get`.

```scala
// 0L
val bookId: Long = book.id.get

// "Type Refinement in Scala"
val bookName: String = book.name.get
```

## Contributing

All contributions are more than welcome. Please feel free to send a pull request for your contributions. Thank you.

## License

As is licensed with MIT License. See [LICENSE.md](LICENSE.md) for details.
