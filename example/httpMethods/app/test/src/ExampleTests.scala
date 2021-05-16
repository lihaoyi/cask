package app
import io.undertow.Undertow

import utest._

object ExampleTests extends TestSuite{
  def withServer[T](example: cask.main.Main)(f: String => T): T = {
    val server = Undertow.builder
      .addHttpListener(8081, "localhost")
      .setHandler(example.defaultHandler)
      .build
    server.start()
    val res =
      try f("http://localhost:8081")
      finally server.stop()
    res
  }

  val tests = Tests{
    test("HttpMethods") - withServer(HttpMethods){ host =>
      requests.post(s"$host/login").text() ==> "do_the_login"
      requests.get(s"$host/login").text() ==> "show_the_login_form"
      requests.put(s"$host/login", check = false).statusCode ==> 405
      requests.delete(s"$host/session").text() ==> "delete_the_session"
      requests.get.copy(verb="secretmethod")(s"$host/session").text() ==> "security_by_obscurity"
      requests.options(s"$host/api").text() ==> "allow_cors"
    }
  }
}
