import cask.util.Logger

package object cask {
  // model
  type Response[T] = model.Response[T]
  val Response = model.Response
  val Abort = model.Abort
  val Redirect = model.Redirect
  val StaticFile = model.StaticFile
  val StaticResource = model.StaticResource
  type FormEntry = model.FormEntry
  val FormEntry = model.FormEntry
  type FormValue = model.FormValue
  val FormValue = model.FormValue
  type FormFile = model.FormFile
  val FormFile = model.FormFile
  type Cookie = model.Cookie
  val Cookie = model.Cookie
  type Request = model.Request
  val Request = model.Request

  // endpoints
  type websocket = endpoints.websocket
  val WebsocketResult = endpoints.WebsocketResult
  type WebsocketResult = endpoints.WebsocketResult

  type get = endpoints.get
  type post = endpoints.post
  type put = endpoints.put
  type delete = endpoints.delete
  type patch = endpoints.patch
  type route = endpoints.route
  type staticFiles = endpoints.staticFiles
  type staticResources = endpoints.staticResources
  type postJson = endpoints.postJson
  type getJson = endpoints.getJson
  type postForm = endpoints.postForm
  type options = endpoints.options

  // main
  type MainRoutes = main.MainRoutes
  type Routes = main.Routes

  type Main = main.Main
  type RawDecorator = router.RawDecorator
  type HttpEndpoint[InnerReturned, Input] = router.HttpEndpoint[InnerReturned, Input]

  type WsHandler = cask.endpoints.WsHandler
  val WsHandler = cask.endpoints.WsHandler
  type WsActor = cask.endpoints.WsActor
  val WsActor = cask.endpoints.WsActor
  type WsChannelActor = cask.endpoints.WsChannelActor
  type WsClient = cask.util.WsClient
  val WsClient = cask.util.WsClient
  val Ws = cask.util.Ws

  // util
  type Logger = util.Logger
  val Logger = util.Logger
}
