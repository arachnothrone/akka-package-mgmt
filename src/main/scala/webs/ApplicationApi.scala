package webs

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import webs.ProcessingCenterMsgs._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import StatusCodes._

class ApplicationApi(system: ActorSystem) extends RestApiRoutes {
    import scala.concurrent.duration._
    implicit val requestTimeout: Timeout = FiniteDuration(3, SECONDS)
    implicit def executionContext: ExecutionContextExecutor = system.dispatcher

    override def createProcCenter(): ActorRef = system.actorOf(ProcessingCenterMsgs.props)
}

trait RestApiRoutes extends ProcessingCenterApi with PkgMarshaller{
    // Create parcel endpoint (packageprocessing/add_package/001_FromFinland, body json: {"state": "Init"}
    val service = "packageprocessing"
    protected val newParcelRoute: Route = {
        pathPrefix(service / "add_package" / Segment) { pid =>
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
//        pathPrefix( service / "allparcels") {
//            get {
//                pathEndOrSingleSlash {
//                    onSuccess(getAvailableParcels()) {parcels =>
//                        complete(OK, parcels)
//                    }
//                }
//            }
//        }
//    }

    // GET packageprocessing/package_info/001_FromFinland
    protected val getParcelRoute: Route = {
        pathPrefix(service / "package_info" / Segment) { parcel =>
            get {
                pathEndOrSingleSlash {
                    onSuccess(getParcel(parcel)) {
                        _.fold(complete(NotFound))(e ⇒ complete(OK, e))
                    }
                }
            }
        }
    }

    // POST packageprocessing/update_package_status/001_FromFinland, body json: {"state": "Delivered"}
    protected val setParcelStatusRoute: Route = {
        pathPrefix(service / "update_package_status" / Segment) {parcel =>
            post {
                pathEndOrSingleSlash {
                    entity(as[ParcelDescription]) {p_st =>
                        onSuccess(updateParcel(parcel, p_st.state)) { state =>
                            complete(Created, state)
                        }
                    }
                }
            }
        }
    }

    val routes: Route = newParcelRoute ~ getParcelRoute ~ setParcelStatusRoute // ~ getAllParcelsRoute
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

    def getAvailableParcels: Future[Parcels] = processingCenter.ask(GetParcels).mapTo[Parcels]
    def getParcel(id: String): Future[Option[Parcel]] = processingCenter.ask(GetParcel(id)).mapTo[Option[Parcel]]
    def updateParcel(id: String, state: String): Future[Option[Parcel]] = processingCenter.ask(UpdateParcel(id, state)).mapTo[Option[Parcel]]
}
