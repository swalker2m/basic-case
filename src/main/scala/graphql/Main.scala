// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql

import basic.graphql.schema._
import cats._
import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.Json
import java.util.concurrent.{ Executors, ExecutorService }
import sangria.schema._
import org.http4s._
import org.http4s.dsl._
import org.http4s.headers.Location
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext
import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

object Main extends IOApp {

  /** Resource yielding an `ExecutionContext` backed by an unbounded thread pool. */
  def cachedThreadPool[F[_]](
    implicit sf: Sync[F]
  ): Resource[F, ExecutionContext] = {
    val alloc = sf.delay(Executors.newCachedThreadPool)
    val free  = (es: ExecutorService) => sf.delay(es.shutdown())
    Resource.make(alloc)(free).map(ExecutionContext.fromExecutor)
  }

  // Construct a GraphQL implementation based on our Sangria definitions.
  def graphQL[F[_]: Effect: ContextShift: Logger](
    blockingContext: ExecutionContext
  ): GraphQL[F] =
    GraphQL[F](
      Schema(QueryType[F]),
      Applicative[F].unit, // NO CONTEXT NEEDED FOR NOW
      blockingContext
    )

  // Playground or else redirect to playground
  def playgroundOrElse[F[_]: Sync: ContextShift](
    blockingContext: ExecutionContext
  ): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._
    HttpRoutes.of[F] {

      case GET -> Root / "playground.html" =>
        StaticFile
          .fromResource[F]("/assets/playground.html", blockingContext)
          .getOrElseF(NotFound())

      case _ =>
        PermanentRedirect(Location(Uri.uri("/playground.html")))

    }
  }

  /** An `HttpRoutes` that maps the standard `/graphql` path to a `GraphQL` instace. */
  def graphQLRoutes[F[_]: Sync: ContextShift](
    graphQL: GraphQL[F]
  ): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / "graphql" ⇒
        req.as[Json].flatMap(graphQL.query).flatMap {
          case Right(json) => Ok(json)
          case Left(json)  => BadRequest(json)
        }
    }
  }

  // Resource that mounts the given `routes` and starts a server.
  def server[F[_]: ConcurrentEffect: ContextShift: Timer](
    port:   Int,
    routes: HttpRoutes[F]
  ): Resource[F, Server[F]] =
    BlazeServerBuilder[F]
      .bindHttp(port, "0.0.0.0")
      .withHttpApp(routes.orNotFound)
      .resource

  // Resource that constructs our final server.
  def resource[F[_]: ConcurrentEffect: ContextShift: Timer](port: Int)(
    implicit L: Logger[F]
  ): Resource[F, Server[F]] =
    for {
      bec <- cachedThreadPool[F]
      gql  = graphQL[F](bec)
      rts  = graphQLRoutes[F](gql) <+> playgroundOrElse(bec)
      svr <- server[F](port, rts)
    } yield svr

  // Our entry point starts the server and blocks forever.
  def run(args: List[String]): IO[ExitCode] = {
    implicit val log = Slf4jLogger.unsafeCreate[IO]
    val port = sys.env.get("PORT").map(_.toInt).getOrElse(8080)
    resource[IO](port).use(_ => IO.never.as(ExitCode.Success))
  }

}

