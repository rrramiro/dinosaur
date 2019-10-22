package io.dinosaur
import scalanative.native._
import scalanative.posix.unistd
import io.dinosaur.CgiUtils._
import io.dinosaur.FastCGIUtils._

case class CGIRouter(handlers: Seq[Handler]) extends Router {
  def handle(method: Method, path: String)(f: Request => Response): Router = {
    val new_handler = Handler(method, CgiUtils.parsePathInfo(path), f)
    return CGIRouter(Seq(new_handler) ++ this.handlers)
  }

  def dispatch(): Unit = {
    val request = Router.parseRequest()
    val matches = for (h @ Handler(method, pattern, handler) <- this.handlers
                       if request.method() == method
                       if request.pathInfo().startsWith(pattern)) yield h
    val bestHandler = matches.maxBy(_.pattern.size)
    val response    = bestHandler.handler(request)
    for ((k, v) <- response.inferHeaders) {
      System.out.println(k + ": " + v)
    }
    System.out.println()
    System.out.println(response.bodyToString)
  }
}
