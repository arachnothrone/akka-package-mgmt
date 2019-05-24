package webs

import akka.actor.ActorRef
import webs.ProcessingCenterMsgs.{GetParcel, NewParcel, NewParcelResponse, Parcel}
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.StatusCodes
import scala.concurrent.{ExecutionContext, Future}

class ApplicationApi {
    Some()
}

trait RestApiRoutes extends ProcessingCenterApi with PkgMarshaller{
    // Create parcel endpoint
    protected val newParcelRoute: Route = {
        pathPrefix("store"/Segment) { parcel_id =>
            post {
                pathEndOrSingleSlash {
                    entity(as[ParcelDescription]) {
                        p_descr => onSuccess(createParcel(parcel_id)) {
                            case ProcessingCenterMsgs.ParcelCreated(p) => complete(p)
                        }
                    }
                }
            }
        }
    }

    val routes: Route = newParcelRoute
}

trait ProcessingCenterApi {
    def createProcCenter(): ActorRef

    implicit def executionContext: ExecutionContext
    implicit def requestTimeout: Timeout

    lazy val processingCenter: ActorRef = createProcCenter()

    def createParcel(id: String): Future[NewParcelResponse] = {
        processingCenter.ask(NewParcel(id)).mapTo[NewParcelResponse]
    }

    def getParcel(id: String): Future[Option[Parcel]] = processingCenter.ask(GetParcel(id)).mapTo[Option[Parcel]]
}
