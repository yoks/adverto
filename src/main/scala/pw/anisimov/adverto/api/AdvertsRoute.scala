package pw.anisimov.adverto.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes.NoContent
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import pw.anisimov.adverto.data.CarAdvertsPersitor.{DeleteAdvert, GetAdvert}
import pw.anisimov.adverto.data.model.CarAdvert

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AdvertsRoute extends AdvertoJsonProtocol{
  implicit val timeout: Timeout = 5.seconds
  implicit val dispatcher: ExecutionContext

  val dataActor: ActorRef

  val advertsRoute = {
    path("adverts") {
      pathPrefix("advert" / JavaUUID) { uuid =>
        pathEnd {
          get {
            complete{
              (dataActor ? GetAdvert(uuid)).mapTo[Option[CarAdvert]]
            }
          } ~
            delete {
              complete {
                (dataActor ? DeleteAdvert(uuid)).map(uuid => NoContent)
              }
            }
        }
      }
    }
  }
}
