// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.enum._
import basic.misc._
import basic.graphql.syntax.all._
import cats.implicits._
import io.circe.{ Json, Decoder }
import sangria.schema._
import sangria.marshalling._
import sangria.marshalling.circe._

object SpectralDistributionType {

  // See SpatialProfile.scala for a rant that is also relevant here.

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
    case object BlackBody  extends Type("BLACK_BODY",  "A black body with a temperature in Kelvin (`temperature` must also be specified).")
    case object PowerLaw   extends Type("POWER_LAW",   "Defined by power law function (`index` must also be specified).")
    case object Stellar    extends Type("STELLAR",     "A library-defined stellar spectrum (`stellarSpectrum` must also be specified).")
    case object NonStellar extends Type("NON_STELLAR", "A library-defined non-stellar spectrum (`nonStellarSpectrum` must also be specified).")
    // TODO: emission line, user-defined
    val all = List(BlackBody, PowerLaw, Stellar, NonStellar)
  }

  // GraphQL metadata for the `Type` enum type.
  val TypeType: EnumType[Type] =
    EnumType(
      name        = "SpectralDistributionType",
      description = Some("Discriminant identifying a spectral distribution variant."),
      values      = Type.all.map(_.toEnumValue)
    )

  // GraphQL metadata for the `StellarLibrarySpectrum` enum type.
  val StellarLibrarySpectrumType: EnumType[StellarLibrarySpectrum] =
    EnumType(
      name       = "StellarLibrarySpectrum",
      description = Some("Identifier for a library-defined stellar spectrum."),
      values      = StellarLibrarySpectrum.all.map { s =>
        EnumValue(
          name        = s.tag.toUpperCase,
          description = Some(s"SED spectrum ${s.sedSpectrum}."),
          value       = s
        )
      }
    )

  // GraphQL metadata for the `NonStellarLibrarySpectrum` enum type.
  val NonStellarLibrarySpectrumType: EnumType[NonStellarLibrarySpectrum] =
    EnumType(
      name       = "NonStellarLibrarySpectrum",
      description = Some("Identifier for a library-defined non-stellar spectrum."),
      values      = NonStellarLibrarySpectrum.all.map { s =>
        EnumValue(
          name        = s.tag.toUpperCase,
          description = Some(s.shortName),
          value       = s
        )
      }
    )

    // GraphQL metadata for the `SpectralDistribution` input tpe.
    def inputType: InputObjectType[SpectralDistribution] =
      InputObjectType[SpectralDistribution](
        name          = "SpectralDistribution",
        description   = "",
        fieldsFn      = () =>
          List[InputField[_]](
            InputField(
              name         = "type",
              fieldType    = TypeType
            ),
            InputField(
              name         = "temperature",
              description  = "Temperature in Kelvin. Required for `BLACK_BODY`, ignored otherwise.",
              fieldType    = OptionInputType(FloatType)
            ),
            InputField(
              name         = "index",
              description  = "Power law function index. Required for `POWER_LAW`, ignored otherwise.",
              fieldType    = OptionInputType(FloatType)
            ),
            InputField(
              name         = "stellarSpectrum",
              description  = "Stellar library spectrum. Required for `STELLAR`, ignored otherwise.",
              fieldType    = OptionInputType(StellarLibrarySpectrumType)
            ),
            InputField(
              name         = "nonStellarSpectrum",
              description  = "Non-stellar library spectrum. Required for `NON_STELLAR`, ignored otherwise.",
              fieldType    = OptionInputType(NonStellarLibrarySpectrumType)
            ),
          )
      )

    object implicits {
      import SpectralDistribution._

      private implicit val DecodeStellarSpectrum: Decoder[StellarLibrarySpectrum] =
        Decoder.instance { c =>
          c.as[String].map(s => StellarLibrarySpectrum.fromTagIgnoreCase(s).getOrElse {
            throw new NoSuchElementException(s"No such stellar spectrum: $s")
          })
        }

      private implicit val DecodeNonStellarSpectrum: Decoder[NonStellarLibrarySpectrum] =
        Decoder.instance { c =>
          c.as[String].map(s => NonStellarLibrarySpectrum.fromTagIgnoreCase(s).getOrElse {
            throw new NoSuchElementException(s"No such non-stellar spectrum: $s")
          })
        }

      implicit val FromInputSpectralDistribution: FromInput[SpectralDistribution] =
        FromInput[Json].map { json =>
          val c = json.hcursor
          val r = c.downField("type").as[String].flatMap {
            case Type.BlackBody.name  => c.downField("temperature").as[Double].map(BlackBody(_))
            case Type.PowerLaw.name   => c.downField("index").as[Double].map(PowerLaw(_))
            case Type.Stellar.name    => c.downField("stellarSpectrum").as[StellarLibrarySpectrum].map(s => Library(Left(s)))
            case Type.NonStellar.name => c.downField("nonStellarSpectrum").as[NonStellarLibrarySpectrum].map(s => Library(Right(s)))
          }
          // Should never fail due to the way the schema is defined.
          r.getOrElse(throw new IllegalArgumentException(s"Can't decode ${json.noSpaces} as a SpectralDistribution."))
        }

    }

}
