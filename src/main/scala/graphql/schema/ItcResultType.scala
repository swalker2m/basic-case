// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package graphql.schema

import basic.syntax.finiteduration._
import sangria.schema._
import scala.concurrent.duration._

object ItcResultType {

  def apply(): UnionType[Unit] =
    UnionType(
      name        = "ItcResult",
      description = None,
      types       = List(SuccessType, SourceTooBrightType)
    )

  val SuccessType: ObjectType[Unit, Itc.Result.Success] =
    ObjectType(
      name  = "ItcResultSuccess",
      fields = fields(

        Field(
          name        = "exposureTime",
          fieldType   = StringType,
          resolve     = _.value.exposureTime.toString
        ),

        Field(
          name        = "exposures",
          fieldType   = IntType,
          resolve     = _.value.exposures
        ),

        Field(
          name        = "totalIntegrationTime",
          fieldType   = StringType,
          resolve     = a => (a.value.exposures * a.value.exposureTime.toDoubleSeconds).seconds.toString
        ),

        Field(
          name        = "signalToNoise",
          fieldType   = IntType,
          resolve     = _.value.signalToNoise
        ),

      )
    )

  val SourceTooBrightType: ObjectType[Unit, Itc.Result.SourceTooBright.type] =
    ObjectType(
      name   = "ItcResultSourceTooBright",
      fields = List(
        Field(
          name        = "message",
          fieldType   = StringType,
          resolve     = _ => "Target is too bright to be observed in this mode."
        ),
      )
    )

}