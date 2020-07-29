// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats.tests.CatsSuite
import cats.kernel.laws.discipline._

import coulomb._
import coulomb.si._
import coulomb.siprefix._
import gem.arb._

final class RadialVelocitySpec extends CatsSuite {
  import ArbRadialVelocity._

  // Laws
  checkAll("RadialVelocity", EqTests[RadialVelocity].eqv)
  checkAll("RadialVelocityOrder", OrderTests[RadialVelocity].order)

  test("toRedshift") {
    assert(
      // Note the speed is given in Meter per second but coulomb will convert
      RadialVelocity(0.withUnit[Meter %/ Second])
        .flatMap(_.toRedshift)
        .exists(_ === Redshift.Zero)
    )
    assert(
      RadialVelocity(1000.withUnit[(Kilo %* Meter) %/ Second])
        .flatMap(_.toRedshift)
        .exists(_ === Redshift(0.003341222805847144))
    )
  }
}
