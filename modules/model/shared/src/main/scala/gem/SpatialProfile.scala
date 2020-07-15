package gem

import cats.Eq
import cats.implicits._
import gsp.math.Angle

sealed trait SpatialProfile extends Product with Serializable

object SpatialProfile {
  case object PointSource   extends SpatialProfile
  case object UniformSource extends SpatialProfile
  final case class GaussianSource(fwhm: Angle) extends SpatialProfile

  implicit val eqSpatialProfile: Eq[SpatialProfile] = Eq.instance {
    case (PointSource, PointSource)             => true
    case (UniformSource, UniformSource)         => true
    case (GaussianSource(a), GaussianSource(b)) => a === b
    case _                                      => false
  }
}
