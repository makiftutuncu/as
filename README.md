# as [![](https://img.shields.io/badge/scaladoc-1.0.1-brightgreen.svg?style=for-the-badge&logo=scala&color=dc322f&labelColor=333333)](https://javadoc.io/doc/dev.akif/as_3)

as is a no-macro, no-reflection, opinionated type refinement library for Scala 3. It is powered by [e](https://github.com/makiftutuncu/e) to handle invalid value errors.

| Latest Version | Java Version | Scala Version |
|----------------|--------------|---------------|
| 1.0.1          | 21           | 3.4.2         |

## Table of Contents

1. [Installation](#installation)
2. [How to Use](#how-to-use)
3. [Development and Testing](#development-and-testing)
4. [Releases](#releases)
5. [Contributing](#contributing)
6. [License](#license)

## Installation
If you use SBT, add following to your `build.sbt`:

```scala 3
libraryDependencies += "dev.akif" %% "as" % "1.0.1"
```

If you use Maven, add following to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>dev.akif</groupId>
        <artifactId>as_3</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

If you use Gradle, add following to your project's `build.gradle`:

```javascript
dependencies
{
    implementation('dev.akif:as_3:1.0.1')
}
```

## How to Use

Assume we have following `Author` and `Book` types.

```scala 3
case class Author(id: Long, name: String)

case class Book(id: Long, authorId: Long, name: String)
```

`Long` and `String` are primitives and may not contain valid values for ids or names. Even if you implement ids and names with their own types to guard against invalid values, author's id is different from a book's id, and they should not be of same type.

To overcome this, we can have separate **refined** types.

Here is how you can create one, refining `Long` as `AuthorId`:

1. Make an `opaque type` alias `AuthorId = Long` in a separate file
   1. Optionally add a subtype constraint `<: Long` if you want to be able to make `AuthorId` type a subtype of `Long` (in other words, to be able to assign an `AuthorId` value to a `Long` reference)
2. Create a companion object extending `UnderlyingType as RefinedType` (`Long as AuthorId` in this case)
3. Implement `undefinedValue` and `validation` members

```scala 3
// AuthorId.scala
import dev.akif.as.*

opaque type AuthorId <: Long = Long

// Parenthesis are required because `as` is used as an infix type, which would otherwise be `as[Long, AuthorId]`
object AuthorId extends (Long as AuthorId):
    override val undefinedValue: Long = 0L
   
    override val validation: Validation[Long] =
        Validation.min(lowerLimit = 0L, inclusive = false)
```

This incurs no additional allocation because `AuthorId` is defined as an `opaque type` alias. Within `AuthorId.scala` file, it can be treated as a `Long`. However, in any other file, `AuthorId` and `Long` are completely different types.

Let's create other types too.

```scala 3
// AuthorName.scala
import dev.akif.as.*

opaque type AuthorName <: String = String

object AuthorName extends (String as AuthorName):
    override val undefinedValue: String = ""

    override val validation: Validation[String] =
       Validation.all(
          Validation.make(predicate = !_.isBlank, failureMessage = _ => "Value is blank"),
          Validation.make(predicate = _.length <= 64, failureMessage = _ => "Value has more than 64 characters")
       )

// BookId.scala
import dev.akif.as.*

opaque type BookId <: Long = Long

object BookId extends (Long as BookId):
    override val undefinedValue: Long = 0L
   
    override val validation: Validation[Long] =
        Validation.min(lowerLimit = 0L, inclusive = false)

// BookName.scala
import dev.akif.as.*

opaque type AuthorName <: String = String

object AuthorName extends (String as AuthorName):
    override val undefinedValue: String = ""

    override val validation: Validation[String] =
        Validation.all(
            Validation.make(predicate = !_.isBlank, failureMessage = _ => "Value is blank"),
            Validation.make(predicate = _.length <= 64, failureMessage = _ => "Value has more than 64 characters")
        )
```

Now let's change the original example.

```scala 3
case class Book(id: AuthorId, name: AuthorName)

case class Book(id: BookId, authorId: AuthorId, name: BookName)
```

Now our types are fine-tuned so that they will have correct values, and we are forced to check everything at compile time. We need to use `apply` method, `as` extension method or their variants to create a refined value. Creating a refined type by applying an `A` to an `A as B` returns an `E or B`. In other words, object construction can fail and this is reflected to types. Since we have `or`s in the construction, it's easy to combine them, even in for comprehensions.

```scala 3
import dev.akif.as.*
import e.scala.*

// Failure({"name":"invalid-data","causes":[{"message":"Value is less than or equal to 0"}],"data":{"type":"AuthorId","value":"-1"}})
val authorId1: E or AuthorId = AuthorId(-1L)

// Success(1)
val authorId2: E or AuthorId = AuthorId(1L)

// Can also be written with the extension method
// val authorId2: E or AuthorId = 1L.as[AuthorId]

// Success(Author(1, Mehmet Akif Tütüncü))
val maybeAuthor: E or Author =
    for
       id   <- AuthorId(1L)
       name <- AuthorName("Mehmet Akif Tütüncü")
    yield
        Author(id, name)
```

In cases where you need to have the refined `AuthorId` directly instead of `E or AuthorId`, you can use:

* `AuthorId.applyOrUndefined(-1L)` (or `-1L.asOrUndefined[AuthorId]`) method that will return you the undefined value when validation fails
* `AuthorId.unsafe(-1L)` (or `-1L.asUnsafe[AuthorId]`) method that will throw the validation error as an exception, hence the name unsafe

```scala 3
// Book(0, 0, Type Refinement in Scala)
val book: Book = Book(
    BookId.undefined,
    AuthorId.applyOrEmpty(-1L),
    BookName.unsafe("Type Refinement in Scala")
)
```

Accessing the unrefined value is possible via the conveniently named extension method `value`. If defined as a subtype, one can also directly assign a refined value to a reference of the unrefined type.

```scala
// 0L
val bookId: Long = book.id.value

// "Type Refinement in Scala"
val bookName: String = book.name // since BookName alias is defined as a subtype of String
```

## Development and Testing

as is built with SBT. You can use `clean`, `compile`, `test` tasks for development and testing.

## Releases

as packages are published to Maven Central, and they are versioned according to [semantic versioning](https://semver.org). Release process is managed by [sbt-release](https://github.com/sbt/sbt-release).

## Contributing

All contributions are welcome, including requests to feature your project utilizing as. Please feel free to send a pull request. Thank you.

## License

as is licensed with [MIT License](LICENSE.md).
