import org.scalatest._
import org.scalatest.time._
import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.Executors
import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.softwaremill.sttp.StatusCodes

class DinosaurTest
    extends FunSuite
    with DockerDinosaurService
    with DockerKitDockerJava
    with EitherValues
    with AsyncHttpClientFixture {

  implicit override val patienceConfig =
    PatienceConfig(timeout = scaled(Span(150, Seconds)), interval = scaled(Span(15, Seconds)))

  test("dinosaur") {
    whenReady {
      for {
        port        <- getDefaultDinosaurPort
        welcome     <- sttp.get(uri"http://localhost:${port}/cgi-bin/app").send()
        hello       <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/hello").send()
        who         <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/who").send()
        john        <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/who/john").send()
        both        <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/who/john/jane").send()
        all         <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/who/john/jane/richard").send()
        bye         <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/bye").send()
        byeJohn     <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/bye?who=john").send()
        byeJohnJane <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/bye?who=john&who=jane").send()
      } yield
        (
          welcome.body.right.value.trim,
          hello.body.right.value.trim,
          who.body.right.value.trim,
          john.body.right.value.trim,
          both.body.right.value.trim,
          all.body.right.value.trim,
          bye.body.right.value.trim,
          byeJohn.body.right.value.trim,
          byeJohnJane.body.right.value.trim
        )
    } {
      case (welcome, hello, who, john, both, all, bye, byeJohn, byeJohnJane) =>
        assert("<H1>Welcome to Dinosaur!</H1>" == welcome)
        assert("Hello World!" == hello)
        assert("Who's there?" == who)
        assert("Hello, john" == john)
        assert("Hello both of you" == both)
        assert("Hello y'all!" == all)
        assert("" == bye)
        assert("Bye, john" == byeJohn)
        assert("Bye, john. Bye, jane" == byeJohnJane)
    }
  }

  /*
  test("dinosaur error") {
    whenReady {
      for {
        port     <- getDefaultDinosaurPort
        notFound <- sttp.get(uri"http://localhost:${port}/cgi-bin/app/notfound").send()
      } yield notFound
    } {
      case notFound =>
        assert(notFound.code == StatusCodes.NotFound)
    }
  }
 */
}
