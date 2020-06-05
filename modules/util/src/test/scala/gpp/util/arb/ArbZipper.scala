// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.util.arb

import cats.data.NonEmptyList
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Cogen, Gen }
import gpp.util.Zipper

trait ArbZipper {

  implicit def arbZipper[A: Arbitrary]: Arbitrary[Zipper[A]] =
    Arbitrary {
      val maxSize = 100
      for {
        h <- arbitrary[A]
        l <- Gen.choose(0, maxSize)
        d <- Gen.listOfN(l, arbitrary[A])
      } yield Zipper.fromNel(NonEmptyList.of(h, d: _*))
    }

  implicit def zipperCogen[A: Cogen]: Cogen[Zipper[A]] =
    Cogen[List[A]].contramap(_.toList)

}

object ArbZipper extends ArbZipper
