// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.util

import cats.tests.CatsSuite
import gem.enum.StepType
import gem.enum.StepType._

final class EnumZipperSpec extends CatsSuite {
  test("focusIn on focus") {
    // move two positions to the right
    val z1 = EnumZipper.of[StepType].next.flatMap(_.next)
    assert(z1.map(_.focusIn(Gcal)).exists(_.focus === Gcal))
    assert(z1.map(_.focusIn(Gcal)).exists(_.lefts === List(Dark, Bias)))
    assert(z1.map(_.focusIn(Gcal)).exists(_.rights == List(Science, SmartGcal)))
  }
  test("focusIn on left") {
    // move two positions to the right
    val z1 = EnumZipper.of[StepType].next.flatMap(_.next)
    assert(z1.map(_.focusIn(Bias)).exists(_.focus === Bias))
    assert(z1.map(_.focusIn(Bias)).exists(_.lefts.isEmpty))
    assert(z1.map(_.focusIn(Bias)).exists(_.rights == List(Dark, Gcal, Science, SmartGcal)))
    assert(z1.map(_.focusIn(Dark)).exists(_.focus === Dark))
    assert(z1.map(_.focusIn(Dark)).exists(_.lefts === List(Bias)))
    assert(z1.map(_.focusIn(Dark)).exists(_.rights == List(Gcal, Science, SmartGcal)))
  }
  test("focusIn on right") {
    // move two positions to the right
    val z1 = EnumZipper.of[StepType].next.flatMap(_.next)
    assert(z1.map(_.focusIn(Science)).exists(_.focus === Science))
    assert(z1.map(_.focusIn(Science)).exists(_.lefts === List(Gcal, Dark, Bias)))
    assert(z1.map(_.focusIn(Science)).exists(_.rights == List(SmartGcal)))
    assert(z1.map(_.focusIn(SmartGcal)).exists(_.focus === SmartGcal))
    assert(z1.map(_.focusIn(SmartGcal)).exists(_.lefts === List(Science, Gcal, Dark, Bias)))
    assert(z1.map(_.focusIn(SmartGcal)).exists(_.rights.isEmpty))
  }
}
