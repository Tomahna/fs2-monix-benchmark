package example

import cats.Id
import cats.effect.IO
import fs2.Stream
import cats.syntax.all._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import org.openjdk.jmh.annotations.Benchmark

class Benchmark1 {
  @Benchmark
  def listBaseline(): Unit = {
    val max = 1000000
    val baselineListBuild = (1 to max).toList
  }

  @Benchmark
  def listMonix(): Unit = {
    val max = 1000000

    val source: Observable[Long] = Observable.range(0, max, 1)

    source
      .foldLeftL(List.empty[Long]) { case (elems, elem) => elem :: elems }
      .void
      .runSyncUnsafe()
  }

  @Benchmark
  def listFs2IO(): Unit = {
    val max = 1000000

    val source: Stream[IO, Int] = Stream.range[IO](0, max, 1)
    source
      .fold(List.empty[Long]) { case (elems, elem) => elem :: elems }
      .compile
      .drain
      .unsafeRunSync()
  }

  @Benchmark
  def listFs2IOToList(): Unit = {
    val max = 1000000

    val source: fs2.Stream[IO, Int] = Stream.iterable(0 to max)
    source.compile.toList.unsafeRunSync()
  }
}
