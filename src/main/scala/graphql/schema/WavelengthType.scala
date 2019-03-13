// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import gem.math.Wavelength
import sangria.ast.{ BigIntValue, IntValue }
import sangria.schema._
import sangria.validation.{ Violation, ValueCoercionViolation }

object WavelengthType {

  object CoercionViolation extends ValueCoercionViolation("Wavelength must be a positive integer in nanometers.")

  def fromNanometers(n: Int): Either[Violation, Wavelength] =
    Wavelength.fromNanometers.getOption(n).toRight(CoercionViolation)

  def scalar: ScalarType[Wavelength] =
    ScalarType(
      name = "Wavelength",
      description = Some("`Wavelength` is a refinement of `Int`, a positive value in nanometers."),
      coerceUserInput = {
        case n: Int => fromNanometers(n)
        case _      => Left(CoercionViolation)
      },
      coerceInput  = {
        case IntValue(n, _, _)                    => fromNanometers(n)
        case BigIntValue(n, _, _) if n.isValidInt => fromNanometers(n.toInt)
        case _                                    => Left(CoercionViolation)
      },
      coerceOutput = (w, _) => Wavelength.fromNanometers.reverseGet(w),
    )

}