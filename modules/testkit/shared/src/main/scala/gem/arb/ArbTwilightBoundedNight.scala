// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package arb

import gsp.math.skycalc.TwilightBoundType
import gem.math.{ ObservingNight, TwilightBoundedNight }

import org.scalacheck._
import org.scalacheck.Arbitrary._

trait ArbTwilightBoundedNight {

  import ArbObservingNight._
  import gsp.math.arb.ArbTwilightBoundType._

  implicit val arbTwilightBoundedNight: Arbitrary[TwilightBoundedNight] =
    Arbitrary {
      for {
        b <- arbitrary[TwilightBoundType]
        n <- arbitrary[ObservingNight]
      } yield n.twilightBoundedUnsafe(b)
      // We use unsafe constructor since for our Sites there are twilight bounds for all nights and bound types.
    }

  implicit val cogTwilightBoundedNight: Cogen[TwilightBoundedNight] =
    Cogen[(TwilightBoundType, ObservingNight)].contramap(o => (o.boundType, o.toObservingNight))
}

object ArbTwilightBoundedNight extends ArbTwilightBoundedNight
