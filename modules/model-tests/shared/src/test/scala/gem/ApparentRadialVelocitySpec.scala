// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats.tests.CatsSuite
import cats.kernel.laws.discipline._

import coulomb._
import coulomb.si._
import coulomb.siprefix._
import gem.arb._
import java.math.MathContext

final class ApparentRadialVelocitySpec extends CatsSuite {
  import ArbApparentRadialVelocity._

  // Laws
  checkAll("ApparentRadialVelocity", EqTests[ApparentRadialVelocity].eqv)
  checkAll("ApparentRadialVelocityOrder", OrderTests[ApparentRadialVelocity].order)

  test("toRedshift") {
    assert(
      // Note the speed is given in Meter per second but coulomb will convert
      ApparentRadialVelocity(0.withUnit[Meter %/ Second]).toRedshift === Redshift.Zero
    )
    assert(ApparentRadialVelocity(RadialVelocity.C).toRedshift === Redshift(1))
    assert(
      ApparentRadialVelocity(
        BigDecimal.decimal(1000, MathContext.DECIMAL32).withUnit[(Kilo %* Meter) %/ Second]
      ).toRedshift ===
        Redshift(BigDecimal.decimal(0.003335641, MathContext.DECIMAL32))
    )
  }
}
