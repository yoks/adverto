package pw.anisimov.adverto.api

import java.util.UUID

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Max-Age`}
import akka.http.scaladsl.model.{HttpHeader, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import pw.anisimov.adverto.data.CarAdvertsPersistor.{DeleteAdvert, GetAdvert, GetAdverts}
import pw.anisimov.adverto.data.model.CarAdvert

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

trait AdvertsRoute extends AdvertoJsonProtocol with CorsSupport {
  implicit val timeout: Timeout
  implicit val dispatcher: ExecutionContext
  implicit val materializer: ActorMaterializer

  val dataActor: ActorRef
  override val corsAllowOrigins: List[String] = List("*")

  override val corsAllowedHeaders: List[String] = List("Origin", "X-Requested-With", "Content-Type", "Accept",
    "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

  override val corsAllowCredentials: Boolean = true

  override val optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
    `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20),
    `Access-Control-Allow-Credentials`(corsAllowCredentials)
  )

  val advertsExceptionHandler = ExceptionHandler {
    case _: IllegalArgumentException =>
      complete(HttpResponse(BadRequest, entity = "Old cars should have registration and mileage"))
    case _: java.util.NoSuchElementException =>
      complete(HttpResponse(NotFound))
  }


  val advertsRoute = {
    cors {
      handleExceptions(advertsExceptionHandler) {
        pathPrefix("advert" / JavaUUID) { uuid =>
          pathEnd {
            get {
              complete {
                (dataActor ? GetAdvert(uuid)).map(ca => ca.asInstanceOf[Option[CarAdvert]].get)
              }
            } ~
              delete {
                complete {
                  OK -> (dataActor ? DeleteAdvert(uuid)).map(ca => ca.asInstanceOf[Option[UUID]].get.toString)
                }
              } ~
              put {
                decodeRequest {
                  entity(as[CarAdvert]) { ca =>
                    complete {
                      OK -> (dataActor ? ca.copy(id = Some(uuid))).map(ca => ca.asInstanceOf[Option[UUID]].get.toString)
                    }
                  }
                }
              }
          }
        } ~
          path("advert") {
            post {
              decodeRequest {
                entity(as[CarAdvert]) { nca =>
                  complete {
                   Created -> (dataActor ? nca.copy(id = None)).map(ca => ca.asInstanceOf[Option[UUID]].get.toString)
                  }
                }
              }
            } ~
              get {
                parameter('sort.as[String] ?) { sorting =>
                  complete {
                    (dataActor ? GetAdverts(sorting.getOrElse("id"))).mapTo[Array[CarAdvert]]
                  }
                }
              }
          }
      }
    }
  }
}