package shapeless.contrib.scalaz

import shapeless._

import scalaz._
import scalaz.syntax.apply._

trait Apply2[FH, OutT] {
  type Out
  def apply(fh: FH, outT: OutT): Out
}

object Apply2 {
  type Aux[FH, OutT, Out0] = Apply2[FH, OutT] { type Out = Out0 }

  implicit def apply2[F[_], H, T <: HList]
    (implicit
      app: Apply[F]
    ): Aux[F[H], F[T], F[H :: T]] =
    new Apply2[F[H], F[T]] {
      type Out = F[H :: T]
      def apply(fh: F[H], outT: F[T]): Out = app.apply2(fh, outT) { _ :: _ }
    }

  implicit def apply2a[F[_, _], A, H, T <: HList]
    (implicit
      app: Apply[({ type λ[x] = F[A, x] })#λ]
    ): Aux[F[A, H], F[A, T], F[A, H :: T]] =
    new Apply2[F[A, H], F[A, T]] {
      type Out = F[A, H :: T]
      def apply(fh: F[A, H], outT: F[A, T]): Out = app.apply2(fh, outT) { _ :: _ }
    }
}

trait Sequencer[L <: HList] {
  type Out
  def apply(in: L): Out
}

trait LowPrioritySequencer {
  type Aux[L <: HList, Out0] = Sequencer[L] { type Out = Out0 }

  implicit def consSequencerAux[FH, FT <: HList, OutT]
    (implicit
      st: Aux[FT, OutT],
      ap: Apply2[FH, OutT]
    ): Aux[FH :: FT, ap.Out] =
      new Sequencer[FH :: FT] {
        type Out = ap.Out
        def apply(in: FH :: FT): Out = ap(in.head, st(in.tail)) // un.TC.apply2(un(in.head), st(in.tail)) { _ :: _ }
      }
}

object Sequencer extends LowPrioritySequencer {
  implicit def nilSequencerAux[F[_]: Applicative]: Aux[HNil, F[HNil]] =
    new Sequencer[HNil] {
      type Out = F[HNil]
      def apply(in: HNil): F[HNil] = Applicative[F].pure(HNil: HNil)
    }

  implicit def singleSequencerAux[FH]
    (implicit
      un: Unapply[Functor, FH]
    ): Aux[FH :: HNil, un.M[un.A :: HNil]] =
      new Sequencer[FH :: HNil] {
        type Out = un.M[un.A :: HNil]
        def apply(in: FH :: HNil): Out = un.TC.map(un(in.head)) { _ :: HNil }
      }
}

trait SequenceFunctions {
  def sequence[L <: HList](in: L)(implicit seq: Sequencer[L]): seq.Out = seq(in)
}
