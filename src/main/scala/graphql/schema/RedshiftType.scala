// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.misc.Redshift
import basic.graphql.syntax.all._
import io.circe.Json
import io.circe.syntax._
import sangria.ast._
import sangria.schema._
import sangria.validation.ValueCoercionViolation
import sangria.marshalling.ToInput
import sangria.marshalling.circe._

object RedshiftType {

  object CoercionViolation extends ValueCoercionViolation("Redshift must be a number.")

  def scalar: ScalarType[Redshift] =
    ScalarType(
      name = "Redshift",
      description = Some("`Redshift` (commonly written `z`) is a refinement of `Float`."),
      coerceUserInput = {
        case n: Double     => Right(Redshift(n))
        case n: Float      => Right(Redshift(n.toDouble))
        case n: Int        => Right(Redshift(n.toDouble))
        case n: BigInt     => Right(Redshift(n.toDouble))
        case n: BigDecimal => Right(Redshift(n.toDouble))
        case _             => Left(CoercionViolation)
      },
      coerceInput  = {
        case IntValue(n, _, _)   => Right(Redshift(n.toDouble))
        case FloatValue(n, _, _) => Right(Redshift(n.toDouble))
        case BigDecimalValue(n, _, _) => Right(Redshift(n.toDouble))
        case BigIntValue(n, _, _) => Right(Redshift(n.toDouble))
        case _                   => Left(CoercionViolation)
      },
      coerceOutput = (r, _) => r.z,
    )

  object implicits {

    implicit val ToInputRedshiftJson: ToInput[Redshift, Json] =
      ToInput[Json, Json].contramap(_.z.asJson)

  }

}