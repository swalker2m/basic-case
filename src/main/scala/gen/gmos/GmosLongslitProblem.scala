// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package gen.gmos

import basic.enum.ImageQuality
import gem.enum.GmosXBinning

import fs2._

/**
 * Possible issues that may arise as a sequence is calculated.
 */
sealed trait GmosLongslitProblem extends Product with Serializable {
  def message: String

  def exception: GmosLongslitException =
    GmosLongslitException(this)

  def raiseError[F[_]: RaiseThrowable]: Stream[F, INothing] =
    Stream.raiseError(exception)
}

object GmosLongslitProblem {

  final case class BinningChange(
    from: GmosXBinning,
    to:   GmosXBinning,
    iq:   ImageQuality
  ) extends GmosLongslitProblem {
    override def message: String =
      s"GMOS X Binning would need to be updated to ${to.count} from ${from.count} in IQ${iq.percentage}"
  }

  final case class SourceTooBright(
    conditions: ObservingConditions
  ) extends GmosLongslitProblem {
    override def message: String =
      s"The ITC reports that the source is too bright to be observed in CC${conditions.cc.percentage}, IQ${conditions.iq.percentage}, and SB${conditions.sb.percentage}"
  }

}
