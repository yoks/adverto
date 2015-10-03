package pw.anisimov.adverto

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.testkit.TestProbe
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FeatureSpec, GivenWhenThen}
import pw.anisimov.adverto.api.ApiManagerActor
import pw.anisimov.adverto.api.ApiManagerActor.GetBinding
import pw.anisimov.adverto.data.CarAdvertsPersitor
import pw.anisimov.adverto.data.CarAdvertsPersitor.{DeleteAdvert, GetAdverts}
import pw.anisimov.adverto.data.model.CarAdvert

import scala.concurrent.Await
import scala.concurrent.duration._

class AcceptanceSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfterAll with BeforeAndAfter {
  val config = ConfigFactory.load()

  implicit val system = ActorSystem()

  val dataActor = system.actorOf(CarAdvertsPersitor.props(UUID.randomUUID().toString))
  val apiManager = system.actorOf(ApiManagerActor.props(config.getString("adverto.hostname"), config.getInt("adverto.port"),
    dataActor))

  var serviceUri: Http.ServerBinding = _

  override def beforeAll(): Unit = {
    val probe = TestProbe()
    probe.expectNoMsg(2.seconds)
    probe.send(apiManager, GetBinding)
    serviceUri = probe.expectMsgClass(classOf[Option[Http.ServerBinding]]).get
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
      probe.send(dataActor, DeleteAdvert(advert.id))
      probe.expectMsg(advert.id)
    }
  }

  feature("Car Service should provide REST api") {
    info("As a user of the Car Advert Service ")
    info("I want be able to interact with REST API")

    scenario("have functionality to return list of all car adverts") {
      fail()
    }

    scenario("have functionality to return data for single car advert by id") {
      fail()
    }

    scenario("have functionality to add car advert") {
      fail()
    }

    scenario("have functionality to modify car advert by id") {
      fail()
    }

    scenario("have functionality to delete car advert by id") {
      fail()
    }

    scenario("have validation by fields and fields used only for used cars") {
      fail()
    }

    scenario("accept and return data in JSON format") {
      fail()
    }

    scenario("Service should be able to handle CORS requests from any domain") {
      fail()
    }

    scenario("sorting by any field specified by query parameter, default sorting - by **id**") {
      fail()
    }
  }
}
