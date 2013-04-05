package shapeless.contrib.scalaz

trait Lens{

  def shapeless2scalaz[A, B](shapelessLens: shapeless.Lens[A, B]): scalaz.Lens[A, B] =
    scalaz.LensFamily.lensFamilyg(shapelessLens.set, shapelessLens.get)

  def scalaz2shapeless[A, B](scalazLens: scalaz.Lens[A, B]): shapeless.Lens[A, B] =
    new shapeless.Lens[A, B]{
      def get(a: A): B = scalazLens.get(a)
      def set(a: A)(b: B): A = scalazLens.set(a, b)
    }

}
