package shapeless.contrib.scalaz

import org.specs2.scalaz.{Spec, ScalazMatchers}
import scalaz.scalacheck.ScalazArbitrary._
import scalaz.scalacheck.ScalazProperties.order
import scalaz.scalacheck.ScalaCheckBinding._
import scalaz._, Isomorphism._
import scalaz.std.AllInstances._
import org.scalacheck.Arbitrary

class CofreeTest extends Spec with ScalazMatchers{

  type CofreeList[A] = Cofree[List, A]

  val treeCofreeListIso: Tree <~> CofreeList =
    new IsoFunctorTemplate[Tree, CofreeList] {
      def to[A](tree: Tree[A]) =
        Cofree(tree.rootLabel, tree.subForest.map(to).toList)
      def from[A](c: CofreeList[A]) =
        Tree.node(c.head, c.tail.map(from(_)).toStream)
    }

  implicit class CofreeOps[A](self: Cofree[Option, A]){
    def toList: List[A] =
      self.head :: self.tail.map(_.toList).getOrElse(Nil)
  }

  implicit def CofreeArb[F[+_]: Functor, A](implicit A: Arbitrary[A], F: shapeless.Lazy[Arbitrary[F[Cofree[F, A]]]]): Arbitrary[Cofree[F, A]] =
    Apply[Arbitrary].apply2(A, F.value)(Cofree(_, _))

  implicit def CofreeListArb[A: Arbitrary]: Arbitrary[CofreeList[A]] =
    Functor[Arbitrary].map(implicitly[Arbitrary[Tree[A]]])(treeCofreeListIso.to)

  implicit def treeShow[A: Show]: Show[Tree[A]] = Show.showA

  //checkAll(order.laws[Cofree[Option, Int]])
  //checkAll(order.laws[Cofree[List, Int]])

  /*"treeCofreeListIso" ! prop{ (a: Tree[Int], b: Cofree[List, Int]) =>
    treeCofreeListIso.from(treeCofreeListIso.to(a)) must equal(a)
    treeCofreeListIso.to(treeCofreeListIso.from(b)) must equal(b)
  }*/

  /*"Equal[Cofree[List, Int]] is Equal[Tree[Int]]" ! prop{ (a: Cofree[List, Int], b: Cofree[List, Int]) =>
    Equal[Cofree[List, Int]].equal(a, b) must_== Equal[Tree[Int]].equal(treeCofreeListIso.from(a), treeCofreeListIso.from(b))
  }*/

  /*"Order[Cofree[Option, Int]] is Order[List[Int]]" ! prop{ (a: Cofree[Option, Int], b: Cofree[Option, Int]) =>
    val aa = a.toList
    val bb = b.toList
    Equal[Cofree[Option, Int]].equal(a, b) must_== Equal[List[Int]].equal(aa, bb)
    Order[Cofree[Option, Int]].order(a, b) must_== Order[List[Int]].order(aa, bb)
  }*/

  "Show[Cofree[Option, Int]]" ! prop{ a: Cofree[Option, Int] =>
    Show[Cofree[Option, Int]].shows(a) must_== a.toString
  }

}

