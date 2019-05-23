package webs

sealed trait PkgStatus
case object Init        extends PkgStatus
case object Accepted    extends PkgStatus
case object Shipped     extends PkgStatus
case object InTransit   extends PkgStatus
case object Delivered   extends PkgStatus

case class Parcel(id: String, state: PkgStatus)
//case class ChangeState(state: PkgStatus)

object typedefs {
    type Pkg = (String, PkgStatus)
}
