package basic

import cats.effect._
import cats.implicits._
import gem.math._

object Main extends IOApp {

  val cs: Coordinates =
    Coordinates.fromHmsDms.unsafeGet("17 57 48.49803 +04 41 36.2072")

  def run(args: List[String]): IO[ExitCode] =
    IO(println(cs)).as(ExitCode.Success)

}