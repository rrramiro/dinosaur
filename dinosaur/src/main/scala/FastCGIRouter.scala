package io.dinosaur
import scalanative.native._
import scalanative.posix.unistd
import io.dinosaur.CgiUtils._
import io.dinosaur.FastCGIUtils._

case class FastCGIRouter(handlers: Seq[Handler]) extends Router {
  def handle(method: Method, path: String)(f: Request => Response): Router = {
    return FastCGIRouter(Seq())
  }
  def dispatch(): Unit = {
    val header_buffer = stackalloc[Byte](8)
    val body_buffer   = stackalloc[Byte](2048)
    System.err.println("reading from STDIN")
    var req_count = 0
    // var open_reqs = scala.collection.mutable.Set[Int]()
    while (true) {
      val header_read = unistd.read(unistd.STDIN_FILENO, header_buffer, 8)
      if (header_read == 0) {
        System.err.println(s"pipe closed, exiting after serving $req_count requests")
        System.exit(0)
      } else if (header_read < 8) {
        System.err.println(s"Warning: read $header_read bytes for record header, expected 8")
      }
      val header = readHeader(header_buffer, 0)
      // open_reqs += header.reqId
      // System.err.println(header)
      val content_read = unistd.read(unistd.STDIN_FILENO, body_buffer, header.length + header.padding)
      if (content_read < (header.length + header.padding)) {
        System.err.println(
          s"Warning: read $content_read bytes for record content, expected ${header.length + header.padding}"
        )
      }
      // System.err.println(s"request ${header.reqId}: read $header_read bytes header type ${header.rec_type} and $content_read body from stdin")
      if ((header.rec_type == FCGI_STDIN) && (header.length == 0)) {
        // System.err.println(s"sending response to request ${header.reqId} : ${req_count} total processed")

        // total hack work for now.
        // will fill in implementation once architecture is stabilized
        writeResponse(header.reqId, "Content-type: text/html\r\n\r\nhello")
        req_count += 1
        if (req_count >= 1000) { // TODO: parameterize
          System.err.println("done")
          stdio.fclose(stdio.stdout)
          stdio.fclose(stdio.stdin)
          System.err.println("closing out pipe, exiting")
          System.exit(0)
        }
      }
    }
  }
}
