package webs

import akka.actor.{Actor, ActorRef, Props}
import webs.ProcessingCenterMsgs.{GetParcel, NewParcel, Parcel, ParcelCreated, ParcelIdExists}

class ProcessingCenter extends Actor{
    // Parcel
    def createParcel(prcId: String): ActorRef = {
        context.actorOf(ParcelActorMsgs.props(prcId), prcId)
    }

    def receive: PartialFunction[Any, Unit] = {
        case NewParcel(id) => //Some(id)          // <-------------------------------
            def create(): Unit = {
                val parcel = createParcel(id)                   // create a parcel with id
                parcel ! ParcelActorMsgs.AddDescription("new parcel")
                sender() ! ParcelCreated(Parcel(id, Init))      // response message
                println(s"Created parcel = $id")
            }
            context.child(id).fold(create())(_ => sender() ! ParcelIdExists)
        case GetParcel(id) =>
            def notFound() = sender() ! None
            def getParcel(child: ActorRef) = child forward ParcelActorMsgs.GetStatus        // GetParcel => GetStatus
            context.child(id).fold(notFound())(getParcel)
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