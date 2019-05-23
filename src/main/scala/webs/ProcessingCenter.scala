package webs

import akka.actor.{Actor, ActorRef, Props}
import webs.ProcessingCenterMsgs.NewParcel

class ProcessingCenter extends Actor{
    // Parcel
    def createParcel(prcId: String): ActorRef = {
        context.actorOf(ParcelActorMsgs.props(prcId), prcId)
    }

    def receive: PartialFunction[Any, Unit] = {
        case NewParcel(id) => Some(id)          // <-------------------------------
    }
}

object ProcessingCenterMsgs {
    def props = Props(new ProcessingCenter)

    case class NewParcel(id: String)
    case class GetParcel(id: String)

    case class Parcel(id: String, state: PkgStatus)     // parcel description message
    sealed trait NewParcelResponse      // response message for NewParcel message
    case class ParcelCreated(prc: Parcel) extends NewParcelResponse
    case object ParcelIdExists extends NewParcelResponse
}