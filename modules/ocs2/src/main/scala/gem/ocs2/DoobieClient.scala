// Copyright (c) 2016-2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.ocs2

import cats.effect.{ Bracket, ContextShift, Effect, IO }
import cats.implicits._
import doobie._, doobie.implicits._
import gem.dao.meta._
import doobie.postgres.implicits._
import java.util.logging.{ Level, Logger }
import scala.concurrent.ExecutionContext

/** Shared support for import applications using Doobie. */
trait DoobieClient extends ProgramIdMeta with IndexMeta {

  implicit val ioContextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  def configureLogging[M[_]: Effect]: M[Unit] =
    Effect[M].delay(
      List("edu.gemini.spModel.type.SpTypeUtil")
        .map(Logger.getLogger)
        .foreach(_.setLevel(Level.OFF))
    )

  def ignoreUniqueViolation(fa: ConnectionIO[Int])(implicit ev: Bracket[ConnectionIO, Throwable]): ConnectionIO[Int] =
    for {
      s <- HC.setSavepoint
      n <- ev.guarantee(fa.onUniqueViolation(HC.rollback(s).as(0)))(HC.releaseSavepoint(s))
    } yield n

}
