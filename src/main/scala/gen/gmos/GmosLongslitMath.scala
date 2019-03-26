// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.gen.gmos

import basic.enum.ImageQuality
import basic.misc.SpatialProfile
import basic.syntax.all._

import gem.enum.GmosDetector.HAMAMATSU
import gem.enum._
import gem.math.{Angle, Wavelength}

import cats.Order
import cats.implicits._


/**
 * GMOS Longslit calculations for use in sequence generation.
 */
private[gmos] trait GmosLongslitMath {
  private implicit val AngleOrder: Order[Angle] =
    Angle.AngleOrder

  // Desired sampling. (May become a parameter.)
  private val Sampling: Double =
    2.5

  // XBinning options sorted in descending order by binning count.  We want to
  // find the largest usable binning so we search from high to low.
  private val DescendingXBinning: List[GmosXBinning] =
    GmosXBinning.all.sortBy(b => -b.count)

  // GmosXBinning.One value with wider type.
  private val One: GmosXBinning =
    GmosXBinning.One

  /**
   * Calculates the best `GmosXBinning` value to use for longslit observing for
   * the desired sampling.
   *
   * @param slitWidth slit size
   * @param sampling desired sampling rate
   * @param site Gemini site, used to determine detector pixel size
   */
  def xbin(slitWidth: Angle, sampling: Double, site: Site): GmosXBinning = {
    val pixsz = HAMAMATSU.pixelSize(site)
    val npix  = slitWidth.toMicroarcseconds.toDouble / pixsz.toMicroarcseconds.toDouble
    DescendingXBinning.find(b => npix / b.count.toDouble >= sampling).getOrElse(One)
  }

  def objectSize(sp: SpatialProfile): Angle =
    sp match {
      case SpatialProfile.PointSource       => Angle.Angle0
      case SpatialProfile.UniformSource     => Angle.Angle180
      case SpatialProfile.GaussianSource(a) => a
    }

  def imageQualitySize(iq: ImageQuality): Angle =
    iq match {
      case ImageQuality.Percent20  => Angle.fromMicroarcseconds( 400000L)
      case ImageQuality.Percent70  => Angle.fromMicroarcseconds( 700000L)
      case ImageQuality.Percent85  => Angle.fromMicroarcseconds( 850000L)
      case ImageQuality.PercentAny => Angle.fromMicroarcseconds(1300000L)
    }

  /**
   * Effective size of a target with the given spatial profile and image quality.
   */
  def effectiveSize(sp: SpatialProfile, iq: ImageQuality): Angle =
    objectSize(sp) max imageQualitySize(iq)

  /**
   * Effective slit width considering the FPU, spatial profile, and image
   * quality.
   */
  def effectiveSlitWidth(fpu: GmosNorthFpu, sp: SpatialProfile, iq: ImageQuality): Angle =
    if (fpu.isIfu) fpu.effectiveSlitWidth
    else fpu.effectiveSlitWidth min effectiveSize(sp, iq)

  def xbin(fpu: GmosNorthFpu, sp: SpatialProfile, iq: ImageQuality): GmosXBinning =
    xbin(effectiveSlitWidth(fpu, sp, iq), Sampling, Site.GN)


  // TODO: convert Angle to pm and change dispersion to Angle

  /**
   * Calculates the wavelength offsets required to fill in the chip gaps,
   * rounded to the nearest 5 nm.
   *
   * @param dispersionPm - dispersion in pm/pix (see https://www.gemini.edu/sciops/instruments/gmos/spectroscopy-overview/gratings)
   * @param site - Gemini site, used to determine the detector gap size
   *
   * @return a Δλ value expressed as a Wavelength (TODO: is this kosher?)
   */
  def Δλ(dispersionPm: Int, site: Site): Wavelength = {
    val d = dispersionPm / 1000.0   // convert to double nm/pix
    val g = HAMAMATSU.effectiveGapSizePixels(site)
    val v = d * g * 2.0             // raw value, which we round to nearest 5 nm
    Wavelength.fromNanometers.unsafeGet(((v/5.0).round * 5.0).toInt)
  }

  def Δλ(d: GmosNorthDisperser): Wavelength =
    Δλ(d.dispersion, Site.GN)

  def Δλ(d: GmosSouthDisperser): Wavelength =
    Δλ(d.dispersion, Site.GS)

}
