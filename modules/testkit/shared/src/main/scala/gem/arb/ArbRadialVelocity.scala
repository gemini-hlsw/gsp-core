// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package arb

import coulomb._
import org.scalacheck.Arbitrary
import org.scalacheck.Cogen
import org.scalacheck.Gen

trait ArbRadialVelocity {

  implicit val arbRadialVelocity: Arbitrary[RadialVelocity] =
    Arbitrary {
      for {
        rv <-
          Gen.chooseNum(-1 * RadialVelocity.CValue.toDouble + 1, RadialVelocity.CValue.toDouble - 1)
      } yield RadialVelocity.unsafeFromRVQuantity(rv.withUnit[RadialVelocity.RVUnit])
    }

  implicit val cogRadialVelocity: Cogen[RadialVelocity] =
    Cogen[BigDecimal].contramap(_.rv.value)
}

object ArbRadialVelocity extends ArbRadialVelocity
