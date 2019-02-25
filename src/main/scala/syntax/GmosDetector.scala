// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.{ GmosDetector, Site }
import gem.math.Angle

// TODO: consider having HamamatsuNorth and HamamatsuSouth because there are
// differing pixel and gap sizes?

final class GmosDetectorOps(val self: GmosDetector) extends AnyVal {

  def pixelSize(site: Site): Angle =
    site match {
      case Site.GN => self.northPixelSize
      case Site.GS => self.southPixelSize
    }

  /**
   * Size of unusable gaps between the detectors in pixels.  GN has a larger
   * effective gap size because of bright columns on either side of the gaps.
   */
  def effectiveGapSizePixels(site: Site): Int =
    site match {
      case Site.GN => 80
      case Site.GS => 67  // TODO: unclear whether to use GN value here as well
    }
}

trait ToGmosDetectorOps {
  implicit def toGmosDetectorOps(self: GmosDetector): GmosDetectorOps =
    new GmosDetectorOps(self)
}

object gmosdetector extends ToGmosDetectorOps
