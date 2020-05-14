# As

No-macro, no-reflection, opinionated type refinement for Scala, powered by [e](https://github.com/makiftutuncu/e)

## Overview

Assume we have following `Author` and `Book` types.

```scala
case class Author(id: Long, name: String)

case class Book(id: Long, authorId: Long, name: String)
```

`Long` and `String` are primitives and may not contain valid values for ids or names. Even if implement types for id and name to guard against invalid values, author's id is different than a book's id and they should not be of same type.

To overcome this, we can have separate **refined** types. Here is how we can create one.

```scala
import dev.akif.As

// AuthorId.scala

sealed abstract case class AuthorId private(get: Long)

object AuthorId extends (Long As AuthorId)(0L, new AuthorId(_) {}, {
  case v: Long if v <= 0 => "Id must be positive!"
})
```

Things to note here

1. Even though `AuthorId` type is a case class (with all its goodies)
   1. it is marked `sealed abstract` so it does not have generated `copy` method so a valid instance cannot be copied with an invalid value.
   2. its constructor is private, meaning that there is no way of creating a new concrete instance of this class outside the file it is defined.
2. Companion object extends abstract class `Long As AuthorId` (which is a syntactic sugar for `As[Long, AuthorId]`) passing some values to its constructor, see [As.scala](src/main/scala/dev/akif/As.scala).
   1. First value is the empty value of this type (`0L` for the case of `Long`)
   2. Second value is a constructor lambda. Since concrete object creation is forbidden as explained above, we provide a way to create an anonymous instance.
   3. Third value is a partial function from the value to a `String` error message in case the value is invalid.
3. We call the underlying value `get` so it makes sense semantically while accessing the value.

Let's create other types too.

```scala
import dev.akif.As

// AuthorName.scala

sealed abstract case class AuthorName private(get: String)

object AuthorName extends (String As AuthorName)("", new AuthorName(_) {}, {
  case s: String if s.isBlank          => "Name must not be blank!"
  case s: String if s.trim.length > 64 => s"Name must have less than 64 characters!"
})

// BookId.scala

sealed abstract case class BookId private(get: Long)

object BookId extends (Long As BookId)(0L, new BookId(_) {}, {
  case v: Long if v <= 0 => "Id must be positive!"
})

// BookName.scala

sealed abstract case class BookName private(get: String)

object BookName extends (String As BookName)("", new BookName(_) {}, {
  case s: String if s.isBlank          => "Name must not be blank!"
  case s: String if s.trim.length > 64 => s"Name must have less than 64 characters!"
})
```

Now let's change the original example.

```scala
case class Author(id: AuthorId, name: AuthorName)

case class Book(id: BookId, authorId: AuthorId, name: BookName)
```

Now our types are fine-tuned so that they will have correct values and we are forced to check everything at compile time.

```scala
import dev.akif.As
import e.scala.E

// Left({"name":"validation","message":"Id must be positive!","data":{"value":"-1"}})
val authorId1 = AuthorId(-1L)

// Right(AuthorId(1))
val authorId2 = AuthorId(1L)
```

Applying an `A` to an `A As B` returns an `Either[E, B]`. In other words, object construction can fail and this is reflected to types. Since we have `Either`s, it's easy to combine them.

```scala
import e.scala.E

// Right(Author(AuthorId(1)))
val maybeAuthor: Either[E, Author] =
  for {
    id   <- AuthorId(1L)
    name <- AuthorName("Mehmet Akif Tütüncü")
  } yield {
    Author(id, name)
  }
```

If using the default value in case of an invalid one is OK, we can do following to get an instance directly, ignoring the error.

```scala
// Book(BookId(0),AuthorId(1),BookName(Type Refinement in Scala))
val book: Book = Book(
  BookId.getOrEmpty(-1),
  AuthorId.getOrEmpty(1L),
  BookName.getOrEmpty("Type Refinement in Scala")
)
```

Accessing values is trivial.

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
