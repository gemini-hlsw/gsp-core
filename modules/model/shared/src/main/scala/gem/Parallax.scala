// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats._
import gsp.math.Angle
import gsp.math.optics.SplitMono
import monocle.Iso

/**
  * Parallax stored as an angle
  */
case class Parallax(toAngle: Angle)

object Parallax extends ParallaxOptics {

  /**
    * The `No parallax`
    * @group Constructors
    */
  val Zero: Parallax = Parallax(Angle.Angle0)

  /** @group Typeclass Instances */
  implicit val eqParallax: Eq[Parallax] =
    Eq.by(_.toAngle)

}

sealed trait ParallaxOptics {

  val angle: Iso[Parallax, Angle] =
    Iso[Parallax, Angle](_.toAngle)(Parallax.apply)

  /**
    * This `Parallax` as signed decimal milliarcseconds.
    */
  val signedMilliarcseconds: SplitMono[Parallax, BigDecimal] =
    SplitMono.fromIso(angle).composeSplitMono(Angle.signedDecimalMilliarcseconds)

}
