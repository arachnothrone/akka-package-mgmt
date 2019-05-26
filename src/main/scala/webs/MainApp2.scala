package webs

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContextExecutor, Future}

object MainApp2 extends App {
    implicit val system: ActorSystem = ActorSystem()
    implicit val ex_cont: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val api = new ApplicationApi(system).routes
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, "0.0.0.0", 9090 )


}

