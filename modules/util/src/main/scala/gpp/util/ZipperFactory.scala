// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.util

import cats._
import cats.implicits._
import cats.data.NonEmptyList
import monocle.{ Lens, Prism, Traversal }

protected[util] trait ZipperFactory[Z[A] <: ZipperOps[A, Zipper[A]]] {

  protected def build[A](lefts: List[A], focus: A, rights: List[A]): Z[A]

  /**
    * @typeclass Eq
    */
  implicit def equal[A: Eq]: Eq[Z[A]] =
    Eq.instance { (a, b) =>
      a.focus === b.focus && a.lefts === b.lefts && a.rights === b.rights
    }

  def lefts[A]: Lens[Z[A], List[A]] = Lens[Z[A], List[A]](_.lefts)(v => z => build(v, z.focus, z.rights))
  def focus[A]: Lens[Z[A], A] = Lens[Z[A], A](_.focus)(v => z => build(z.lefts, v, z.rights))
  def rights[A]: Lens[Z[A], List[A]] = Lens[Z[A], List[A]](_.rights)(v => z => build(z.lefts, z.focus, v))
}
