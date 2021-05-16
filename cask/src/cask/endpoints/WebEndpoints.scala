package cask.endpoints

import cask.router.HttpEndpoint
import cask.model.{Request, Response}
import cask.router.{ArgReader, Result}

import collection.JavaConverters._


trait WebEndpoint extends HttpEndpoint[Response.Raw, Seq[String]]{
  type InputParser[T] = QueryParamReader[T]
  def wrapFunction(ctx: Request,
                       delegate: Delegate): Result[Response.Raw] = {
    delegate(WebEndpoint.buildMapFromQueryParams(ctx))
  }
  def wrapPathSegment(s: String) = Seq(s)
}
object WebEndpoint{
  def buildMapFromQueryParams(ctx: Request) = {
    val b = Map.newBuilder[String, Seq[String]]
    val queryParams = ctx.exchange.getQueryParameters
    for(k <- queryParams.keySet().iterator().asScala){
      val deque = queryParams.get(k)
      val arr = new Array[String](deque.size)
      deque.toArray(arr)
      b += (k -> (arr: Seq[String]))
    }
    b.result()
  }
}
class get(val path: String, override val subpath: Boolean = false) extends WebEndpoint{
  val methods = Seq("get")
}
class post(val path: String, override val subpath: Boolean = false) extends WebEndpoint{
  val methods = Seq("post")
}
class put(val path: String, override val subpath: Boolean = false) extends WebEndpoint{
  val methods = Seq("put")
}
class patch(val path: String, override val subpath: Boolean = false) extends WebEndpoint{
  val methods = Seq("patch")
}
class delete(val path: String, override val subpath: Boolean = false) extends WebEndpoint{
  val methods = Seq("delete")
}
class route(val path: String, val methods: Seq[String], override val subpath: Boolean = false) extends WebEndpoint
class options(val path: String, override val subpath: Boolean = false) extends WebEndpoint{
  val methods = Seq("options")
}

abstract class QueryParamReader[T]
  extends ArgReader[Seq[String], T, cask.model.Request]{
  def arity: Int
  def read(ctx: cask.model.Request, label: String, v: Seq[String]): T
}
object QueryParamReader{
  class SimpleParam[T](f: String => T) extends QueryParamReader[T]{
    def arity = 1
    def read(ctx: cask.model.Request, label: String, v: Seq[String]): T = f(v.head)
  }

  implicit object StringParam extends SimpleParam[String](x => x)
  implicit object BooleanParam extends SimpleParam[Boolean](_.toBoolean)
  implicit object ByteParam extends SimpleParam[Byte](_.toByte)
  implicit object ShortParam extends SimpleParam[Short](_.toShort)
  implicit object IntParam extends SimpleParam[Int](_.toInt)
  implicit object LongParam extends SimpleParam[Long](_.toLong)
  implicit object DoubleParam extends SimpleParam[Double](_.toDouble)
  implicit object FloatParam extends SimpleParam[Float](_.toFloat)
  implicit def SeqParam[T: QueryParamReader]: QueryParamReader[Seq[T]] = new QueryParamReader[Seq[T]]{
    def arity = 1
    def read(ctx: cask.model.Request, label: String, v: Seq[String]): Seq[T] = {
      v.map(x => implicitly[QueryParamReader[T]].read(ctx, label, Seq(x)))
    }
  }
  implicit def OptionParam[T: QueryParamReader]: QueryParamReader[Option[T]] = new QueryParamReader[Option[T]]{
    def arity = 1
    def read(ctx: cask.model.Request, label: String, v: Seq[String]): Option[T] = {
      v.headOption.map(x => implicitly[QueryParamReader[T]].read(ctx, label, Seq(x)))
    }
  }
  implicit def paramReader[T: ParamReader]: QueryParamReader[T] = new QueryParamReader[T] {
    override def arity = 0

    override def read(ctx: cask.model.Request, label: String, v: Seq[String]) = {
      implicitly[ParamReader[T]].read(ctx, label, v)
    }
  }

}
