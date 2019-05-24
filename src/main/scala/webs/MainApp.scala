package webs

import akka.actor.{ActorSystem, Props}
import akka.persistence._
import webs.ProcessingCenterMsgs.{GetParcel, NewParcel}
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
case class EvtR(data: List[PersistentPackage])
// ---------------------- events -------------
case class PackageState(events: List[Pkg] = Nil) {
    def updated(evt: Evt): PackageState = copy(evt.data :: events)
    def size: Int = events.length
    override def toString: String = events.reverse.toString
    def lst: Pkg = events.head
}
case class RootState(events: List[List[PersistentPackage]] = Nil){
    def updated(evt: EvtR): RootState = copy(evt.data :: events)
    def lst: List[PersistentPackage] = events.last
}
// ---------------------------------------------
// ---------------------- actors ---------------
class PersistentPackage() extends PersistentActor {
    override def persistenceId = "PKG_001v1"

    var state = PackageState()
    var pkgId: String = ""

    def updateState(event: Evt): Unit =
        state = state.updated(event)

    def numEvents: Int =
        state.size
    //def pkgId(x: Int): Int = x

    val receiveRecover: Receive = {
        case evt: Evt                                 => updateState(evt)
        case SnapshotOffer(_, snapshot: PackageState) => state = snapshot
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
        case "testcmd" => pkgId = "dddddd"
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
    val mmmactor = system.actorOf(ProcessingCenterMsgs.props)
    mmmactor ! NewParcel("001_One")
    Thread.sleep(3000)
    mmmactor ! GetParcel("002")
    mmmactor ! GetParcel("001_One")
    Some
    println("stop ----")

    Thread.sleep(10000)
    system.terminate()



    val persistentActor = system.actorOf(Props[PersistentPackage], "persistentActor-4-scala")
    //val persistentActor2 = system.actorOf(Props[ExamplePersistentActor], "actor-2")

    persistentActor ! PkgSetName("001")
    persistentActor ! PkgUpdateCmd(Accepted)
    persistentActor ! PkgUpdateCmd(Shipped)
    persistentActor ! PkgUpdateCmd(InTransit)
    persistentActor ! "snap"
    persistentActor ! PkgUpdateCmd(Delivered)
    persistentActor.tell("print", persistentActor)
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

