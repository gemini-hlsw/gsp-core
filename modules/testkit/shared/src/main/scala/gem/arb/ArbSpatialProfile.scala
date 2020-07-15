// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package arb

import gem.SpatialProfile
import gem.SpatialProfile._
import gsp.math.arb.ArbAngle._
import gsp.math.Angle
import org.scalacheck.{ Arbitrary, Cogen, Gen }
import org.scalacheck.Arbitrary._

trait ArbSpatialProfile {

  val genGaussianSource: Gen[GaussianSource] =
    for {
      fwhm <- arbitrary[Angle]
    } yield GaussianSource(fwhm)

  implicit val arbSpatialProfile: Arbitrary[SpatialProfile] =
    Arbitrary {
      Gen.oneOf(
        Gen.const(SpatialProfile.PointSource),
        Gen.const(SpatialProfile.UniformSource),
        genGaussianSource
      )
    }

  implicit val cogSpatialProfile: Cogen[SpatialProfile] =
    Cogen[Option[Option[Angle]]].contramap {
      case PointSource       => None
      case UniformSource     => Some(None)
      case GaussianSource(a) => Some(Some(a))
    }
}

object ArbSpatialProfile extends ArbSpatialProfile
