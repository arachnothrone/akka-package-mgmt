package webs

//import akka.actor.{ActorRef, ActorSystem}
//import webs.ProcessingCenterMsgs.{GetParcel, NewParcel, NewParcelResponse, Parcel}
//import akka.pattern.ask
//import akka.util.Timeout
//import akka.http.scaladsl.server._
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.model.StatusCodes
//import StatusCodes._
//import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import webs.ProcessingCenterMsgs._

import scala.concurrent.duration.FiniteDuration
//import com.showtix.messages._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import StatusCodes._


//class ApplicationApi(system: ActorSystem, timeout: Timeout) extends RestApiRoutes {
class ApplicationApi(system: ActorSystem) extends RestApiRoutes {
    // implicit val requestTimeout: Timeout = timeout
    import scala.concurrent.duration._
    implicit val requestTimeout: Timeout = FiniteDuration(3, SECONDS)  // TimeUnit
    implicit def executionContext: ExecutionContextExecutor = system.dispatcher

    override def createProcCenter(): ActorRef = system.actorOf(ProcessingCenterMsgs.props)
}

trait RestApiRoutes extends ProcessingCenterApi with PkgMarshaller{
    // Create parcel endpoint
    val service = "parcel"
    val version = "v1"
    protected val newParcelRoute: Route = {
        pathPrefix(service / version / "parcels" / Segment) { pid =>
            post {
                pathEndOrSingleSlash {
                    entity(as[ParcelDescription]) { p_descr =>
                        onSuccess(createParcel(pid, p_descr.state)) {
                            case ProcessingCenterMsgs.ParcelCreated(p) => complete(Created, p)
                            case ProcessingCenterMsgs.ParcelIdExists   =>
                                val err = Error(s"$pid exists")
                                complete(BadRequest, err)
                        }
                    }
                }
            }
        }
    }

//    protected val getAllParcelsRoute: Route = {
//        pathPrefix( service / version / "allparcels") {
//            get {
//                pathEndOrSingleSlash {
//                    onSuccess(getAvailableParcels()) {parcels =>
//                        complete(OK, parcels)
//                    }
//                }
//            }
//        }
//    }

    protected val getParcelRoute: Route = {
        pathPrefix(service / version / "parcel" / Segment) { parcel ⇒
            get {
                // GET show-tix/v1/parcel/:parcel
                pathEndOrSingleSlash {
                    onSuccess(getParcel(parcel)) {
                        _.fold(complete(NotFound))(e ⇒ complete(OK, e))
                    }
                }
            }
        }
    }

    val routes: Route = newParcelRoute ~ getParcelRoute // ~ getAllParcelsRoute
}

trait ProcessingCenterApi {
    def createProcCenter(): ActorRef

    implicit def executionContext: ExecutionContext
    implicit def requestTimeout: Timeout

    lazy val processingCenter: ActorRef = createProcCenter()

    //                                   +---- PkgStatus
    def createParcel(id: String, st: String): Future[NewParcelResponse] = {
        processingCenter.ask(NewParcel(id)).mapTo[NewParcelResponse]
    }

    def getAvailableParcels(): Future[Parcels] = processingCenter.ask(GetParcels).mapTo[Parcels]
    def getParcel(id: String): Future[Option[Parcel]] = processingCenter.ask(GetParcel(id)).mapTo[Option[Parcel]]
}