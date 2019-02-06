package basic.search

// taken from OCS … is this sufficient?
sealed trait SpatialProfile
object SpatialProfile {
  final case object PointSource                  extends SpatialProfile
  final case object UniformSource                extends SpatialProfile
  final case class  GaussianSource(fwhm: Double) extends SpatialProfile
}
