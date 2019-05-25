package webs

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import webs.ProcessingCenterMsgs.{GetParcel, GetParcels, NewParcel, Parcel, ParcelCreated, ParcelIdExists, Parcels, UpdateParcel}
import akka.pattern.{ask, pipe}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//class ProcessingCenter(implicit timeout: Timeout) extends Actor{
class ProcessingCenter extends Actor{
    //import webs.ProcessingCenterMsgs
    import scala.concurrent.duration._
    implicit val requestTimeout: Timeout = FiniteDuration(3, SECONDS)
    // Parcel
    def createParcel(prcId: String): ActorRef = {
        context.actorOf(ParcelActorMsgs.props(prcId), prcId)
    }

    def receive: PartialFunction[Any, Unit] = {
        case NewParcel(id) => //Some(id)          // <-------------------------------
            def create(): Unit = {
                val parcel = createParcel(id)                   // create a parcel with id
                //parcel ! ParcelActorMsgs.AddDescription("new parcel")
                //sender() ! ParcelCreated(Parcel(id, Init))      // response message
                sender() ! ParcelCreated(Parcel(id, "Init"))      // response message
                println(s"Created parcel = $id")
            }
            context.child(id).fold(create())(_ => sender() ! ParcelIdExists)
            //context.child(id)//(create())

        case GetParcel(id) =>
            def notFound(): Unit = sender() ! None
            def getParcel(child: ActorRef): Unit = child forward ParcelActorMsgs.GetStatus        // GetParcel => GetStatus
            //println(s"GETPARCEL for $id")
            //context.child(id).fold(notFound())(getParcel)
            context.child(id).fold()(getParcel)

        case GetParcels =>
            def getParcels = {
                context.children.map { child =>
                    self.ask(GetParcel(child.path.name)).mapTo[Option[Parcel]]
                }
            }
            def convertToParcels(f: Future[Iterable[Option[Parcel]]]): Future[Parcels] = {
                f.map(_.flatten).map(l => Parcels(l.toVector))
            }
            pipe(convertToParcels(Future.sequence(getParcels))) to sender()

        case UpdateParcel(id, state) =>
            def notFound(): Unit = sender() ! None
            def updateParcel(child: ActorRef): Unit = child forward ParcelActorMsgs.UpdateStatus(state)
            context.child(id).fold(notFound())(updateParcel)
            //context.child(id)(updateParcel)
    }
}

object ProcessingCenterMsgs {
    //def props(implicit timeout: Timeout) = Props(new ProcessingCenter)
    def props = Props(new ProcessingCenter)
    case class NewParcel(id: String)                    // creating a new parcel message
    case class GetParcel(id: String)                    // requesting specific parcel message
    case object GetParcels                              // requesting all available parcels message
    case class UpdateParcel(id: String, state: String)  // updating parcel's state message

    //case class Parcel(id: String, st: PkgStatus)      // parcel description message
    case class Parcel(id: String, state: String)        // parcel description message
    case class Parcels(parcels: Vector[Parcel])         // message with list of parcels

    sealed trait NewParcelResponse                      // response message for NewParcel message
    case class ParcelCreated(prcId: Parcel) extends NewParcelResponse
    case object ParcelIdExists extends NewParcelResponse
}