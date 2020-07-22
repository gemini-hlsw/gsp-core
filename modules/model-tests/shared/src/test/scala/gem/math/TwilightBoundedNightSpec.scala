// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.math

import gem.arb.{ ArbEnumerated, ArbObservingNight }
import gem.enum.Site
import gem.instances.time._
import gsp.math.arb.ArbTime

import cats.{ Eq, Show }
import cats.kernel.laws.discipline._
import cats.tests.CatsSuite
import java.time._
import monocle.law.discipline._
import gsp.math.skycalc.TwilightBoundType

final class TwilightBoundedNightSpec extends CatsSuite {
  import ArbEnumerated._
  import ArbObservingNight._
  import gem.arb.ArbTwilightBoundedNight._
  import ArbTime._
  import gsp.math.arb.ArbTwilightBoundType._

  checkAll("TwilightBoundedNight", OrderTests[TwilightBoundedNight].order)
  checkAll("TwilightBoundedNight.boundType", LensTests(TwilightBoundedNight.boundType))
  checkAll("TwilightBoundedNight.observingNight", LensTests(TwilightBoundedNight.observingNight))
  checkAll("TwilightBoundedNight.localObservingNight",
           LensTests(TwilightBoundedNight.localObservingNight)
  )
  checkAll("TwilightBoundedNight.site", LensTests(ObservingNight.site))
  checkAll("TwilightBoundedNight.localDate", LensTests(ObservingNight.localDate))

  test("Equality must be natural") {
    forAll { (a: TwilightBoundedNight, b: TwilightBoundedNight) =>
      a.equals(b) shouldEqual Eq[TwilightBoundedNight].eqv(a, b)
    }
  }

  test("Show must be natural") {
    forAll { (o: TwilightBoundedNight) =>
      o.toString shouldEqual Show[TwilightBoundedNight].show(o)
    }
  }

  test("Start time consistent with ObservingNight") {
    forAll { (o: TwilightBoundedNight) =>
      o.start shouldBe >(o.toObservingNight.start)
      o.start shouldBe <(o.toObservingNight.end)
    }
  }

  test("End time consistent with ObservingNight") {
    forAll { (o: TwilightBoundedNight) =>
      o.end shouldBe >(o.toObservingNight.start)
      o.end shouldBe <(o.toObservingNight.end)
    }
  }

  // We use unsafe constructors since we know that for our Sites
  // there are twilight bounds for all nights and bound types.

  test("night.previous.next shouldEqual night") {
    forAll { (o: TwilightBoundedNight) =>
      o.previousUnsafe.nextUnsafe shouldEqual o
    }
  }

  test("night.next.previous shouldEqual night") {
    forAll { (o: TwilightBoundedNight) =>
      o.nextUnsafe.previousUnsafe shouldEqual o
    }
  }

  test("fromBoundTypeAndSiteAndLocalDate consistent") {
    forAll { (b: TwilightBoundType, s: Site, l: LocalDate) =>
      TwilightBoundedNight
        .fromBoundTypeAndSiteAndLocalDateUnsafe(b, s, l)
        .toLocalDate shouldEqual l
    }
  }

  test("fromBoundTypeAndSiteAndLocalDateTime consistent") {
    forAll { (b: TwilightBoundType, s: Site, l: LocalDateTime) =>
      val n  = TwilightBoundedNight.fromBoundTypeAndSiteAndLocalDateTimeUnsafe(b, s, l)
      val d  = l.toLocalDate
      val dʹ = if (l.toLocalTime.isBefore(LocalObservingNight.Start)) d else d.plusDays(1L)
      n.toLocalDate shouldEqual dʹ
    }
  }
}
