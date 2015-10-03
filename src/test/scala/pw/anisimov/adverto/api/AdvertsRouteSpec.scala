package pw.anisimov.adverto.api

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpec}
import pw.anisimov.adverto.data.CarAdvertsPersitor.{DeleteAdvert, GetAdvert, GetAdverts}
import pw.anisimov.adverto.data.model.{CarAdvert, Diesel}
import spray.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class AdvertsRouteSpec extends WordSpec with Matchers with ScalatestRouteTest with AdvertsRoute {
  val correctUuid = UUID.randomUUID()
  val dataActor = system.actorOf(Props(classOf[MockDataActor], correctUuid))
  val dispatcher: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = 1.seconds

  "Adverts Route" should {
    "fail to handle not adverts route request" in {
      Get() ~> advertsRoute ~> check {
        handled shouldBe false
      }
    }

    "return Car Advert to the correct request" in {
      Get(s"/advert/${correctUuid.toString}") ~> advertsRoute ~> check {
        responseAs[CarAdvert] shouldEqual CarAdvert(correctUuid, "Car", Diesel, 1000, `new` = true)
      }
    }

    "return NoContent on DELETE command" in {
      Delete(s"/advert/${correctUuid.toString}") ~> advertsRoute ~> check {
        status shouldEqual NoContent
      }
    }

    "return NoContent on PUT command" in {
      Put(s"/advert/${correctUuid.toString}",
        HttpEntity(ContentTypes.`application/json`, CarAdvert(correctUuid, "Car", Diesel, 1000, `new` = true).toJson.toString())) ~> advertsRoute ~> check {
        status shouldEqual NoContent
      }
    }

    "return NoContent on POST command" in {
      Post(s"/advert",
        HttpEntity(ContentTypes.`application/json`, NewCarAdvert("Car", Diesel, 1000, `new` = true).toJson.toString())) ~> advertsRoute ~> check {
        status shouldEqual Created
      }
    }

    "return array car adverts on get command" in {
      Get("/advert") ~> advertsRoute ~> check {
        responseAs[Array[CarAdvert]] shouldEqual Array(CarAdvert(correctUuid, "Car", Diesel, 1000, `new` = true))
      }
    }

    "return array car adverts on get command with Sorting" in {
      Get("/advert?sort=title") ~> advertsRoute ~> check {
        responseAs[Array[CarAdvert]] shouldEqual Array(CarAdvert(correctUuid, "Car", Diesel, 1000, `new` = true))
      }
    }

    "return bad request on invalid submission" in {
      Post(s"/advert",
        HttpEntity(ContentTypes.`application/json`, NewCarAdvert("Car", Diesel, 1000, `new` = false).toJson.toString())) ~> advertsRoute ~> check {
        status shouldEqual BadRequest
      }
    }
  }
}

class MockDataActor(correctUuid: UUID) extends Actor {
  override def receive: Receive = {
    case GetAdvert(`correctUuid`) =>
      sender() ! Some(CarAdvert(correctUuid, "Car", Diesel, 1000, `new` = true))
    case GetAdvert(uuid) =>
      sender() ! None
    case DeleteAdvert(uuid) =>
      sender() ! uuid
    case advert: CarAdvert =>
      sender() ! advert
    case msg: GetAdverts =>
      sender() ! Array(CarAdvert(correctUuid, "Car", Diesel, 1000, `new` = true))
  }
}
