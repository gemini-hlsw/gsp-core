// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.util

import cats._
import cats.implicits._
import cats.data.NonEmptyList
import monocle.{ Prism, Traversal }
import monocle.macros.Lenses
import monocle.macros.GenLens
import monocle.Lens

/**
  * Minimal zipper based on scalaz's implementation
  * This is only meant for small collections. performance has not been optimized
  */
protected[util] trait ZipperOps[A, +Z] {
  val lefts: List[A]
  val focus: A
  val rights: List[A]

  protected def build(lefts: List[A], focus: A, rights: List[A]): Z

  protected def unmodified: Z

  /**
    * Modify the focus
    */
  def modify(f: A => A): Z = build(lefts, f(focus), rights)

  /**
    * Modify the focus
    */
  def modifyP(p: Prism[A, A]): Z =
    p.getOption(focus).map(f => build(lefts, f, rights)).getOrElse(unmodified)

  /**
    * Find and element and focus if successful
    */
  def findFocusP(p: PartialFunction[A, Boolean]): Option[Z] =
    findFocus(p.lift.andThen(_.getOrElse(false)))

  /**
    * How many items are in the zipper
    */
  def length: Int = lefts.length + 1 + rights.length

  /**
    * Find and element and focus if successful
    */
  def findFocus(p: A => Boolean): Option[Z] =
    if (p(focus)) unmodified.some
    else {
      val indexLeft  = lefts.lastIndexWhere(p)
      val indexRight = rights.indexWhere(p)
      if (indexLeft === -1 && indexRight === -1)
        none
      else if (indexLeft >= 0)
        (lefts.splitAt(indexLeft) match {
          case (x, i :: l) =>
            build(l, i, (focus :: x).reverse ::: rights)
          case _           =>
            unmodified
        }).some
      else
        (rights.splitAt(indexRight) match {
          case (x, h :: t) =>
            build((focus :: x).reverse ::: lefts, h, t)
          case _           =>
            unmodified
        }).some
    }

  def exists(p: A => Boolean): Boolean =
    if (p(focus))
      true
    else
      lefts.exists(p) || rights.exists(p)

  def previous: Option[Z] =
    lefts match {
      case Nil    => none
      case h :: t => build(t, h, focus :: rights).some
    }

  def next: Option[Z] =
    rights match {
      case Nil       => none
      case h :: tail => build(focus :: lefts, h, tail).some
    }

  def find(p: A => Boolean): Option[A] =
    if (p(focus))
      focus.some
    else
      lefts.find(p).orElse(rights.find(p))

  def withFocus: Zipper[(A, Boolean)] =
    new Zipper(lefts.map((_, false)), (focus, true), rights.map((_, false)))

  def toList: List[A] = lefts.reverse ::: (focus :: rights)

  def toNel: NonEmptyList[A] = NonEmptyList.fromListUnsafe(toList)
}

class Zipper[A](val lefts: List[A], val focus: A, val rights: List[A])
  extends ZipperOps[A, Zipper[A]] {

  override def build(lefts: List[A], focus: A, rights: List[A]): Zipper[A] =
    Zipper.build(lefts, focus, rights)

  override def unmodified: Zipper[A] = this
}

object Zipper extends ZipperFactory[Zipper] {

  def apply[A](lefts: List[A], focus: A, rights: List[A]): Zipper[A] = 
    new Zipper(lefts, focus, rights)

  /**
    * Builds a Zipper from NonEmptyList. The head of the list becomes the focus
    */
  def fromNel[A](ne: NonEmptyList[A]): Zipper[A] =
    apply(Nil, ne.head, ne.tail)

  /**
    * Builds a Zipper from elements. The first element becomes the focus
    */
  def of[A](a: A, as: A*): Zipper[A] =
    apply(Nil, a, as.toList)

  protected def build[A](lefts: List[A], focus: A, rights: List[A]): Zipper[A] =
    apply(lefts, focus, rights)
}
