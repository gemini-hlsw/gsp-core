// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package data

import cats.kernel.Eq
import cats.implicits._
import gem.util.Enumerated

class EnumZipper[A] private (lefts: List[A], focus: A, rights: List[A])
  extends Zipper[A](lefts: List[A], focus: A, rights: List[A])
  with ZipperOps[A, EnumZipper[A]] {

  override def build(lefts: List[A], focus: A, rights: List[A]): EnumZipper[A] =
    EnumZipper.build(lefts, focus, rights)

  override def unmodified: EnumZipper[A] = this

  def withFocus(a: A)(implicit eq: Eq[A]): EnumZipper[A] =
    findFocus(_ === a)
      .getOrElse(unmodified) // This shouldn't happen. Zipper contains all elements.
}

object EnumZipper extends ZipperFactory[EnumZipper] {

  /**
    * Builds an EnumZipper from an Enumerated. The first element becomes the focus.
    */
  def of[A](implicit e: Enumerated[A]): EnumZipper[A] = 
    build(Nil, e.all.head, e.all.tail)

  override protected def build[A](lefts: List[A], focus: A, rights: List[A]): EnumZipper[A] = 
    new EnumZipper(lefts, focus, rights)

}
