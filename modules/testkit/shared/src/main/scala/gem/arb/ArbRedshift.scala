// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem
package arb

import gem.Redshift
import org.scalacheck.Arbitrary
import org.scalacheck.Cogen
import org.scalacheck.Arbitrary._

trait ArbRedshift {

  implicit val arbRedshift: Arbitrary[Redshift] =
    Arbitrary {
      for {
        rs <- arbitrary[BigDecimal]
      } yield Redshift(rs)
    }

  implicit val cogRedshift: Cogen[Redshift] =
    Cogen[BigDecimal].contramap(_.z)
}

object ArbRedshift extends ArbRedshift
