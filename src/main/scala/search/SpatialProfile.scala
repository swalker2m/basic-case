// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.search

// taken from OCS … is this sufficient?
sealed trait SpatialProfile
object SpatialProfile {
  final case object PointSource                  extends SpatialProfile
  final case object UniformSource                extends SpatialProfile
  final case class  GaussianSource(fwhm: Double) extends SpatialProfile
}
