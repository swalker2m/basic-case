package basic.misc

import basic.syntax.wavelength._
import cats.implicits._
import gem.math.Wavelength

sealed trait Coverage {
  import Coverage.{ Empty, Range }

  /** Intersect this `Coverage` with another. */
  def ⋂(other: Coverage): Coverage =
    (this, other) match {
      case (Empty, _) => Empty
      case (_, Empty) => Empty
      case (Range(a, b), Range(aʹ, bʹ)) => new Range(a max aʹ, b min bʹ) {}
    }

  def width: Wavelength =
    this match {
      case Empty       => Wavelength.Min
      case Range(a, b) => b - a
    }

  def range: Option[Coverage.Range] =
    this match {
      case Empty              => None
      case r @ Range(_, _) => Some(r)
    }

}

object Coverage {

  case object Empty extends Coverage

  sealed abstract case class Range(min: Wavelength, max: Wavelength) extends Coverage {
    require(min <= max) // smart ctor should guarantee this
  }

  def apply(min: Wavelength, max: Wavelength): Coverage =
    if (min > max) Empty else new Range(min, max) {}

  def centered(central: Wavelength, width: Wavelength): Coverage =
    apply(central - width / 2, central + width / 2)

}
