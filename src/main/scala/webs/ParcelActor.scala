package webs

import akka.actor.{Actor, ActorSystem, Props}
import webs.ProcessingCenterMsgs.Parcel
// import webs.typedefs.Pkg

class ParcelActor(prcId: String) extends Actor {
    import webs.ParcelActorMsgs.{AddDescription, GetStatus, UpdateStatus}
    //var parcel = Parcel
    //var state: Pkg = (prcId, Init)
    //var state: PkgStatus = Init
    var state: String = "Init"
    var description: String = ""

    def receive: PartialFunction[Any, Unit] = {
        case UpdateStatus(nstatus) =>
            state = nstatus
            sender() ! Some(Parcel(prcId, state))
        // case AddDescription(id) => description = id
        case GetStatus => sender() ! Some(ProcessingCenterMsgs.Parcel(prcId, state))     //Some       // <----------------------------
    }

}

object ParcelActorMsgs {
    def props(id: String) = Props(new ParcelActor(id))

    case class AddDescription(description: String)      // message to add description
    //case class UpdateStatus(state: PkgStatus)           // message to update parcel's state
    case class UpdateStatus(state: String)              // message to update parcel's state
    case object GetStatus                               // message with the parcel's current status
}