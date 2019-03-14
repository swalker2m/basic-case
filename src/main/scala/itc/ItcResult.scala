// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.itc

import cats.data.NonEmptyList
import cats.implicits._
import io.circe.Decoder
import io.circe.generic.semiauto._

final case class ItcResult(ccds: NonEmptyList[ItcCcd]) {

  // We may not need these
  def maxPeakPixelFlux:   Int    = ccds.map(_.peakPixelFlux).maximum.toInt
  def maxAdu:             Int    = ccds.map(_.adu).maximum
  def maxPercentFullWell: Double = ccds.map(_.percentFullWell).maximum
  def maxSingleSNRatio:   Double = ccds.map(_.singleSNRatio).maximum
  def maxTotalSNRatio:    Double = ccds.map(_.totalSNRatio).maximum

}

object ItcResult {

  implicit val decoder: Decoder[ItcResult] = deriveDecoder

}