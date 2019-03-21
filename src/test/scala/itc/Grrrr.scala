package grrr


// Input is a GraphQL AST node
trait Ast


trait Decode[A] {
  def decode(ast: Ast): Decode.Result[A]
  def map[B](f: A => B): Decode[B]
}

object Decode {

  case class DecodeFailure(msg: String)

  type Result[A] = Either[DecodeFailure, A]

  def listOf[A](decode: Decode[A]): Decode[List[A]] = ???

}


// Output is just Json so we can use Circe for that
trait Encode[A]
object Encode {
  def listOf[A](encode: Encode[A]): Encode[List[A]] = ???
  def optionOf[A](encode: Encode[A]): Encode[Option[A]] = ???
}

trait Type {
  def name: String
  def description: Option[String]
  def isRequired: Boolean = true
  def toSchemaString: String = if (isRequired) s"$name!" else name
}

trait InputType[A] extends Type {
  def decode: Encode[A]
}

trait OutputType[A] extends Type {
  def encode: Encode[A]
}

case class ListInputType[A](inputType: InputType[A]) extends InputType[List[A]] {
  def name        = inputType.name
  def description = inputType.description
  def decode      = Encode.listOf(inputType.decode)
  override def toSchemaString: String = if (isRequired) s"[$name]!" else s"[$name]"
}

case class OptionInputType[A](inputType: InputType[A]) extends InputType[Option[A]] {
  def name        = inputType.name
  def description = inputType.description
  def decode      = Encode.optionOf(inputType.decode)
  override def isRequired = false
}

