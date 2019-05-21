package webs

import play.api.libs.json._
import de.heikoseeberger.akkahttpplayjson._

trait EventMarshaller extends PlayJsonSupport{
    implicit val cmdFormat: OFormat[PkgSetName] = Json.format[PkgSetName]
}

object EventMarshaller extends EventMarshaller