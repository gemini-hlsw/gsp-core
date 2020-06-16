// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.util.arb

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Cogen, Gen }
import org.scalacheck.Gen._
import gpp.util.EnumZipper
import gem.util.Enumerated

trait ArbEnumZipper {
  implicit def arbEnumZipper[A: Enumerated]: Arbitrary[EnumZipper[A]] =
    Arbitrary(const(EnumZipper.of[A]))

  implicit def enumZipperCogen[A]: Cogen[EnumZipper[A]] =
    Cogen[Unit].contramap(_ => ())
}

object ArbEnumZipper extends ArbEnumZipper
