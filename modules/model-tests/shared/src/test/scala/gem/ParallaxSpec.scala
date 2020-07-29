// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats.tests.CatsSuite
import cats.kernel.laws.discipline._
import monocle.law.discipline._

import gem.arb._
import gsp.math.arb.ArbAngle._
import gsp.math.laws.discipline.SplitMonoTests

final class ParallaxSpec extends CatsSuite {
  import ArbParallax._

  // Laws
  checkAll("Parallax", EqTests[Parallax].eqv)
  checkAll("Parallax.angle", IsoTests(Parallax.angle))
  checkAll("Parallax.signedMicroarcseconds",
           SplitMonoTests(Parallax.signedMilliarcseconds).splitMono
  )
}
