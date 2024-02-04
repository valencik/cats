/*
 * Copyright (c) 2015 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cats.bench

import cats.{Eval, Traverse, TraverseFilter}
import org.openjdk.jmh.annotations.{Benchmark, Param, Scope, Setup, State}
import org.openjdk.jmh.infra.Blackhole
import scala.collection.immutable.Seq

@State(Scope.Benchmark)
class TraverseDirectlyBench {
  val seqT: Traverse[Seq] = Traverse[Seq]
  val seqTFilter: TraverseFilter[Seq] = TraverseFilter[Seq]

  // the unit of CPU work per iteration
  private[this] val Work: Long = 10

  private[this] case object Failure extends RuntimeException

  @Param(Array("10000"))
  var length: Int = _

  var seq: Seq[Int] = _

  @Setup
  def setup(): Unit = {
    seq = 0.until(length).toSeq
  }

  @Benchmark
  def traverseSeq(bh: Blackhole) = {
    val result = seqT.traverse(seq) { i =>
      Eval.later {
        Blackhole.consumeCPU(Work)
        i * 2
      }
    }

    bh.consume(result.value)
  }

  @Benchmark
  def traverseSeqError(bh: Blackhole) = {
    val result = seqT.traverse(seq) { i =>
      Eval.later {
        Blackhole.consumeCPU(Work)

        if (i == length * 0.3) {
          throw Failure
        }

        i * 2
      }
    }

    try {
      bh.consume(result.value)
    } catch {
      case Failure => ()
    }
  }

  @Benchmark
  def traverse_Seq(bh: Blackhole) = {
    val result = seqT.traverse_(seq) { i =>
      Eval.later {
        Blackhole.consumeCPU(Work)
        i * 2
      }
    }

    bh.consume(result.value)
  }

  @Benchmark
  def traverseFilterSeq(bh: Blackhole) = {
    val result = seqTFilter.traverseFilter(seq) { i =>
      Eval.later {
        Blackhole.consumeCPU(Work)
        if (i % 2 == 0) Some(i * 2) else None
      }
    }

    bh.consume(result.value)
  }

  @Benchmark
  def mapSeq(bh: Blackhole) = {
    val results = seq.map { i =>
      val inner = Eval.later {
        Blackhole.consumeCPU(Work)
        i * 2
      }

      // we just want to force the allocation to level the playing field
      inner.value
    }

    bh.consume(results)
  }

}
