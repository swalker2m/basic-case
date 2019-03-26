// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.misc._
import basic.graphql.syntax.all._
import cats.implicits._
import io.circe.Json
import io.circe.syntax._
import sangria.schema._
import sangria.marshalling._
import sangria.marshalling.circe._

object SpatialProfileType {

  // This is Very Bad™ because GraphQL doesn't support union types for input so we have to hack up
  // a discriminated union ourselves. Schema validation will check that everything has the right
  // type, but *we* have to check that `fwhm` is provided when `type: GAUSSIAN`.

  // A discriminant, to define which constructor we're encoding. We will encode this discriminant
  // as a GraphQL enum type, so it's useful to be able to turn it into an EnumValue.
  sealed abstract class Type(val name: String, description: String) {
    def toEnumValue: EnumValue[Type] =
      EnumValue(
        name        = name,
        description = Some(description),
        value       = this
      )
  }
  object Type {
    case object Point    extends Type("POINT",    "Point source.")
    case object Uniform  extends Type("UNIFORM",  "Extended source with uniform surface brightness.")
    case object Gaussian extends Type("GAUSSIAN", "Extended source with a Gaussian profile (`fwhm` must also be specified).")
    val all = List(Point, Uniform, Gaussian)
  }

  // GraphQL metadata for the `Type` enum type.
  val TypeType: EnumType[Type] =
    EnumType(
      name        = "SpatialProfileType",
      description = Some("Discriminant identifying a spatial profile variant."),
      values      = Type.all.map(_.toEnumValue)
    )

  // GraphQL metadata for the `SpatialProfile` input tpe.
  def inputType: InputObjectType[SpatialProfile] =
    InputObjectType[SpatialProfile](
      name          = "SpatialProfile",
      description   = "",
      fieldsFn      = () =>
        List[InputField[_]](
          InputField(
            name         = "type",
            fieldType    = TypeType
          ),
          InputField(
            name         = "fwhm",
            description  = "Full width half maximum (including seeing) in arcseconds. Required for `GAUSSIAN` spatial profile, ignored otherwise.",
            fieldType    = OptionInputType(FloatType),
          )
        )
    )

  // In order to construct an `Argument[SpatialProfileType]` we need a `FromInput` instance that
  // describes how to decode the incoming Json. This is extremely lame but it's what we get with
  // Sangria. Also in order to use a specific `SpatialProfile` as a default value for the argument
  // we need a `ToInput` instance that explains how to *encode* the value as Json. It's all bad.
  object implicits {

    implicit val FromInputSpatialProfile: FromInput[SpatialProfile] =
      FromInput[Json].map { json =>
        val c = json.hcursor
        val r = c.downField("type").as[String].flatMap {
          case Type.Point.name    => SpatialProfile.PointSource.asRight
          case Type.Uniform.name  => SpatialProfile.UniformSource.asRight
          case Type.Gaussian.name =>
            c.downField("fwhm").as[Double].map { fwhm =>
              SpatialProfile.GaussianSource.arcsec.reverseGet(fwhm)
            }
        }
        // Should never fail due to the way the schema is defined.
        r.getOrElse(throw new IllegalArgumentException(s"Can't decode ${json.noSpaces} as a SpatialProfile."))
      }

    implicit val ToInputSpatialProfileJson: ToInput[SpatialProfile, Json] =
      ToInput[Json, Json].contramap {
        case SpatialProfile.PointSource           => Json.obj("type" -> Type.Point.name.asJson)
        case SpatialProfile.UniformSource         => Json.obj("type" -> Type.Uniform.name.asJson)
        case g @ SpatialProfile.GaussianSource(_) =>
          Json.obj(
            "type" -> Type.Gaussian.name.asJson,
            "fwhm" -> SpatialProfile.GaussianSource.arcsec.get(g).asJson
          )
      }

    }

}
