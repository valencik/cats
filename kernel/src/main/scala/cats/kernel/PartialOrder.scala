/*
 * Copyright (c) 2015 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cats.kernel

import java.lang.Double.isNaN
import scala.{specialized => sp}
import compat.scalaVersionSpecific.*

/**
 * The `PartialOrder` type class is used to define a partial ordering on some type `A`.
 *
 * A partial order is defined by a relation <=, which obeys the following laws:
 *
 *   - x <= x (reflexivity)
 *   - if x <= y and y <= x, then x = y (anti-symmetry)
 *   - if x <= y and y <= z, then x <= z (transitivity)
 *
 * To compute both <= and >= at the same time, we use a Double number
 * to encode the result of the comparisons x <= y and x >= y.
 * The truth table is defined as follows:
 *
 * | x <= y | x >= y | result | note |
 * | :--    | :--    | --:    | :-- |
 * | true   |true    | 0.0    | (corresponds to x = y) |
 * | false  |false   | NaN    | (x and y cannot be compared) |
 * | true   |false   | -1.0   | (corresponds to x < y) |
 * | false  |true    | 1.0    | (corresponds to x > y) |
 */
trait PartialOrder[@sp A] extends Any with Eq[A] { self =>

  /**
   * Result of comparing `x` with `y`. Returns NaN if operands are not
   * comparable. If operands are comparable, returns a Double whose
   * sign is:
   *
   *   - negative iff `x < y`
   *   - zero     iff `x = y`
   *   - positive iff `x > y`
   */
  def partialCompare(x: A, y: A): Double

  /**
   * Like `partialCompare`, but returns a [[cats.kernel.Comparison]] instead of an Double.
   * Has the benefit of being able to pattern match on, but not as performant.
   */
  def partialComparison(x: A, y: A): Option[Comparison] =
    Comparison.fromDouble(partialCompare(x, y))

  /**
   * Result of comparing `x` with `y`. Returns None if operands are
   * not comparable. If operands are comparable, returns Some[Int]
   * where the Int sign is:
   *
   *   - negative iff `x < y`
   *   - zero     iff `x = y`
   *   - positive iff `x > y`
   */
  def tryCompare(x: A, y: A): Option[Int] = {
    val c = partialCompare(x, y).sign
    if (isNaN(c)) None else Some(c.toInt)
  }

  /**
   * Returns Some(x) if x <= y, Some(y) if x > y, otherwise None.
   */
  def pmin(x: A, y: A): Option[A] = {
    val c = partialCompare(x, y)
    if (c <= 0) Some(x)
    else if (c > 0) Some(y)
    else None
  }

  /**
   * Returns Some(x) if x >= y, Some(y) if x < y, otherwise None.
   */
  def pmax(x: A, y: A): Option[A] = {
    val c = partialCompare(x, y)
    if (c >= 0) Some(x)
    else if (c < 0) Some(y)
    else None
  }

  // The following may be overridden for performance:

  /**
   * Returns true if `x` = `y`, false otherwise.
   */
  def eqv(x: A, y: A): Boolean = partialCompare(x, y) == 0

  /**
   * Returns true if `x` <= `y`, false otherwise.
   */
  def lteqv(x: A, y: A): Boolean = partialCompare(x, y) <= 0

  /**
   * Returns true if `x` < `y`, false otherwise.
   */
  def lt(x: A, y: A): Boolean = partialCompare(x, y) < 0

  /**
   * Returns true if `x` >= `y`, false otherwise.
   */
  def gteqv(x: A, y: A): Boolean = partialCompare(x, y) >= 0

  /**
   * Returns true if `x` > `y`, false otherwise.
   */
  def gt(x: A, y: A): Boolean = partialCompare(x, y) > 0
}

abstract class PartialOrderFunctions[P[T] <: PartialOrder[T]] extends EqFunctions[P] {

  def partialCompare[@sp A](x: A, y: A)(implicit ev: P[A]): Double =
    ev.partialCompare(x, y)
  def tryCompare[@sp A](x: A, y: A)(implicit ev: P[A]): Option[Int] =
    ev.tryCompare(x, y)

  def pmin[@sp A](x: A, y: A)(implicit ev: P[A]): Option[A] =
    ev.pmin(x, y)
  def pmax[@sp A](x: A, y: A)(implicit ev: P[A]): Option[A] =
    ev.pmax(x, y)

  def lteqv[@sp A](x: A, y: A)(implicit ev: P[A]): Boolean =
    ev.lteqv(x, y)
  def lt[@sp A](x: A, y: A)(implicit ev: P[A]): Boolean =
    ev.lt(x, y)
  def gteqv[@sp A](x: A, y: A)(implicit ev: P[A]): Boolean =
    ev.gteqv(x, y)
  def gt[@sp A](x: A, y: A)(implicit ev: P[A]): Boolean =
    ev.gt(x, y)
}

@suppressUnusedImportWarningForScalaVersionSpecific
object PartialOrder extends PartialOrderFunctions[PartialOrder] with PartialOrderToPartialOrderingConversion {

  /**
   * Access an implicit `PartialOrder[A]`.
   */
  @inline final def apply[A](implicit ev: PartialOrder[A]): PartialOrder[A] = ev

  /**
   * Convert an implicit `PartialOrder[B]` to an `PartialOrder[A]` using the given
   * function `f`.
   */
  def by[@sp A, @sp B](f: A => B)(implicit ev: PartialOrder[B]): PartialOrder[A] =
    (x, y) => ev.partialCompare(f(x), f(y))

  /**
   * Defines a partial order on `A` from p where all arrows switch direction.
   */
  def reverse[@sp A](p: PartialOrder[A]): PartialOrder[A] =
    (x, y) => p.partialCompare(y, x)

  /**
   * Define a `PartialOrder[A]` using the given function `f`.
   */
  def from[@sp A](f: (A, A) => Double): PartialOrder[A] = f(_, _)

  def fromPartialOrdering[A](implicit ev: PartialOrdering[A]): PartialOrder[A] =
    ev.tryCompare(_, _).fold(Double.NaN)(_.toDouble)
}

trait PartialOrderToPartialOrderingConversion {
  implicit def catsKernelPartialOrderingForPartialOrder[A](implicit ev: PartialOrder[A]): PartialOrdering[A] =
    new PartialOrdering[A] {
      def tryCompare(x: A, y: A): Option[Int] = ev.tryCompare(x, y)
      def lteq(x: A, y: A): Boolean = ev.lteqv(x, y)
    }
}
