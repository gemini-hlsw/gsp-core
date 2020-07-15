// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats._
import coulomb._
import coulomb.si._
import coulomb.siprefix._
import spire.std.bigDecimal._

case class Redshift(z: BigDecimal)

object Redshift {
  type RadialVelocity         = (Kilo %* Meter) %/ Second
  type RadialVelocityQuantity = Quantity[BigDecimal, RadialVelocity]

  val C: RadialVelocityQuantity =
    299792.458.withUnit[RadialVelocity] // Speed of light in km/s

  /** @group Typeclass Instances */
  implicit val eqRedshift: Eq[Redshift] = Eq.by(_.z)

  /**
    * The `No redshift`
    * @group Constructors
    */
  val Zero: Redshift = Redshift(0)

  /** @group Typeclass Instances */
  implicit val order: Order[Redshift] =
    Order.by(_.z)

  def fromVelocity(v: RadialVelocityQuantity): Option[Redshift] =
    if (v < C) {
      val i = (v / C).value
      val t = (1 + i) / (1 - i)
      Some(Redshift(scala.math.sqrt(t.toDouble) - 1))
    } else None
}
