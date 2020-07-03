// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package arb

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Cogen, Gen }
import gem.data.Zipper

trait ArbZipper {
  implicit def arbZipper[A: Arbitrary]: Arbitrary[Zipper[A]] =
    Arbitrary {
      val MaxSideLength = 100

      for {
        f <- arbitrary[A]
        ll <- Gen.choose(0, MaxSideLength)
        l <- Gen.listOfN(ll, arbitrary[A])
        rl <- Gen.choose(0, MaxSideLength)
        r <- Gen.listOfN(rl, arbitrary[A])
      } yield Zipper(l, f, r)
    }

  implicit def zipperCogen[A: Cogen]: Cogen[Zipper[A]] =
    Cogen[(List[A], A, List[A])].contramap(z => (z.lefts, z.focus, z.rights))

}

object ArbZipper extends ArbZipper
