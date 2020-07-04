// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package arb

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Cogen }
import org.scalacheck.Gen._
import gem.arb.ArbEnumerated._
import gem.data.EnumZipper
import gem.util.Enumerated

trait ArbEnumZipper {
  implicit def arbEnumZipper[A: Enumerated]: Arbitrary[EnumZipper[A]] =
    Arbitrary(
      for {
        z <- const(EnumZipper.of[A])
        a <- arbitrary[A]
       } yield z.withFocus(a)
    )

  implicit def enumZipperCogen[A: Enumerated]: Cogen[EnumZipper[A]] =
    Cogen[A].contramap(_.focus)
}

object ArbEnumZipper extends ArbEnumZipper
