package pw.anisimov.adverto

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FeatureSpec, GivenWhenThen}
import pw.anisimov.adverto.api.ApiManagerActor.GetBinding
import pw.anisimov.adverto.api.{AdvertoJsonProtocol, ApiManagerActor}
import pw.anisimov.adverto.data.CarAdvertsPersistor
import pw.anisimov.adverto.data.CarAdvertsPersistor.{DeleteAdvert, GetAdverts}
import pw.anisimov.adverto.data.model.{CarAdvert, Diesel, Gasoline}
import spray.json._
import HttpMethods._
import StatusCodes._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class AcceptanceSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfterAll with BeforeAndAfter with AdvertoJsonProtocol {
  val config = ConfigFactory.load()

  implicit val system = ActorSystem()
  implicit val timeout = 1.second
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val dataActor = system.actorOf(CarAdvertsPersistor.props(UUID.randomUUID().toString))
  val apiManager = system.actorOf(ApiManagerActor.props(config.getString("adverto.hostname"), config.getInt("adverto.port"),
    dataActor))

  var serviceUri: String = _

  override def beforeAll(): Unit = {
    val probe = TestProbe()
    probe.expectNoMsg(2.seconds)
    probe.send(apiManager, GetBinding)
    val binding = probe.expectMsgClass(classOf[Option[Http.ServerBinding]]).get
    serviceUri = s"http://${binding.localAddress.getHostName}:${binding.localAddress.getPort}"
  }

  override def afterAll(): Unit = {
    Await.result(system.terminate(), Duration.Inf)
  }

  /**
   * Clean all adverts we created during test
   */
  after {
    val probe = TestProbe()
    probe.send(dataActor, GetAdverts())
    probe.expectMsgClass(classOf[Array[CarAdvert]]).foreach { advert =>
      probe.send(dataActor, DeleteAdvert(advert.id.get))
      probe.expectMsg(advert.id.get)
    }
  }

  def createAdvert(advert: CarAdvert): UUID = {
    val probe = TestProbe()
    probe.send(dataActor, advert)
    probe.expectMsgClass(classOf[Option[UUID]]).get
  }

  val VolvoAdvert = CarAdvert("Volvo S60", Gasoline, 15000, `new` = true)
  val AudiAdvert = CarAdvert("Audi A4", Gasoline, 17000, `new` = true)
  val MercedesAdvert = CarAdvert("Mercedes E320", Gasoline, 5000, `new` = false, Some(120000),
    Some(OffsetDateTime.of(2003, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC)))

  feature("Car Service should provide REST api") {
    info("As a user of the Car Advert Service ")
    info("I want be able to interact with REST API")

    scenario("have functionality to return list of all car adverts") {
      Given("collection of adverts")
      createAdvert(VolvoAdvert)
      createAdvert(MercedesAdvert)
      createAdvert(AudiAdvert)
      When("get message sent to REST API")
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$serviceUri/advert")).mapTo[HttpResponse]
      Then("answer should contain all adverts")
      assert(Await.result(Unmarshal(Await.result(response, timeout).entity).to[Array[CarAdvert]], timeout).length === 3)
    }

    scenario("have functionality to return data for single car advert by id") {
      Given("single advert")
      val carUuid = createAdvert(VolvoAdvert)
      When("get message sent to REST API with UUID")
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri =
        s"$serviceUri/advert/${carUuid.toString}")).mapTo[HttpResponse]
      Then("answer should contain direct advert")
      assert(Await.result(Unmarshal(Await.result(response, timeout).entity).to[CarAdvert], timeout) === VolvoAdvert.copy(id = Some(carUuid)))
    }

    scenario("have functionality to add car advert") {
      Given("no adverts")
      When("POST message sent to REST API")
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$serviceUri/advert", method = POST)
      .withEntity(HttpEntity(ContentTypes.`application/json`, CarAdvert("Car", Diesel, 1000, `new` = true).toJson.toString()))).mapTo[HttpResponse]
      Then("answer should contain 201 status")
      assert(Await.result(response, timeout).status === Created)
      And("GET Request should find new record")
      val getResponse: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$serviceUri/advert")).mapTo[HttpResponse]
      assert(Await.result(Unmarshal(Await.result(getResponse, timeout).entity).to[Array[CarAdvert]], timeout).length === 1)
    }

    scenario("have functionality to modify car advert by id") {
      Given("single advert")
      val carUuid = createAdvert(MercedesAdvert)
      When("PUT message sent to REST API with UUID")
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        uri = s"$serviceUri/advert/${carUuid.toString}", method = PUT)
        .withEntity(HttpEntity(ContentTypes.`application/json`, CarAdvert(
        "Mercedes Car NEW!", Gasoline, 33000, `new` = true, id = Some(carUuid)).toJson.toString()))).mapTo[HttpResponse]
      Then("answer should contain 204 status")
      assert(Await.result(response, timeout).status === NoContent)
      And("GET Request should find modified record")
      val getResponse: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri =
        s"$serviceUri/advert/${carUuid.toString}")).mapTo[HttpResponse]
      assert(Await.result(Unmarshal(Await.result(getResponse, timeout).entity).to[CarAdvert], timeout).title ===
        "Mercedes Car NEW!")
    }

    scenario("have functionality to delete car advert by id") {
      Given("single advert")
      val carUuid = createAdvert(AudiAdvert)
      When("DELETE message sent to REST API with UUID")
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        uri = s"$serviceUri/advert/${carUuid.toString}", method = DELETE)).mapTo[HttpResponse]
      Then("answer should contain 204 status")
      assert(Await.result(response, timeout).status === NoContent)
      And("GET Request should not find new record")
      val getResponse: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri =
        s"$serviceUri/advert/${carUuid.toString}")).mapTo[HttpResponse]
      assert(Await.result(getResponse, timeout).status === OK)
    }

    scenario("have validation by fields and fields used only for used cars") {
      Given("no adverts")
      When("POST message sent to REST API with wrong values")
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$serviceUri/advert", method = POST)
        .withEntity(HttpEntity(ContentTypes.`application/json`,
          """{"price":15000,"fuel":"Gasoline","id":"fc4a7e15-9499-4f8b-b80b-9bf38429c1ba","new":false,"title":"Volvo S60"}"""))).mapTo[HttpResponse]
      Then("answer should contain 400 status")
      assert(Await.result(response, timeout).status === BadRequest)
    }

    scenario("sorting by any field specified by query parameter, default sorting - by **id**") {
      Given("collection of adverts")
      createAdvert(VolvoAdvert)
      createAdvert(MercedesAdvert)
      createAdvert(AudiAdvert)
      When("get message sent to REST API with sorting by title")
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$serviceUri/advert?sort=title")).mapTo[HttpResponse]
      Then("answer should contain all adverts")
      assert(Await.result(Unmarshal(Await.result(response, timeout).entity).to[Array[CarAdvert]], timeout).head.title ===
        AudiAdvert.title)
    }
  }
}
