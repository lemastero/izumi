package izumi.fundamentals.graphs.tools

import izumi.fundamentals.graphs.struct.IncidenceMatrix
import izumi.fundamentals.graphs.tools.GC.{GCInput, GCOutput}

trait GC[N] {
  def collect(input: GCInput[N]): Either[Nothing, GCOutput[N]]
}

object GC {
  final case class WeakEdge[N](predcessor: N, successor: N)
  final case class GCInput[N](predcessorMatrix: IncidenceMatrix[N], roots: Set[N], weakSP: Set[WeakEdge[N]])

  final case class GCOutput[N](predcessorMatrix: IncidenceMatrix[N], removed: Set[N])

  class GCTracer[N] extends GC[N] {
    override def collect(input: GCInput[N]): Either[Nothing, GCOutput[N]] = {
      val missingRoots = input.roots.diff(input.predcessorMatrix.links.keySet)
      val withRoots = IncidenceMatrix(input.predcessorMatrix.links ++ missingRoots.map(r => (r, Set.empty[N])))
      val reachable = new Tracer[N].trace(withRoots, input.weakSP.map(w => (w.successor, w.predcessor)), input.roots)
      val unreachable = withRoots.links.keySet.diff(reachable)
      Right(GCOutput(withRoots.without(unreachable), unreachable))

      //      if (missingRoots.isEmpty) {
//      } else {
//        Left(GCError.MissingRoots(missingRoots))
//      }
    }

  }
}
