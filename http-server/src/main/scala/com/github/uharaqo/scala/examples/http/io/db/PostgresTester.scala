package com.github.uharaqo.scala.examples.http.io.db

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts

import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.update.Update.apply
import javax.xml.stream.util.EventReaderDelegate
import cats.free.Free

@main
def main() = {
  import cats.effect.unsafe.implicits.global
  val v = PostgresTester().upsert(AggregateRecord("user", "John", 5))
  println(v.unsafeRunSync())
}

/** CREATE TABLE events (agg_type text, agg_id text, ver integer, event text, PRIMARY KEY (agg_type, agg_id, ver));
  * CREATE TABLE aggregates (agg_type text, agg_id text, curr_ver integer, PRIMARY KEY (agg_type, agg_id));
  */
class PostgresTester {
  private val xa =
    Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:postgres",
    )
  private val selectAggregate = "SELECT curr_ver FROM aggregates WHERE agg_type = ? AND agg_id = ?"
  private val insertAggregate = "INSERT INTO aggregates (agg_type, agg_id, curr_ver) values (?, ?, ?)"
  private val updateAggregate = "UPDATE aggregates SET curr_ver = ? WHERE agg_type = ? AND agg_id = ?"
  private val insertEvent     = "INSERT INTO events (agg_type, agg_id, ver, event) values (?, ?, ?, ?)"

  private val events = List(
    EventRecord("user", "John", 1, "Created"),
    EventRecord("user", "John", 2, "Added"),
    EventRecord("user", "Tom", 1, "Created"),
  )
  private val aggregates = events.map(e => AggregateRecord(e.aggType, e.aggId, e.ver))

  def upsert(record: AggregateRecord): IO[Boolean] = {
    upsert(
      record.currVer,
      sql"SELECT curr_ver FROM aggregates WHERE agg_type = ${record.aggType} AND agg_id = ${record.aggId}".query[Int].option,
      Update[AggregateRecord](insertAggregate).run(record),
      Update[(Int, String, String)](updateAggregate).run((record.currVer, record.aggType, record.aggId)),
    )
  }

  def upsert(
    nextVer: Int,
    select: ConnectionIO[Option[Int]],
    insert: ConnectionIO[Int],
    update: ConnectionIO[Int]
  ): IO[Boolean] = {
    val sqls =
      for {
        currVer <- select
        updated <- currVer match {
          case None       => if (nextVer == 1) insert else Free.pure(0)
          case Some(curr) => if (nextVer == curr + 1) update else Free.pure(0)
        }
        // update
        // inserted <- if (updated == 0) insert else Free.pure(0)
      } yield updated

    sqls.transact(xa).map(_ > 0)
  }

  def insertEventsInTransaction(): IO[Int] = {
    val sqls =
      (aggregates.map(a => Update[AggregateRecord](insertAggregate).run(a))
        ++ events.map(e => Update[EventRecord](insertEvent).run(e))).traverse(x => x)

    sqls.transact(xa).map(_.sum)
  }
}

class PostgresTesterQuill {
  import io.getquill.{ idiom => _, _ }
  import io.getquill.doobie.DoobieContext

  // private val xa =
  //   Transactor.fromDriverManager[IO](
  //     "org.postgresql.Driver",
  //     "jdbc:postgresql:postgres",
  //   )
  // private val selectAggregate = "SELECT curr_ver FROM aggregates WHERE agg_type = ? AND agg_id = ?"
  // private val insertAggregate = "INSERT INTO aggregates (agg_type, agg_id, curr_ver) values (?, ?, ?)"
  // private val updateAggregate = "UPDATE aggregates SET curr_ver = ? WHERE agg_type = ? AND agg_id = ?"
  // private val insertEvent     = "INSERT INTO events (agg_type, agg_id, ver, event) values (?, ?, ?, ?)"

  // private val events = List(
  //   EventRecord("user", "John", 1, "Created"),
  //   EventRecord("user", "John", 2, "Added"),
  //   EventRecord("user", "Tom", 1, "Created"),
  // )
  // private val aggregates = events.map(e => AggregateRecord(e.aggType, e.aggId, e.ver))

  // def upsert(record: AggregateRecord): IO[Boolean] = {
  //   upsert(
  //     record.currVer,
  //     sql"SELECT curr_ver FROM aggregates WHERE agg_type = ${record.aggType} AND agg_id = ${record.aggId}".query[Int].option,
  //     Update[AggregateRecord](insertAggregate).run(record),
  //     Update[(Int, String, String)](updateAggregate).run((record.currVer, record.aggType, record.aggId)),
  //   )
  // }

  // def upsert(
  //   nextVer: Int,
  //   select: ConnectionIO[Option[Int]],
  //   insert: ConnectionIO[Int],
  //   update: ConnectionIO[Int]
  // ): IO[Boolean] = {
  //   val sqls =
  //     for {
  //       currVer <- select
  //       updated <- currVer match {
  //         case None       => if (nextVer == 1) insert else Free.pure(0)
  //         case Some(curr) => if (nextVer == curr + 1) update else Free.pure(0)
  //       }
  //       // update
  //       // inserted <- if (updated == 0) insert else Free.pure(0)
  //     } yield updated
  //   sqls.transact(xa).map(_ > 0)
  // }

  // def insertEventsInTransaction(): IO[Int] = {
  //   val sqls =
  //     (aggregates.map(a => Update[AggregateRecord](insertAggregate).run(a))
  //       ++ events.map(e => Update[EventRecord](insertEvent).run(e))).traverse(x => x)

  //   sqls.transact(xa).map(_.sum)
  // }
}

case class EventRecord(aggType: String, aggId: String, ver: Int, event: String)
case class AggregateRecord(aggType: String, aggId: String, currVer: Int)
