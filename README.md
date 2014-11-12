# Play JSON Examples #

## Introduction ##

This repo attempts to show some basic examples of working with the
[play-json][playjson]. The [Play][Play] library is generally quite a
nice easy to use JSON library, however it can sometimes be a bit
*magical* to first time users of the library. This confusion may arise
from the fact that simplest way to work with the library is through
the use of the [combinators][combinators] which somewhat mask what is
really happening under to hood.

Hopefully these examples, which are by no means exhaustive, help shed
some light on how these very useful tools work. I want to be clear,
you should probably read through the [play-json][playjson]
documentation first. It is quite good and well written. These examples
were of some use in helping some colleagues through their confusion on
some parts of the [play-json][playjson] API, so I thought I would put
them up here so that they may be of help to other people as well.

If you find anything unclear, or any errors, please create an issue
and I will attempt to address it as soon as possible!

## Getting Started ##

The first thing you should do is read through plays documentation on
JSON. If you have found your way here and things are still a bit
confusing, then you should *read this README*. Then you should *read
the example code*.

This example repo assumes that you will be generally testing things
out via the `sbt` and the scala interpreter.

To try out the examples you need to have `sbt` installed. For
information on how to install it, click here -> [sbt][sbt].

Assuming that you have `sbt` installed, you can get started playing with
the examples by running his command in the top level folder.

    $ sbt console
    scala>

Or alternatively,

    $ sbt
    > console
    scala>

This will drop you to a scala interpreter with all of the project
resources loaded. You can begin playing with one of the included
examples by importing them from the interpreter.

    scala> import core._

Now we can get started. `core` provides a useful, but trivial,
`Person` `case class` which we can use to test. A `Person` is defined
as having three attributes, a first name, a last name, and a favorite
prime number. Why a favorite prime number you ask? Well, it turns out
one of the bits of [play-json][playjson] which seems to most confuse
people in my experience is how do I perform non-trivial filtering when
reading JSON values into a scala literal object. Examples of this are
things like validating a date is of [ISO 8601][ISO8601] format, and
not simply a valid string. Prime numbers are a simpler case, we don't
simply want to be sure that the JSON has valid numbers (the simple
case) but that those numbers are also valid primes.

Anyway, back to the task at hand. Let's make some people!

    scala> val john = Person("John", "Smith", 1)
    john: core.Person = Person(John,Smith,1)

    scala>

The mathematically inclined of you should immediately note that I made
a `Person` value with the number `1` as the favorite prime, but `1` is
not a prime number. The reason for that is that the `case class`
`Person` performs no validation that the number is actually prime. We
of course could do this, but we don't here, because we want to be able
to easily create invalid JSON values and see how our various versions
of Play JSON reading and writing handles these cases.

In a similar vein, the companion object for `Person` defines a useful
method for `makePersonJson` which takes a `Person` value and writes it
as a JSON value (with no validation). This allows us to create
`JsValue` values from `Person` in order to test various implementations
of reading JSON. I encourage you *not* to look inside the `Person`
class first to see how this is defined, but to instead look at the
provided examples, starting with PersonFormatterTry0, in turn to get
an idea of how all of this works. Anyway, lets see that
`makePersonJson` method in action.

    scala> Person.makePersonJson(john)
    res3: play.api.libs.json.JsValue = {"firstName":"John","lastName":"Smith","favoritePrime":1}

    scala>

Cool! Now you are read to actually start playing with the real JSON
reading and writing tools in this example.

## Play `Reads` and `Writes` ##

Okay, so in order to read or write JSON values to and from native
scala values in with [play-json][playjson] you need to have an
instance of [`Reads[T]`][play.api.libs.jsonReads] and
[`Writes[T]`][play.api.libs.json.Writes] for your given type `T`, or
in our case `Person`. These traits are *much* simpler than they may
appear. They define some utility methods for working with JSON and
each define an single abstract method that you must implement in your
subclass. Here are the signatures,

```scala
trait Reads[T] {

    abstract def reads(json: JsValue): JsResult[T]

}

trait Writes[T] {

    abstract def writes(o: T): JsValue

}
```

A `reads` method takes any `JsValue` (which is just a JSON type) and
attempts to make it into a full scala values (like `Person`!). The
`JsResult[T]` is very similar to `Either` or `Try` from the standard
library. It can either return a `JsSuccess` which just wraps the
deserailized value, or a `JsError` in case the JSON did not map to the
real object correctly. See the play documentation for working with
`JsResult`. `writes` is the reverse of `reads` it takes a object of
the given type and creates a `JsValue`, which always succeeds.

There is another type worth mentioning, `Format`. This trait mixes in
both `Reads` and `Writes`, which allows you to sometimes define
instances of `reads` and `writes` at the same time thus saving you
code. This is the easiest and most preferred way to work with JSON,
though it is not always expressive enough under certain
conditions...but don't worry about that for now. If/when you come to
an instance where you can't use `Format` you will probably know enough
about JSON with Play to know what is going on.

Cool! Now this example library provides some various instances of
these `Reads` and `Writes` for `Person` in terms of `Format`
values. Each of the examples is different, and attempts to illustrate
better usage of the [play-json][playjson] library. Let's take a look
at the first one.

## PersonFormatterTry0 ##

Assuming you are starting from a fresh sbt console, here is how to get
the first example up and running.

First we import the example library code here,

    scala> import core._

Now we make an `implicit` val for the `PersonFormatterTry0`. This is
the normal way that [play-json][playjson] works with values, through a
`implicit` `Reads`, `Writes`, or `Format` in scope. Under the hood,
the `implicit` is used to call `reads` or `writes` on the give
value. If we don't define this `implicit` the `Json.toJson` and
`validate` methods used later won't work.

    scala> implicit val format0 = PersonFormatterTry0
    format0: core.PersonFormatterTry0.type = core.PersonFormatterTry0$@2699ba10

Now we need to make a person, we'll make the same invalid `john`
person from earlier. Recall that the third field of a person is their
favorite prime number, and thus `john` is actually an invalid person
because `1` is not prime.

    scala> val john = Person("John", "Smith", 1)
    john: core.Person = Person(John,Smith,1)

Now we import the required play libraries.

    scala> import play.api.libs.json.Json

Now let's use `Json.toJson` to serialize `john` as JSON.

    scala> Json.toJson(john)
    res0: play.api.libs.json.JsValue = {"firstName":"John","lastName":"Smith","favoritePrime":1}

Cool! That worked! Now let's try to read him back into a `Person`
value. We do this with the `validate` method on a `JsValue`
value. `validate` is parameterized by the type we wish to read the
`JsValue` as, in this case `Person`.

    scala> Json.toJson(john).validate[Person]
    res1: play.api.libs.json.JsResult[core.Person] = JsError(List((,List())))

In this case it gives us a `JsError` as the result, with a not too
helpful `List((,List()))` as the error value (don't worry we'll do
better on the other tries!). The reason it did this is because the
`reads` instance in `PersonFormatterTry0` checks to be sure that the
`favoritePrime` number is valid, and if it is not it rejects that
read. So let's try again with a valid `Person`.

    scala> val larry = Person("Larry", "Smith", 2)
    larry: core.Person = Person(Larry,Smith,2)

    scala> Json.toJson(larry).validate[Person]
    res2: play.api.libs.json.JsResult[core.Person] = JsSuccess(Person(Larry,Smith,2),)

That worked as expected!

Now I would encourage you to go and look at the code for
`PersonFormatterTry0` and then try `PersonFormatterTry1` and
`PersonFormatterTry2` just as we did with `PersonFormatterTry0`. The
comments in the code attempt to explain what is going on with each of
the examples. Of particular note, `PersonFormatterTry1` and
`PersonFormatterTry2` use the JSON [combinator][combinators] syntax,
which has several advantages for general use.


[playjson]: https://www.playframework.com/documentation/2.3.x/ScalaJson "Play JSON"

[Play]: https://www.playframework.com/ "Play"

[combinators]: https://www.playframework.com/documentation/2.3.x/ScalaJsonCombinators "Play Combinators"

[sbt]: http://www.scala-sbt.org/download "sbt"

[ISO8601]: http://en.wikipedia.org/wiki/ISO_8601 "ISO 8601 Wikipedia"

[reads]: https://www.playframework.com/documentation/2.3.x/api/scala/index.html#play.api.libs.json.Reads "Reads"

[writes]: https://www.playframework.com/documentation/2.3.x/api/scala/index.html#play.api.libs.json.Reads "writes"
