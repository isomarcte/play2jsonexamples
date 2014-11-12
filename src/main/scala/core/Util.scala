/**
  *  Package(s)  :  core
  *  Description :  Utilities for this scala program
  *  License     :  BSD3
  *
  *  Maintainer  :  isomarcte@gmail.com
  *  Stability   :  unstable
  *  Portability :  portable
  *
  *  Utilities for this scala program
  */
package core

// Objects //

/** Utilities for this program */
object Util {

  /** Checks to see if a number is prime
    *
    * @param x the number to check for primality
    *
    * @return true if it is prime, false otherwise
    */
  def isPrime(x: Int): Boolean =
    x >= 2 && ((2 to (x-1)).filter(n => (x % n) == 0).length == 0)

}
