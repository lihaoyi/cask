package app
object HttpMethods extends cask.MainRoutes{
  @cask.route("/login", methods = Seq("get", "post"))
  def login(request: cask.Request) = {
    if (request.exchange.getRequestMethod.equalToString("post")) "do_the_login"
    else "show_the_login_form"
  }

  @cask.route("/session", methods = Seq("delete"))
  def session(request: cask.Request) = {
    "delete_the_session"
  }

  @cask.route("/session", methods = Seq("secretmethod"))
  def admin(request: cask.Request) = {
    "security_by_obscurity"
  }

  @cask.route("/api", methods = Seq("options"))
  def cors(request: cask.Request) = {
    "allow_cors"
  }


  initialize()
}
