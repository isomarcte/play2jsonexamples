/**
  *  Package(s)  :  core
  *  Description :  Third attempt at making a Person formatter.
  *  License     :  BSD3
  *
  *  Maintainer  :  isomarcte@gmail.com
  *  Stability   :  unstable
  *  Portability :  portable
  *
  *  This file contains the third attempt at making a Person formatter.
  */
package core

// Global Imports //
import play.api.libs.json.Format
import play.api.libs.json.JsPath
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.Reads
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax.toContraFunctorOps
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.unlift

import core.Util.isPrime


// Foramtter //

/** Our third attempt at defining a [[Person]] [[Format]] instance */
object PersonFormatterTry2 extends Format[Person] {

  /** The real formatter defined with combinator syntax */
  private[this] val formatter: Format[Person] = (
    (JsPath \ "firstName").format[String](
      Reads.minLength(1): Reads[String]) and
    (JsPath \ "lastName").format[String](
      Reads.minLength(1): Reads[String]) and
    (JsPath \ "favoritePrime").format[Int](Reads.min(0).
      filter(ValidationError("Number is not Prime!"))(isPrime(_)))
  )(Person.apply, unlift(Person.unapply))

  // Abstract Method Implementation //

  /** @inheritdoc */
  override final def reads(json: JsValue): JsResult[Person] =
    this.formatter.reads(json)

  /** @inheritdoc */
  override final def writes(p: Person): JsValue =
    this.formatter.writes(p)

}
