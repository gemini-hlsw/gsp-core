// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats._
import cats.implicits._
import coulomb._

/**
  * Represents a redshift of an object as it moves away (positive) or towards (negative) the observing point
  * 
  * Redshift can be (perhaps suprpsinginly) higher than 1.
  *
  * For far objects Redshift can be converted to RadialVelocity which takes into account relativistic effects and cannot be more than C
  * For nearer objects we can convert to ApparentRadialVelocity which doesn't consider relativistic effects
  *
  * Often Redshift is referred as z
  */
final case class Redshift(z: BigDecimal) {

  /**
    * Converts to RadialVelocity, approximate
    */
  def toRadialVelocity: Option[RadialVelocity] = {
    val rv = RadialVelocity.CValue * (((z + 1) * (z + 1) - 1) / ((z + 1) * (z + 1) + 1))
    RadialVelocity(rv.round(z.mc).withUnit[RadialVelocity.RVUnit])
  }

  def toApparentRadialVelocity: ApparentRadialVelocity =
    ApparentRadialVelocity((RadialVelocity.CValue * z).withUnit[RadialVelocity.RVUnit])
}

object Redshift {

  /**
    * The `No redshift`
    * @group Constructors
    */
  val Zero: Redshift = new Redshift(0)

  /** @group Typeclass Instances */
  implicit val order: Order[Redshift] =
    Order.by(_.z)

}
