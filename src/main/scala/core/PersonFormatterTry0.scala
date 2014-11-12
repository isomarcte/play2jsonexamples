/**
  *  Package(s)  :  core
  *  Description :  First attempt at making a Person formatter.
  *  License     :  BSD3
  *
  *  Maintainer  :  isomarcte@gmail.com
  *  Stability   :  unstable
  *  Portability :  portable
  *
  *  This file contains the first attempt at making a Person formatter.
  */
package core

// Global Imports //
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import play.api.libs.json.Json

import core.Util.isPrime

// Formatter //

/** Our first attempt at reading and writing [[Person]] values
  *
  * Issues:
  * - Quite verbose
  * - Not too helpful error messages.
  */
object PersonFormatterTry0 extends Format[Person] {

  // Member Values //

  private[this] val firstNameString = "firstName"
  private[this] val lastNameString = "lastName"
  private[this] val favoritePrimeString = "favoritePrime"

  // Abstract Method Implementation //

  /** @inheritdoc */
  override final def reads(json: JsValue): JsResult[Person] = {
    // Don't ever use .as !!!!
    val firstName: Option[String] = (json.\(firstNameString)).asOpt[String]
    val lastName: Option[String] = (json.\(lastNameString)).asOpt[String]
    val favoritePrime: Option[Int] = (json.\(favoritePrimeString)).asOpt[Int]

    firstName match {
      case Some(fn) if (fn.length > 0)=>
        lastName match {
          case Some(ln) if (ln.length > 0) =>
            favoritePrime match {
              case Some(num) if (isPrime(num)) =>
                JsSuccess(Person(fn, ln, num))
              case _ => JsError()
            }
          case _ =>
            JsError()
        }
      case _ =>
        JsError()
    }
  }

  /** @inheritdoc */
  override final def writes(p: Person): JsValue =
    Json.obj(
      (firstNameString -> p.firstName),
      (lastNameString -> p.lastName),
      (favoritePrimeString -> p.favoritePrime))

}
