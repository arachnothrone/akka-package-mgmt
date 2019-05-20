package webs

import akka.actor.{ActorSystem, Props}
import akka.persistence._
import webs.typedefs.Pkg
//import webs.WebServer

//case class PkgUpdateCmd(data: String)
case class PkgUpdateCmd(data: PkgStatus)
case class PkgSetName(data: String)
//sealed trait Pkg {
//    var pkgId: Int = 0
//    var pkgWeight: Double = 0.0
//    var pkgName: String = ""
//    var pkgState: PkgStatus = Init
//}
case class AddPkg(data: String)     // for RootActor

//case class Evt(data: String)
case class Evt(data: Pkg)
case class EvtR(data: List[ExamplePersistentActor])
// ---------------------- events -------------
case class ExampleState(events: List[Pkg] = Nil) {
    def updated(evt: Evt): ExampleState = copy(evt.data :: events)
    def size: Int = events.length
    override def toString: String = events.reverse.toString
    def lst: Pkg = events.head
}
case class RootState(events: List[List[ExamplePersistentActor]] = Nil){
    def updated(evt: EvtR): RootState = copy(evt.data :: events)
    def lst: List[ExamplePersistentActor] = events.last
}
// ---------------------------------------------
// ---------------------- actors ---------------
class ExamplePersistentActor() extends PersistentActor {
    override def persistenceId = "PKG_001v1"

    var state = ExampleState()
    var pkgId: String = ""

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
            persist(Evt((pkgId, data))) { event =>    //persist(Evt(s"$data-$numEvents")) { event =>
                updateState(event)
                context.system.eventStream.publish(event)
            }
        case PkgSetName(data) => pkgId = data
        case "snap"  => saveSnapshot(state)
        case "print" => println(state)
        case "printLast" => println(s"PackageId = ${state.lst._1}, PackageStatus = ${state.lst._2}")
        case "setName" => pkgId = "sdf"
    }

}

class RootActor() extends PersistentActor{
    override def persistenceId: String = "Root"
    var state = RootState()
    def updateState(event: EvtR): Unit =
        state = state.updated(event)

    val receiveRecover: Receive = {
        case evt: EvtR  => updateState(evt)
    }
    val receiveCommand: Receive = {
        case AddPkg(pid) => println("zzzzzz")

//            persist(EvtR())
    }
}
// ---------------------------------------------

object MainApp extends App {
    //WebServer.startServer("localhost", port = 8080)

    val system = ActorSystem("example")

    val persistentActor = system.actorOf(Props[ExamplePersistentActor], "persistentActor-4-scala")
    //val persistentActor2 = system.actorOf(Props[ExamplePersistentActor], "actor-2")

    persistentActor ! PkgSetName("001")
    persistentActor ! PkgUpdateCmd(Accepted)
    persistentActor ! PkgUpdateCmd(Shipped)
    persistentActor ! PkgUpdateCmd(InTransit)
    persistentActor ! "snap"
    persistentActor ! PkgUpdateCmd(Delivered)
    persistentActor ! "print"
    persistentActor ! "printLast"
    //persistentActor2 ! PkgUpdateCmd(Accepted)
    //persistentActor2 ! "print"

    //persistentActor ! "setName"
    //persistentActor ! PkgUpdateCmd(Accepted)
    //persistentActor ! "print"
    //persistentActor ! "printLast"

    Thread.sleep(10000)
    system.terminate()
}

