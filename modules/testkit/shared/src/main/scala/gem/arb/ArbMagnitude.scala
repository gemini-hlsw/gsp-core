// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

package arb

import gem.enum.MagnitudeBand
import gem.enum.MagnitudeSystem
import gsp.math.arb.ArbMagnitudeValue._
import gsp.math.MagnitudeValue
import org.scalacheck._
import org.scalacheck.Arbitrary._
import org.scalacheck.Cogen._

trait ArbMagnitude {

  import ArbEnumerated._

  implicit val arbMagnitude: Arbitrary[Magnitude] =
    Arbitrary {
      for {
        v <- arbitrary[MagnitudeValue]
        b <- arbitrary[MagnitudeBand]
        e <- arbitrary[Option[MagnitudeValue]]
        s <- arbitrary[MagnitudeSystem]
      } yield Magnitude(v, b, e, s)
    }

  implicit val cogMagnitude: Cogen[Magnitude] =
    Cogen[(MagnitudeValue, MagnitudeBand, Option[MagnitudeValue], MagnitudeSystem)].contramap { u =>
      (u.value, u.band, u.error, u.system)
    }
}

object ArbMagnitude extends ArbMagnitude
