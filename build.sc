import mill._, scalalib._, scalajslib._, publish._

import $file.example.compress.build
import $file.example.compress2.build
import $file.example.compress3.build
import $file.example.cookies.build
import $file.example.decorated.build
import $file.example.decorated2.build
import $file.example.endpoints.build
import $file.example.formJsonPost.build
import $file.example.httpMethods.build
import $file.example.minimalApplication.build
import $file.example.minimalApplication2.build
import $file.example.redirectAbort.build
import $file.example.scalatags.build
import $file.example.staticFiles.build
import $file.example.staticFiles2.build
import $file.example.todo.build
import $file.example.todoApi.build
import $file.example.todoDb.build
import $file.example.twirl.build
import $file.example.variableRoutes.build
import $file.example.websockets.build
import $file.example.websockets2.build
import $file.example.websockets3.build
import $file.example.websockets4.build
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version_mill0.9:0.1.1`
import de.tobiasroeser.mill.vcs.version.VcsVersion

val scala213 = "2.13.5"
val scala3 = "3.0.0"
val dottyCustomVersion = Option(sys.props("dottyVersion"))

trait CaskModule extends CrossScalaModule with PublishModule{
  def isDotty = crossScalaVersion.startsWith("3")

  def publishVersion = VcsVersion.vcsState().format()

  def pomSettings = PomSettings(
    description = artifactName(),
    organization = "com.lihaoyi",
    url = "https://github.com/lihaoyi/cask",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("lihaoyi", "cask"),
    developers = Seq(
      Developer("lihaoyi", "Li Haoyi","https://github.com/lihaoyi")
    )
  )
}

class CaskMainModule(val crossScalaVersion: String) extends CaskModule {
  def ivyDeps = T{
    Agg(
      ivy"io.undertow:undertow-core:2.2.3.Final",
      ivy"com.lihaoyi::upickle:1.3.13"
    ) ++
    (if(!isDotty) Agg(ivy"org.scala-lang:scala-reflect:${scalaVersion()}") else Agg())
  }
  def compileIvyDeps = T{ if (!isDotty) Agg(ivy"com.lihaoyi::acyclic:0.2.0") else Agg() }
  def scalacOptions = T{ if (!isDotty) Seq("-P:acyclic:force") else Seq() }
  def scalacPluginIvyDeps = T{ if (!isDotty) Agg(ivy"com.lihaoyi::acyclic:0.2.0") else Agg() }

  object test extends Tests{
    def testFrameworks = Seq("utest.runner.Framework")
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.7.10",
      ivy"com.lihaoyi::requests::0.6.9"
    )
  }
  def moduleDeps = Seq(cask.util.jvm(crossScalaVersion))
  def artifactName = "cask"
}
object cask extends Cross[CaskMainModule]((Seq(scala213, scala3) ++ dottyCustomVersion): _*) {
  object util extends Module {
    trait UtilModule extends CaskModule {
      def artifactName = "cask-util"
      def platformSegment: String
      def millSourcePath = super.millSourcePath / os.up

      def sources = T.sources(
        millSourcePath / "src",
        millSourcePath / s"src-$platformSegment"
      )
      def ivyDeps = Agg(
        ivy"com.lihaoyi::sourcecode:0.2.7",
        ivy"com.lihaoyi::pprint:0.6.6",
        ivy"com.lihaoyi::geny:0.6.10"
      )
    }
    class UtilJvmModule(val crossScalaVersion: String) extends UtilModule {
      def platformSegment = "jvm"
      def ivyDeps = super.ivyDeps() ++ Agg(
        ivy"com.lihaoyi::castor::0.1.7".withDottyCompat(scalaVersion()),
        ivy"org.java-websocket:Java-WebSocket:1.5.0"
      )
    }
    object jvm extends Cross[UtilJvmModule]((Seq(scala213, scala3) ++ dottyCustomVersion): _*)

    class UtilJsModule(val crossScalaVersion: String) extends UtilModule with ScalaJSModule {
      def platformSegment = "js"
      def scalaJSVersion = "0.6.33"
      def ivyDeps = super.ivyDeps() ++ Agg(
        ivy"com.lihaoyi::castor::0.1.7",
        ivy"org.scala-js::scalajs-dom::0.9.7"
      )
    }
    object js extends Cross[UtilJsModule]("2.13.4")

  }
}

object example extends Module{
  trait LocalModule extends CrossScalaModule{
    override def millSourcePath = super.millSourcePath / "app"
    def moduleDeps = Seq(cask(crossScalaVersion))
  }

  val allVersions = Seq(scala213, scala3) ++ dottyCustomVersion

  class CompressModule(val crossScalaVersion: String) extends $file.example.compress.build.AppModule with LocalModule
  object compress extends Cross[CompressModule](allVersions: _*)

  class Compress2Module(val crossScalaVersion: String) extends $file.example.compress2.build.AppModule with LocalModule
  object compress2 extends Cross[Compress2Module](allVersions: _*)

  class Compress3Module(val crossScalaVersion: String) extends $file.example.compress3.build.AppModule with LocalModule
  object compress3 extends Cross[Compress3Module](allVersions: _*)

  class CookiesModule(val crossScalaVersion: String) extends $file.example.cookies.build.AppModule with LocalModule
  object cookies extends Cross[CookiesModule](allVersions: _*)

  class DecoratedModule(val crossScalaVersion: String) extends $file.example.decorated.build.AppModule with LocalModule
  object decorated extends Cross[DecoratedModule](allVersions: _*)

  class Decorated2Module(val crossScalaVersion: String) extends $file.example.decorated2.build.AppModule with LocalModule
  object decorated2 extends Cross[Decorated2Module](allVersions: _*)

  class EndpointsModule(val crossScalaVersion: String) extends $file.example.endpoints.build.AppModule with LocalModule
  object endpoints extends Cross[EndpointsModule](allVersions: _*)

  class FormJsonPostModule(val crossScalaVersion: String) extends $file.example.formJsonPost.build.AppModule with LocalModule
  object formJsonPost extends Cross[FormJsonPostModule](allVersions: _*)

  class HttpMethodsModule(val crossScalaVersion: String) extends $file.example.httpMethods.build.AppModule with LocalModule
  object httpMethods extends Cross[HttpMethodsModule](allVersions: _*)

  class MinimalApplicationModule(val crossScalaVersion: String) extends $file.example.minimalApplication.build.AppModule with LocalModule
  object minimalApplication extends Cross[MinimalApplicationModule](allVersions: _*)

  class MinimalApplication2Module(val crossScalaVersion: String) extends $file.example.minimalApplication2.build.AppModule with LocalModule
  object minimalApplication2 extends Cross[MinimalApplication2Module](allVersions: _*)

  class RedirectAbortModule(val crossScalaVersion: String) extends $file.example.redirectAbort.build.AppModule with LocalModule
  object redirectAbort extends Cross[RedirectAbortModule](allVersions: _*)

  // java.lang.NoSuchMethodError: 'void geny.Writable.$init$(geny.Writable)' - geny mismatch, need to upgrade
  class ScalatagsModule(val crossScalaVersion: String) extends $file.example.scalatags.build.AppModule with LocalModule
  object scalatags extends Cross[ScalatagsModule](scala213)

  class StaticFilesModule(val crossScalaVersion: String) extends $file.example.staticFiles.build.AppModule with LocalModule
  object staticFiles extends Cross[StaticFilesModule](allVersions: _*)

  class StaticFiles2Module(val crossScalaVersion: String) extends $file.example.staticFiles2.build.AppModule with LocalModule
  object staticFiles2 extends Cross[StaticFiles2Module](allVersions: _*)

  class TodoModule(val crossScalaVersion: String) extends $file.example.todo.build.AppModule with LocalModule
  object todo extends Cross[TodoModule](scala213) // uses quill, can't enable for Dotty yet

  class TodoApiModule(val crossScalaVersion: String) extends $file.example.todoApi.build.AppModule with LocalModule
  object todoApi extends Cross[TodoApiModule](allVersions: _*)

  class TodoDbModule(val crossScalaVersion: String) extends $file.example.todoDb.build.AppModule with LocalModule
  object todoDb extends Cross[TodoDbModule](scala213) // uses quill, can't enable for Dotty yet

  class TwirlModule(val crossScalaVersion: String) extends $file.example.twirl.build.AppModule with LocalModule
  object twirl extends Cross[TwirlModule](allVersions: _*)

  class VariableRoutesModule(val crossScalaVersion: String) extends $file.example.variableRoutes.build.AppModule with LocalModule
  object variableRoutes extends Cross[VariableRoutesModule](allVersions: _*)

  class WebsocketsModule(val crossScalaVersion: String) extends $file.example.websockets.build.AppModule with LocalModule
  object websockets extends Cross[WebsocketsModule](allVersions: _*)

  class Websockets2Module(val crossScalaVersion: String) extends $file.example.websockets2.build.AppModule with LocalModule
  object websockets2 extends Cross[Websockets2Module](allVersions: _*)

  class Websockets3Module(val crossScalaVersion: String) extends $file.example.websockets3.build.AppModule with LocalModule
  object websockets3 extends Cross[Websockets3Module](allVersions: _*)

  class Websockets4Module(val crossScalaVersion: String) extends $file.example.websockets4.build.AppModule with LocalModule
  object websockets4 extends Cross[Websockets4Module](allVersions: _*)

}
