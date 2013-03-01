package shapeless.contrib

import shapeless._

trait Product[+C[_], F, T <: HList] {
  def F: C[F]
  def T: C[T]

  type λ = F :: T
}

trait Isomorphic[+C[_], A, B] {
  def B: C[B]
  def iso: Iso[A, B]
}

// vim: expandtab:ts=2:sw=2
