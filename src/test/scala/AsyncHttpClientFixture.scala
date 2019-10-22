import org.scalatest._
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import scala.concurrent._
import org.scalatest.BeforeAndAfterAll

trait AsyncHttpClientFixture extends BeforeAndAfterAll with SttpApi { this: Suite =>

  implicit val backend = AsyncHttpClientFutureBackend(SttpBackendOptions.Default)(ExecutionContext.Implicits.global)

  override def afterAll(): Unit = {
    super.afterAll()
    backend.close()
  }
}
