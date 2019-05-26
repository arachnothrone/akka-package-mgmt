package webs

import play.api.libs.json._
import webs.ProcessingCenterMsgs._
import de.heikoseeberger.akkahttpplayjson._

//case class ParcelDescription(st: PkgStatus)
case class ParcelDescription(state: String) {
    require(state != "")
}
case class Error(message: String)

trait PkgMarshaller extends PlayJsonSupport{
    implicit val newParcelFormat: OFormat[ParcelDescription] = Json.format[ParcelDescription]
    implicit val errorFormat: OFormat[Error] = Json.format[Error]
    implicit val pkgFormat: OFormat[Parcel] = Json.format[Parcel]
}

object PkgMarshaller extends PkgMarshaller