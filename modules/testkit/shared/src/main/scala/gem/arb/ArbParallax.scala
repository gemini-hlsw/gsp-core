// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package arb

import gem.Parallax
import org.scalacheck.{ Arbitrary, Cogen }
import org.scalacheck.Arbitrary._

trait ArbParallax {

  implicit val arbParallax: Arbitrary[Parallax] =
    Arbitrary {
      arbitrary[BigDecimal].map(Parallax.apply)
    }

  implicit val cogParallax: Cogen[Parallax] =
    Cogen[BigDecimal].contramap(_.mas)
}

object ArbParallax extends ArbParallax
