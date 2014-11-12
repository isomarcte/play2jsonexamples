/**
  *  Package(s)  :  core
  *  Description :  second attempt at making a Person formatter.
  *  License     :  BSD3
  *
  *  Maintainer  :  isomarcte@gmail.com
  *  Stability   :  unstable
  *  Portability :  portable
  *
  *  This file contains the second attempt at making a Person formatter.
  */
package core

// Global Imports //
import play.api.libs.json.Format
import play.api.libs.json.JsPath
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.Reads
import play.api.libs.functional.syntax.toContraFunctorOps
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.unlift


// Foramtter //

/** Our second attempt at defining a [[Person]] [[play.api.libs.json.Format]]
  * instance
  *
  * It actually just composes another [[play.api.libs.json.Format]] instance.
  *
  * Issues
  * - No longer handles primes!
  *
  * Benefits over Try0
  * - Handles error paths better
  * - reads and writes are more succinct
  */
object PersonFormatterTry1 extends Format[Person] {

  // scalastyle:off line.size.limit
  /** The real formatter defined with combinator syntax
    *
    * @see https://www.playframework.com/documentation/2.3.x/ScalaJsonCombinators
    */
  // scalastyle:on line.size.limit
  private[this] val formatter: Format[Person] = (
    (JsPath \ "firstName").format[String](
      Reads.minLength(1): Reads[String]) and
    (JsPath \ "lastName").format[String](
      Reads.minLength(1): Reads[String]) and
    (JsPath \ "favoritePrime").format[Int](Reads.min(0))
  )(Person.apply, unlift(Person.unapply))

  // Abstract Method Implementation //

  /** @inheritdoc */
  override final def reads(json: JsValue): JsResult[Person] =
    this.formatter.reads(json)

  /** @inheritdoc */
  override final def writes(p: Person): JsValue =
    this.formatter.writes(p)

}
