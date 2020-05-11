// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats.tests.CatsSuite
import cats.kernel.laws.discipline._
import gem.arb._

final class MagnitudeSpec extends CatsSuite {
  import ArbMagnitude._

  // Laws
  checkAll("Magnitude",         EqTests[Magnitude].eqv)
  checkAll("MagnitudeOrdering", OrderTests[Magnitude](Magnitude.MagnitudeOrdering).order)
}
