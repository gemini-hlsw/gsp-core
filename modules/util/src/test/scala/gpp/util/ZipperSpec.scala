// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.util

import cats.data.NonEmptyList
import cats.tests.CatsSuite
import cats.kernel.laws.discipline.EqTests
import cats.laws.discipline.{ FunctorTests, TraverseTests }
import cats.laws.discipline.arbitrary._
import monocle.law.discipline.TraversalTests
import arb.ArbZipper._

/**
  * Tests the Zipper typeclasses
  */
final class ZipperSpec extends CatsSuite {
  test("support modify") {
    forAll { (l: List[Int], r: List[Int]) =>
      val z = Zipper(l, 0, r)
      assert(z.modify(_ => 1) !== z)
    }
  }
  test("Zipper length") {
    forAll { (l: Zipper[Int]) =>
      assert(l.length > 0)
    }
  }
  test("supports previous") {
    val z1 = Zipper(List(0, 1, 2), 3, List(4, 5, 6))
    assert(z1.previous === Zipper(List(0, 1), 2, List(3, 4, 5, 6)).some)
    val z2 = Zipper(List(0), 1, List(2, 3, 4, 5, 6))
    assert(z2.previous === Zipper(Nil, 0, List(1, 2, 3, 4, 5, 6)).some)
    val z3 = Zipper(Nil, 1, List(2, 3, 4, 5, 6))
    assert(z3.previous.isEmpty)
    val z4 = Zipper(List(0, 1, 2), 3, Nil)
    assert(z4.previous === Zipper(List(0, 1), 2, List(3)).some)
  }
  test("supports next") {
    val z1 = Zipper(List(0, 1, 2), 3, List(4, 5, 6))
    assert(z1.next === Zipper(List(0, 1, 2, 3), 4, List(5, 6)).some)
    val z2 = Zipper(List(0), 1, List(2, 3, 4, 5, 6))
    assert(z2.next === Zipper(List(0, 1), 2, List(3, 4, 5, 6)).some)
    val z3 = Zipper(Nil, 1, Nil)
    assert(z3.next.isEmpty)
    val z4 = Zipper(List(0, 1, 2), 3, List(4))
    assert(z4.next === Zipper(List(0, 1, 2, 3), 4, Nil).some)
  }
  test("previous/next cancel each other") {
    forAll { (l: Zipper[Int]) =>
      if (l.lefts.nonEmpty)
        assert(l.previous.flatMap(_.next) === l.some)
      if (l.rights.nonEmpty)
        assert(l.next.flatMap(_.previous) === l.some)
    }
  }
  test("toNel") {
    forAll { (nel: NonEmptyList[Int]) =>
      assert(Zipper.fromNel(nel).toNel === nel)
    }
  }
  test("toList") {
    forAll { (h: Int, l: List[Int]) =>
      assert(Zipper.fromNel(NonEmptyList(h, l)).toList === h :: l)
    }
  }
  test("support exists") {
    forAll { (l: List[Int], r: List[Int]) =>
      val u = Zipper(l, 0, r)
      val e = u.exists(_ === 0)
      assert(e)
    }
  }
  test("support find") {
    forAll { (l: List[Int], r: List[Int]) =>
      val u = Zipper(l, 0, r)
      val e = u.find(_ === 0)
      assert(e.isDefined)
    }
  }
  test("support find focus I") {
    forAll { (l: List[Int], r: List[Int]) =>
      val u = Zipper(l, 0, r)
      val e = u.findFocus(_ === 0)
      assert(e.exists(_.focus === 0) === true)
    }
  }
  test("support find focus II") {
    forAll { (l: List[Int], r: List[Int]) =>
      val u = Zipper(l, 0, r)
      val e = u.findFocus(x => l.headOption.forall(x === _))
      val m = l.headOption.forall(x => e.exists(_.focus === x))
      assert(m)
    }
  }
  test("support find focus III") {
    forAll { (l: List[Int], r: List[Int]) =>
      val u = Zipper(l, 0, r)
      val e = u.findFocus(x => r.headOption.forall(x === _))
      val m = l.forall(x => e.exists(_.focus === x)) ||
        r.headOption.forall(x => e.exists(_.focus === x))
      assert(m)
    }
  }

  checkAll("Functor[Zipper]", FunctorTests[Zipper].functor[Int, Int, Int])
  checkAll("Traversable[Zipper]",
           TraverseTests[Zipper].traverse[Int, Int, Int, Int, Option, Option]
  )
  checkAll("Eq[Zipper]", EqTests[Zipper[Int]].eqv)
  checkAll("Zipper.zipperT", TraversalTests(Zipper.zipperT[Int]))
  // The zippers are unlawful
  // checkAll("Zipper.filterValue", TraversalTests(Zipper.unsafeFilterZ[Int](_ % 2 === 0)))
}
