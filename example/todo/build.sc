import mill._, scalalib._


trait AppModule extends CrossScalaModule{

  def ivyDeps = Agg[Dep](
    ivy"org.xerial:sqlite-jdbc:3.18.0",
    ivy"io.getquill::quill-jdbc:3.4.10",
    ivy"com.lihaoyi::scalatags:0.9.1",
  )

  object test extends Tests{
    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.7.10",
      ivy"com.lihaoyi::requests::0.6.9",
    )
  }
}
