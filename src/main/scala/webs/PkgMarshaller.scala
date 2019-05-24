package webs

import play.api.libs.json._
import de.heikoseeberger.akkahttpplayjson._
import webs.ProcessingCenterMsgs.Parcel

case class ParcelDescription(id: String)

trait PkgMarshaller extends PlayJsonSupport{
    implicit val newParcelFormat: OFormat[ParcelDescription] = Json.format[ParcelDescription]
    implicit val pkgFormat: OFormat[Parcel] = Json.format[Parcel]
}

object PkgMarshaller extends PkgMarshaller