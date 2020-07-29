// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem

import cats.Order
import cats.implicits._

/**
  * Parallax in mas
  * Store the value in mas to avoid rounding errors
  */
case class Parallax(mas: BigDecimal) extends Serializable

object Parallax {

  /**
    * The `No parallax`
    * @group Constructors
    */
  val Zero: Parallax = Parallax(0)

  /** @group Typeclass Instances */
  implicit val order: Order[Parallax] =
    Order.by(_.mas)

}
