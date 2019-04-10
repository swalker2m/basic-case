// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.math.Wavelength

final class WavelengthOps(val self: Wavelength) extends AnyVal {

  /** Returns the difference of this wavelength and `other`, clipped at Wavelength.Min. */
  def -(other: Wavelength): Wavelength =
    Wavelength.fromPicometers.getOption(self.toPicometers - other.toPicometers)
      .getOrElse(Wavelength.Min)

  /** Returns the sum of this wavelength and `other`, clipped at Wavelength.Max. */
  def +(other: Wavelength): Wavelength =
    Some(BigInt(self.toPicometers) + BigInt(other.toPicometers))
      .filter(_.isValidInt)
      .map(_.toInt)
      .flatMap(Wavelength.fromPicometers.getOption)
      .getOrElse(Wavelength.Max)

  def /(scalar: Int): Wavelength =
    Wavelength.fromPicometers.getOption(self.toPicometers / scalar).get // always positive

}

trait ToWavelengthOps {
  implicit def toWavelengthOps(self: Wavelength): WavelengthOps =
    new WavelengthOps(self)
}

object wavelength extends ToWavelengthOps
