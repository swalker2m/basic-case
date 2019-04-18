// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.misc


import cats.tests._
import gem.math.Wavelength
import org.scalacheck._
import org.scalacheck.Arbitrary._

class CoverageSuite extends CatsSuite {

  implicit def arbWavelength: Arbitrary[Wavelength] =
    Arbitrary {
      arbitrary[Int].map(_.abs).map(Wavelength.fromPicometers.getOption).flatMap {
        case Some(w) => Gen.const(w)
        case None    => Gen.fail
      }
    }

  implicit def arbCoverage: Arbitrary[Coverage] =
    Arbitrary {
      for {
        a <- arbitrary[Wavelength]
        b <- arbitrary[Wavelength]
      } yield Coverage(a, b)
    }

  test("construction.invariant") {
    forAll { (a: Wavelength, b: Wavelength) =>
      Coverage(a, b).range.isDefined should be (a < b)
    }
  }

  test("intersection.identity") {
    forAll { (a: Coverage) =>
      (a ⋂ a) should be (a)
    }
  }

  test("intersection.annihilation.right") {
    forAll { (a: Coverage) =>
      (a ⋂ Coverage.Empty) should be (Coverage.Empty)
    }
  }

  test("intersection.annihilation.left") {
    forAll { (a: Coverage) =>
      (Coverage.Empty ⋂ a) should be (Coverage.Empty)
    }
  }

  test("intersection.reduction") {
    forAll { (a: Coverage, b: Coverage) =>
      (a ⋂ b).width should be <= a.width
      (a ⋂ b).width should be <= b.width
    }
  }

}