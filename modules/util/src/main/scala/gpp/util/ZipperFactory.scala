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

  /**
    * @typeclass Traverse
    * Based on traverse implementation for List
    */
  implicit val traverse: Traverse[Z] = new Traverse[Z] {
    override def traverse[G[_], A, B](
      fa: Z[A]
    )(f:  A => G[B])(implicit G: Applicative[G]): G[Z[B]] =
      (fa.lefts.traverse(f), f(fa.focus), fa.rights.traverse(f)).mapN {
        case (l, f, r) => build(l, f, r)
      }

    override def foldLeft[A, B](fa: Z[A], b: B)(f: (B, A) => B): B =
      fa.toNel.foldLeft(b)(f)

    override def foldRight[A, B](fa: Z[A], lb: Eval[B])(
      f:                             (A, Eval[B]) => Eval[B]
    ): Eval[B] = {
      def loop(as: Vector[A]): Eval[B] =
        as match {
          case h +: t => f(h, Eval.defer(loop(t)))
          case _      => lb
        }

      Eval.defer(loop(fa.toList.toVector))
    }
  }

  def lefts[A]: Lens[Z[A], List[A]] = Lens[Z[A], List[A]](_.lefts)(v => z => build(v, z.focus, z.rights))
  def focus[A]: Lens[Z[A], A] = Lens[Z[A], A](_.focus)(v => z => build(z.lefts, v, z.rights))
  def rights[A]: Lens[Z[A], List[A]] = Lens[Z[A], List[A]](_.rights)(v => z => build(z.lefts, z.focus, v))

  /**
    * Creates a monocle Traversal for the Zipper
    */
  def zipperT[A]: Traversal[Z[A], A] =
    Traversal.fromTraverse

  /**
    * Traversal filtered zipper, Note this is unsafe as the predicate breaks some laws
    */
  def unsafeSelect[A](predicate: A => Boolean): Traversal[Z[A], A] =
    new Traversal[Z[A], A] {
      override def modifyF[F[_]: Applicative](f: A => F[A])(s: Z[A]): F[Z[A]] = {
        val lefts: F[List[A]]  = s.lefts.traverse {
          case x if predicate(x) => f(x)
          case x                 => x.pure[F]
        }
        val rights: F[List[A]] = s.rights.traverse {
          case x if predicate(x) => f(x)
          case x                 => x.pure[F]
        }
        val focus: F[A]        =
          if (predicate(s.focus)) f(s.focus) else s.focus.pure[F]
        (lefts, focus, rights).mapN { (l, f, r) =>
          build(l, f, r)
        }
      }
    }
}
