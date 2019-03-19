// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.itc

import io.circe.Decoder
import io.circe.generic.semiauto._

final case class ItcWarning(msg: String)

object ItcWarning {
  implicit val decoder: Decoder[ItcWarning] = deriveDecoder
}
