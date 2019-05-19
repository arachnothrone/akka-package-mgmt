package webs

import akka.actor.{ActorSystem, Props}
import akka.persistence._
//import webs.WebServer

sealed trait PkgStatus
case object Init        extends PkgStatus
case object Accepted    extends PkgStatus
case object Shipped     extends PkgStatus
case object InTransit   extends PkgStatus
case object Delivered   extends PkgStatus
//case class PkgUpdateCmd(data: String)
case class PkgUpdateCmd(data: PkgStatus)

sealed trait Pkg {
    var pkgId: Int = 0
    var pkgWeight: Double = 0.0
    var pkgName: String = ""
    var pkgState: PkgStatus = Init
}

case class Evt(data: String)
//case class Evt(data: Pkg)

case class ExampleState(events: List[String] = Nil) {
    def updated(evt: Evt): ExampleState = copy(evt.data :: events)
    def size: Int = events.length
    override def toString: String = events.reverse.toString
}

class ExamplePersistentActor extends PersistentActor {
    override def persistenceId = "sample-id-1"

    var state = ExampleState()

    def updateState(event: Evt): Unit =
        state = state.updated(event)

    def numEvents: Int =
        state.size
    //def pkgId(x: Int): Int = x

    val receiveRecover: Receive = {
        case evt: Evt                                 => updateState(evt)
        case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
    }

    val receiveCommand: Receive = {
        case PkgUpdateCmd(data) =>
//            var e: Pkg
//            e.pkgId = 2
            persist(Evt(s"$data-$numEvents")) { event =>    //persist(Evt(s"$data-$numEvents")) { event =>
                updateState(event)
                context.system.eventStream.publish(event)
            }
        case "snap"  => saveSnapshot(state)
        case "print" => println(state)
    }

}

object MainApp extends App {
    //WebServer.startServer("localhost", port = 8080)


    val system = ActorSystem("example")
    val persistentActor = system.actorOf(Props[ExamplePersistentActor], "persistentActor-4-scala")
    //val persistentActor2 = system.actorOf(Props[ExamplePersistentActor], "actor-2")

    persistentActor ! PkgUpdateCmd(Accepted)
    persistentActor ! PkgUpdateCmd(Shipped)
    persistentActor ! PkgUpdateCmd(InTransit)
    persistentActor ! "snap"
    persistentActor ! PkgUpdateCmd(Delivered)
    persistentActor ! "print"
    //persistentActor2 ! PkgUpdateCmd(Accepted)
    //persistentActor2 ! "print"

    Thread.sleep(10000)
    system.terminate()
}

