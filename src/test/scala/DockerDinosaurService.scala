import com.whisk.docker.{DockerContainer, DockerKit, DockerReadyChecker}
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest._
import org.scalatest.time._
import java.util.concurrent.Executors
import scala.concurrent._

trait DockerDinosaurService extends DockerTestKit { self: Suite =>

  val defaultDinosaurPort = 80

  val dinosaurContainer = DockerContainer("dinosaur:latest")
    .withPorts(defaultDinosaurPort -> None)
    .withReadyChecker(DockerReadyChecker.LogLineContains("httpd -D FOREGROUND"))

  abstract override def dockerContainers: List[DockerContainer] =
    dinosaurContainer :: super.dockerContainers

  def getDefaultDinosaurPort =
    dinosaurContainer.getPorts().map(_.get(defaultDinosaurPort).getOrElse(fail("port not found")))

}
