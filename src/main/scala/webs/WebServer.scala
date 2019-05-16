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
        } ~
        path("two") {
            get {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>two 2</h1>"))
            }
        } ~
        path("three") {
            get {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>three 3</h1>"))
            }
    }
}
