// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats._
import cats.implicits._
import gem.enum.MagnitudeBand
import gem.enum.MagnitudeSystem
import gsp.math.MagnitudeValue
import monocle.macros.Lenses

/**
 * Describes the magnitude of a target on a given band
 */
@Lenses
case class Magnitude(
  value: MagnitudeValue,
  band: MagnitudeBand,
  error: Option[MagnitudeValue],
  system: MagnitudeSystem
)

object Magnitude {
  /** Secondary constructor. */
  def apply(value: MagnitudeValue, band: MagnitudeBand, error: MagnitudeValue) =
    new Magnitude(value, band, Some(error), band.magnitudeSystem)

  /** Secondary constructor defaulting to no error. */
  def apply(value: MagnitudeValue, band: MagnitudeBand, system: MagnitudeSystem) =
    new Magnitude(value, band, None, system)

  /** Secondary constructor defaulting to no given error. */
  def apply(value: MagnitudeValue, band: MagnitudeBand) =
    new Magnitude(value, band, None, band.magnitudeSystem)

  // by system name, band name, value and error (in that order)
  implicit val MagnitudeOrdering: Order[Magnitude] =
    Order.by(m => (m.system.tag, m.band.tag, m.value, m.error))

  private def compareOptionWith(
    x: Option[Magnitude],
    y: Option[Magnitude],
    c: Order[Magnitude]
  ): Int =
    (x,y) match {
      case (Some(m1), Some(m2)) => c.compare(m1, m2)
      case (None,     None)     =>  0
      case (_,        None)     => -1
      case (None,     _)        =>  1
    }

  // comparison on Option[Magnitude] that reverses the way that None is treated, i.e. None is always > Some(Magnitude).
  implicit val MagnitudeOptionOrdering: Order[Option[Magnitude]] =
    new Order[Option[Magnitude]] {
      override def compare(x: Option[Magnitude], y: Option[Magnitude]): Int =
        compareOptionWith(x, y, MagnitudeOrdering)
    }

  /** @group Typeclass Instances */
  implicit val EqMagnitude: Eq[Magnitude] = Eq.by(x => (x.value, x.band, x.error, x.system))

  /** group Typeclass Instances */
  implicit val MagnitudeShow: Show[Magnitude] =
    Show.show { mag =>
      val errStr = mag.error.map { e => f" e${e.toDoubleValue}%.2f " }.getOrElse(" ")
      f"${mag.band.shortName}${mag.value.toDoubleValue}%.2f$errStr(${mag.system.tag})"
    }
}

