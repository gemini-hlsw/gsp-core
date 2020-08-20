// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.sql

/** Minimal Quantity classes to support reading from the enum
  * tables.
  */
sealed trait Quantity extends Product with Serializable {
  def toBigDecimal: BigDecimal
}

object Quantity {
  final case class Nm(toBigDecimal: BigDecimal) extends Quantity

  def fromNm(bd: BigDecimal): Quantity.Nm =
    Nm(bd)
}
