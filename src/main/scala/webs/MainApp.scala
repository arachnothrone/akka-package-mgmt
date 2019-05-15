package webs

//import webs.WebServer

object MainApp extends App {
    WebServer.startServer("localhost", port = 8080)
}
