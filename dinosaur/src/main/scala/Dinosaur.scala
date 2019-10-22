package io.dinosaur
import scalanative.native._
import scalanative.posix.unistd
import io.dinosaur.CgiUtils._
import io.dinosaur.FastCGIUtils._

sealed trait RouterMode
case object CGIMode    extends RouterMode
case object FCGIMode   extends RouterMode
case object UVFCGIMode extends RouterMode

case class Handler(
    method: Method,
    pattern: Seq[String],
    handler: Request => Response
) {
  def this(method: Method, pattern: String, handler: Request => Response) = {
    this(method, CgiUtils.parsePathInfo(pattern), handler)
  }
}

trait Router {
  def handle(method: Method, path: String)(f: Request => Response): Router
  def get(path: String)(f: Request                    => Response): Router = handle(GET, path)(f)
  def post(path: String)(f: Request                   => Response): Router = handle(POST, path)(f)
  def put(path: String)(f: Request                    => Response): Router = handle(PUT, path)(f)
  def delete(path: String)(f: Request                 => Response): Router = handle(DELETE, path)(f)
  def dispatch(): Unit
}

object Router {
  def parseRequest(): Request = {
    //val scriptName = env(c"SCRIPT_NAME")
    val pathInfo    = parsePathInfo(env(c"PATH_INFO"))
    val queryString = parseQueryString(env(c"QUERY_STRING"))
    val method = env(c"METHOD") match {
      case "GET"     => GET
      case "POST"    => POST
      case "PUT"     => PUT
      case "DELETE"  => DELETE
      case "HEAD"    => HEAD
      case "OPTIONS" => OPTIONS
      case "PATCH"   => PATCH
      case _         => GET
    }

    Request(() => method, () => pathInfo, queryString)
  }

  def init(): Router = {
    val errorResponse = Response(StringBody("No path matched the request"))
    val errorHandler  = Handler(GET, List(), (_) => errorResponse)

    val debugHandler = Handler(GET, List("debug"), (request) => {
      Response(StringBody(request.toString))
    })
    val handlers: Seq[Handler] = List(debugHandler, errorHandler)

    CgiUtils.env(c"ROUTER_MODE") match {
      case "FCGI"   => FastCGIRouter(handlers)
      case "UVFCGI" => UVFCGIRouter(handlers)
      case _        => CGIRouter(handlers)
    }
  }
}
