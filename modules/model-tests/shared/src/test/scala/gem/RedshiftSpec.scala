// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats.tests.CatsSuite
import cats.kernel.laws.discipline._

import coulomb._
import coulomb.si._
import gem.arb._

final class RedshiftSpec extends CatsSuite {
  import ArbRedshift._

  // Laws
  checkAll("Redshift", EqTests[Redshift].eqv)
  checkAll("RedshiftOrder", OrderTests[Redshift].order)

  test("fromVelocity") {
    assert(Redshift.Zero.some === Redshift.fromVelocity(0.withUnit[Meter %/ Second]))
    assert(Redshift.fromVelocity(Redshift.C).isEmpty)
    assert(
      Redshift(3.335641007851109e-8).some === Redshift.fromVelocity(10.withUnit[Meter %/ Second])
    )
  }
}
