package webs

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, ResponseEntity, StatusCodes}
import akka.http.scaladsl.server.{HttpApp, Route}
import webs.MainApp.system
// Server definition
object WebServer extends HttpApp {
    val system = ActorSystem("example")
    val persistentActor = system.actorOf(Props[ExamplePersistentActor], "persistentActor-4-scala")
    def getInfo(): String = "lklkjlklkj"
    override def routes: Route =
        path("hello") {
            get {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http v2</h1>"))
            }
        } ~
        path("two") {
            get {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>two 2</h1>"))
            }
        } ~
//        path("three") {
//            get { pathEndOrSingleSlash { entity(){
//                onSuccess() {persistentActor ! PkgUpdateCmd(Accepted)}
//            }}
//            }
//        } ~
        {
            pathPrefix("three"/"four"){
                get{
                    pathEndOrSingleSlash{
                        //onSuccess(getInfo()){ info =>
                          {  complete(getInfo())
                        }
                    }
                }
            }

        }
}

