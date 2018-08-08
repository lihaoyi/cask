package test.cask
import io.undertow.Undertow
import io.undertow.server.handlers.BlockingHandler
import utest._

object ExampleTests extends TestSuite{
  def test[T](example: cask.main.BaseMain)(f: String => T): T = {
    val server = Undertow.builder
      .addHttpListener(8080, "localhost")
      .setHandler(new BlockingHandler(example.defaultHandler))
      .build
    server.start()
    val res =
      try f("http://localhost:8080")
      finally server.stop()
    res
  }

  val tests = Tests{
    'MinimalApplication - test(MinimalApplication){ host =>
      val success = requests.get(host)

      success.text() ==> "Hello World!"
      success.statusCode ==> 200

      requests.get(s"$host/doesnt-exist").statusCode ==> 404

      requests.post(s"$host/do-thing", data = "hello").text() ==> "olleh"

      requests.get(s"$host/do-thing").statusCode ==> 404
    }
    'MinimalApplication2 - test(MinimalMain){ host =>
      val success = requests.get(host)

      success.text() ==> "Hello World!"
      success.statusCode ==> 200

      requests.get(s"$host/doesnt-exist").statusCode ==> 404

      requests.post(s"$host/do-thing", data = "hello").text() ==> "olleh"

      requests.get(s"$host/do-thing").statusCode ==> 404
    }
    'VariableRoutes - test(VariableRoutes){ host =>
      val noIndexPage = requests.get(host)
      noIndexPage.statusCode ==> 404

      requests.get(s"$host/user/lihaoyi").text() ==> "User lihaoyi"

      requests.get(s"$host/user").statusCode ==> 404


      requests.get(s"$host/post/123?param=xyz&param=abc").text() ==>
        "Post 123 ArrayBuffer(xyz, abc)"

      requests.get(s"$host/post/123").text() ==>
        """Missing argument: (param: Seq[String])
          |
          |Arguments provided did not match expected signature:
          |
          |showPost
          |  postId  Int
          |  param  Seq[String]
          |
          |""".stripMargin

      requests.get(s"$host/path/one/two/three").text() ==>
        "Subpath List(one, two, three)"
    }

    'StaticFiles - test(StaticFiles){ host =>
      requests.get(s"$host/static/example.txt").text() ==>
        "the quick brown fox jumps over the lazy dog"
    }

    'RedirectAbort - test(RedirectAbort){ host =>
      val resp = requests.get(s"$host/")
      resp.statusCode ==> 401
      resp.history.get.statusCode ==> 301
    }

    'FormJsonPost - test(FormJsonPost){ host =>
      requests.post(s"$host/json", data = """{"value1": true, "value2": [3]}""").text() ==>
        "OK true Vector(3)"

      requests.post(
        s"$host/form",
        data = Seq("value1" -> "hello", "value2" -> "1", "value2" -> "2")
      ).text() ==>
      "OK FormValue(hello,null) List(1, 2)"

      val resp = requests.post(
        s"$host/upload",
        data = requests.MultiPart(
          requests.MultiItem("image", "...", "my-best-image.txt")
        )
      )
      resp.text() ==> "my-best-image.txt"
    }
    'Decorated - test(Decorated){ host =>
      requests.get(s"$host/hello/woo").text() ==> "woo31337"
      requests.get(s"$host/internal/boo").text() ==> "boo[haoyi]"
      requests.get(s"$host/internal-extra/goo").text() ==> "goo[haoyi]31337"

    }
    'Decorated2 - test(Decorated2){ host =>
      requests.get(s"$host/hello/woo").text() ==> "woo31337"
      requests.get(s"$host/internal-extra/goo").text() ==> "goo[haoyi]31337"
      requests.get(s"$host/ignore-extra/boo").text() ==> "boo[haoyi]"

    }
    'TodoMvcApi - test(TodoMvcApi){ host =>
      requests.get(s"$host/list/all").text() ==>
        """[{"checked":true,"text":"Get started with Cask"},{"checked":false,"text":"Profit!"}]"""
      requests.get(s"$host/list/active").text() ==>
        """[{"checked":false,"text":"Profit!"}]"""
      requests.get(s"$host/list/completed").text() ==>
        """[{"checked":true,"text":"Get started with Cask"}]"""

      requests.post(s"$host/toggle/1")

      requests.get(s"$host/list/all").text() ==>
        """[{"checked":true,"text":"Get started with Cask"},{"checked":true,"text":"Profit!"}]"""

      requests.get(s"$host/list/active").text() ==>
        """[]"""

      requests.post(s"$host/add", data = "new Task")

      requests.get(s"$host/list/active").text() ==>
        """[{"checked":false,"text":"new Task"}]"""

      requests.post(s"$host/delete/0")

      requests.get(s"$host/list/active").text() ==>
        """[]"""
    }
    'TodoMvcDb - test(TodoMvcDb){ host =>
      requests.get(s"$host/list/all").text() ==>
        """[{"id":1,"checked":true,"text":"Get started with Cask"},{"id":2,"checked":false,"text":"Profit!"}]"""
      requests.get(s"$host/list/active").text() ==>
        """[{"id":2,"checked":false,"text":"Profit!"}]"""
      requests.get(s"$host/list/completed").text() ==>
        """[{"id":1,"checked":true,"text":"Get started with Cask"}]"""

      requests.post(s"$host/toggle/2")

      requests.get(s"$host/list/all").text() ==>
        """[{"id":1,"checked":true,"text":"Get started with Cask"},{"id":2,"checked":true,"text":"Profit!"}]"""

      requests.get(s"$host/list/active").text() ==>
        """[]"""

      requests.post(s"$host/add", data = "new Task")

      requests.get(s"$host/list/active").text() ==>
        """[{"id":3,"checked":false,"text":"new Task"}]"""

      requests.post(s"$host/delete/3")

      requests.get(s"$host/list/active").text() ==>
        """[]"""
    }

    'Compress - test(Compress){ host =>
      val expected = "Hello World! Hello World! Hello World!"
      requests.get(s"$host").text() ==> expected
      assert(
        requests.get(s"$host", autoDecompress = false).text().length < expected.length
      )

    }

    'Compress2Main - test(Compress2Main) { host =>
      val expected = "Hello World! Hello World! Hello World!"
      requests.get(s"$host").text() ==> expected
      assert(
        requests.get(s"$host", autoDecompress = false).text().length < expected.length
      )
    }

    'Compress3Main - test(Compress3Main){ host =>
      val expected = "Hello World! Hello World! Hello World!"
      requests.get(s"$host").text() ==> expected
      assert(
        requests.get(s"$host", autoDecompress = false).text().length < expected.length
      )

    }
  }
}