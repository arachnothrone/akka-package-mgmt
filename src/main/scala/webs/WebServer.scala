package webs

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{HttpApp, Route}

// Server definition
object WebServer extends HttpApp {
    override def routes: Route =
        path("hello") {
            get {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http v2</h1>"))
            }
        }~
        path("asdf") {
            get {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>asdf</h1>"))
            }
        }
}
