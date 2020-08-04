// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats._
import coulomb._
import spire.std.bigDecimal._

/**
  * Representation of a radial velocity in kilometers per second
  * Unlike RadialVelocity this is not limited to the speed of light
  * Apparent Radial Velocity is often represented as cz
  */
final case class ApparentRadialVelocity(cz: RadialVelocity.RVQuantity) {

  /**
    * Converts the apparent radial velocity to a Redshift
    */
  def toRedshift: Redshift = Redshift((cz / RadialVelocity.C).value)
}

object ApparentRadialVelocity {

  /**
    * `Zero ApparentRadialVelocity`
    * @group Constructors
    */
  val Zero: ApparentRadialVelocity = new ApparentRadialVelocity(0.withUnit[RadialVelocity.RVUnit])

  /** @group Typeclass Instances */
  implicit val order: Order[ApparentRadialVelocity] =
    Order.by(_.cz.value)

}
