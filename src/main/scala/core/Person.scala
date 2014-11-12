/**
  *  Package(s)  :  core
  *  Description :  Contains class for making people.
  *  License     :  BSD3
  *
  *  Maintainer  :  isomarcte@gmail.com
  *  Stability   :  unstable
  *  Portability :  portable
  *
  *  This file contains a case class for making People.
  */
package core

// Global Imports //
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.JsPath
import play.api.libs.functional.syntax.toContraFunctorOps
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.unlift

// Classes //

/** Class that represents a person.
  *
  * @param firstName the first name of the person
  * @param lastName the last name of the person
  * @param favoritePrime the age of the person
  */
final case class
  Person(val firstName: String, val lastName: String, val favoritePrime: Int)

/** Companion object for the [[Person]] class
  *
  * If you are reading this code in order to be a better understanding of how
  * play-json works, you probably don't want to start here. Instead, look at
  * [[PersonFormatterTry0]] and the README.md. Come here later...
  */
object Person {

  // Json Methods //

  /** Make Json for a [[Person]]
    *
    * @param p the [[Person]] value to make into Json
    *
    * @return [[JsValue]] of said person
    */
  final def makePersonJson(p: Person): JsValue = {
    personFormatter.writes(p) // Same as Json.toJson! Demystified!
  }

  // Private Values //

  /** [[play.api.libs.json.Format]] instance for reading and writing
    * [[Person]] values.
    *
    * If you are looking through this code in order to help get a better
    * understanding of how Play JSON tools work, you should ignore this method
    * for now. A much better place to start is looking at the implementation
    * of [[PersonFormatterTry0]]
    */
  private[this] final implicit val personFormatter: Format[Person] = (
    (JsPath \ "firstName").format[String] and
    (JsPath \ "lastName").format[String] and
    (JsPath \ "favoritePrime").format[Int]
  )(Person.apply, unlift(Person.unapply))

}
