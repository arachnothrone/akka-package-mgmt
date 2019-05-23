package webs

import akka.actor.{Actor, ActorSystem, Props}

// import webs.typedefs.Pkg

class ParcelActor(prcId: String) extends Actor {
    import webs.ParcelActorMsgs.{AddDescription, GetStatus, UpdateStatus}
    //var parcel = Parcel
    //var state: Pkg = (prcId, Init)
    var state: PkgStatus = Init
    var description: String = ""

    def receive: PartialFunction[Any, Unit] = {
        case UpdateStatus(nstatus) => state = nstatus
        case AddDescription(id) => description = id
        case GetStatus => sender() ! Some       // <----------------------------
    }

}

object ParcelActorMsgs {
    def props(pId: String) = Props(new ParcelActor(pId))

    case class AddDescription(description: String)  // message to add description
    case class UpdateStatus(state: PkgStatus)       // message to update parcel's state
    case object GetStatus                           // message with the parcel's current status
}