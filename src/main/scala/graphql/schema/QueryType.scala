// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import cats.effect._
import sangria.schema._

object QueryType {

  val NamePattern: Argument[String] =
    Argument(
      name         = "namePattern",
      argumentType = OptionInputType(StringType),
      description  = "SQL-style pattern for city name, like \"San %\".",
      defaultValue = "%"
    )

  val Code: Argument[String] =
    Argument(
      name         = "code",
      argumentType = StringType,
      description  = "Unique code of a country."
    )

  def apply[F[_]: Effect]: ObjectType[Unit, Unit] =
    ObjectType(
      name  = "Query",
      fields = fields(

        Field(
          name        = "modes",
          fieldType   = StringType,
          description = None,
          arguments   = List(),
          resolve     = _ => "foobar"
        ),

        // Field(
        //   name        = "country",
        //   fieldType   = OptionType(CountryType[F]),
        //   description = Some("Returns the country with the given code, if any."),
        //   arguments   = List(Code),
        //   resolve     = c => c.ctx.country.fetchByCode(c.arg(Code)).toIO.unsafeToFuture
        // ),

        // Field(
        //   name        = "countries",
        //   fieldType   = ListType(CountryType[F]),
        //   description = Some("Returns all countries."),
        //   resolve     = c => c.ctx.country.fetchAll.toIO.unsafeToFuture
        // ),

      )
    )

}
