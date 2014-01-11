package shapeless.contrib

package object scalaz extends Instances with Functions with Lifts with Lenses with FreeInstances with CofreeInstances{

  object instances extends Instances

  object functions extends Functions

  object lift extends Lifts

  object lenses extends Lenses

  object binary extends BinarySyntax

  object free extends FreeInstances

  object cofree extends CofreeInstances

}

// vim: expandtab:ts=2:sw=2
