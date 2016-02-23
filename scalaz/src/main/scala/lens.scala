package shapeless.contrib.scalaz

trait LensOps[A, B] {

  def asScalaz: scalaz.Lens[A, B] =
    scalaz.LensFamily.lensg(asShapeless.set, asShapeless.get)

  def asShapeless: shapeless.Lens[A, B] =
    new shapeless.Lens[A, B] {
      def get(a: A): B = asScalaz.get(a)
      def set(a: A)(b: B): A = asScalaz.set(a, b)
    }

}

trait Lenses {

  implicit def scalazLensOps[A, B](l: scalaz.Lens[A, B]) = new LensOps[A, B] {
    override val asScalaz = l
  }

  implicit def shapelessLensOps[A, B](l: shapeless.Lens[A, B]) = new LensOps[A, B] {
    override val asShapeless = l
  }

}

// vim: expandtab:ts=2:sw=2
