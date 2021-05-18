import mill._, scalalib._
import $ivy.`com.lihaoyi::mill-contrib-twirllib:$MILL_VERSION`

trait AppModule extends CrossScalaModule with mill.twirllib.TwirlModule{

  def twirlVersion = "1.5.0-M1"

  def generatedSources = T{ Seq(compileTwirl().classes) }
  def ivyDeps = Agg[Dep](
    ivy"com.lihaoyi::scalatags:0.9.1".withDottyCompat(scalaVersion()),
    ivy"com.typesafe.play::twirl-api:${twirlVersion()}".withDottyCompat(scalaVersion()),
  )

  object test extends Tests{
    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.7.10",
      ivy"com.lihaoyi::requests::0.6.9",
    )
  }
}
