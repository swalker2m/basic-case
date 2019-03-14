// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.itc.client

final case class ItcObservationDetails(
  calculationMethod: ItcObservationDetails.CalculationMethod,
  analysisMethod:    ItcObservationDetails.AnalysisMethod
)

object ItcObservationDetails {

  sealed trait CalculationMethod
    extends Product with Serializable

  object CalculationMethod {

    sealed trait SignalToNoise extends CalculationMethod {
      def exposures:      Int
      def coadds:         Option[Int]
      def exposureTime:   Double
      def sourceFraction: Double
      def offset:         Double
    }

    object SignalToNoise {

      final case class Imaging(
        exposures:      Int,
        coadds:         Option[Int],
        exposureTime:   Double,
        sourceFraction: Double,
        offset:         Double
      ) extends SignalToNoise

      final case class Spectroscopy(
        exposures:      Int,
        coadds:         Option[Int],
        exposureTime:   Double,
        sourceFraction: Double,
        offset:         Double
      ) extends SignalToNoise

    }

    sealed trait IntegrationTime extends CalculationMethod {
      def sigma:          Double
      def exposureTime:   Double
      def coadds:         Option[Int]
      def sourceFraction: Double
      def offset:         Double
    }

    object IntegrationTime {

      final case class Imaging(
        sigma:          Double,
        exposureTime:   Double,
        coadds:         Option[Int],
        sourceFraction: Double,
        offset:         Double
      ) extends IntegrationTime

    }

  }

  sealed trait AnalysisMethod
    extends Product with Serializable

  object AnalysisMethod {

    object Aperture {

      final case class Auto(
        skyAperture: Double
      ) extends AnalysisMethod

      final case class User(
        diameter: Double,
        skyAperture: Double
      ) extends AnalysisMethod

    }

    object Ifu {

      final case class IfuSingle(
        skyFibres: Int,
        offset:    Double
      ) extends AnalysisMethod

      final case class IfuRadial(
        skyFibres: Int,
        minOffset: Double,
        maxOffset: Double
      ) extends AnalysisMethod

      final case class IfuSummed(
        skyFibres: Int,
        numX:      Int,
        numY:      Int,
        centerX:   Double,
        centerY:   Double
      ) extends AnalysisMethod

      final case class IfuSum(
        skyFibres: Int,
        num:       Double,
        isIfu2:    Boolean
      ) extends AnalysisMethod
    }

  }

}