// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats._
import coulomb._
import coulomb.si._
import coulomb.siprefix._
import spire.std.bigDecimal._
import java.math.MathContext

/**
  * Representation of a radial velocity in kilometers per second
  * Valid range is (-C, C) where C is the speed of light
  * Radial Velocity is often represented as RV
  */
final case class RadialVelocity private (rv: RadialVelocity.RVQuantity) {

  /**
    * Converts the radial velocity to a Redshift, approximate
    * a return value of None should be understood as an infinity Redshift
    */
  def toRedshift: Option[Redshift] =
    // Though we forbid constructing an RV with value C there is at least one instance with that value
    if (rv.value.abs < RadialVelocity.CValue) {
      val i = (rv / RadialVelocity.C).value
      val t = (1 + i) / (1 - i)
      Some(Redshift(BigDecimal.decimal(scala.math.sqrt(t.toDouble) - 1).round(rv.value.mc)))
    } else None
}

object RadialVelocity {
  type CUnit      = Meter %/ Second
  type RVUnit     = (Kilo %* Meter) %/ Second
  type RVQuantity = Quantity[BigDecimal, RVUnit]

  // Reference: https://www.nist.gov/si-redefinition/meet-constants
  // Exact value of the speed of light in m/s
  private val CValue: BigDecimal = BigDecimal.decimal(299792458, MathContext.DECIMAL64)

  val C: RVQuantity =
    CValue.withUnit[CUnit].toUnit[RVUnit] // Speed of light in km/s

  /**
    * Construct a RadialVelocity if the value is in the allowed range
    * @group Constructors
    */
  def apply(rv: RadialVelocity.RVQuantity): Option[RadialVelocity] =
    if (rv.value.abs < CValue) Some(new RadialVelocity(rv)) else None

  /**
    * Attempts to construct a RadialVelocity, it will fail if the value is outside the allowed range
    * @group Constructors
    */
  def unsafeFromRVQuantity(rv: RadialVelocity.RVQuantity): RadialVelocity =
    apply(rv).getOrElse(sys.error(s"Value of rv $rv not allowed"))

  /**
    * `Zero RadialVelocity`
    * @group Constructors
    */
  val Zero: RadialVelocity = new RadialVelocity(0.withUnit[RVUnit])

  /** @group Typeclass Instances */
  implicit val order: Order[RadialVelocity] =
    Order.by(_.rv.value)

}
