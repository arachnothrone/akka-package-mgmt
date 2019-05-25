package webs

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.{ExecutionContextExecutor, Future}

//import scala.concurrent.{ExecutionContextExecutor, Future}
//import akka.actor.ActorSystem
//import akka.event.Logging
//import akka.http.scaladsl.Http.ServerBinding
//import akka.http.scaladsl.Http
//import akka.stream.ActorMaterializer
//import akka.util.Timeout
////import webs.ApplicationApi
////import com.typesafe.config.{Config, ConfigFactory}

//object MainApp2 extends App with RequestTimeout {
object MainApp2 extends App {
    implicit val system: ActorSystem = ActorSystem()
    implicit val ex_cont: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    //val api = new ApplicationApi(system, requestTimeout("10")).routes
    val api = new ApplicationApi(system).routes
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, "0.0.0.0", 9090 )


}

//trait RequestTimeout {
//    import scala.concurrent.duration._
//    def requestTimeout(tout: String): Timeout = {
//        val d = Duration(tout)
//        FiniteDuration(d.length, d.unit)
//    }
//}
