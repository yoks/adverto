package pw.anisimov.adverto.data

import java.time.{ZoneOffset, OffsetDateTime}
import java.util.UUID

import akka.actor.{PoisonPill, ActorSystem}
import akka.testkit.TestProbe
import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpec}
import pw.anisimov.adverto.data.CarAdvertsPersitor.{DeleteAdvert, GetAdvert, GetAdverts}
import pw.anisimov.adverto.data.model.{Diesel, Gasoline, CarAdvert}

import scala.concurrent.Await
import scala.concurrent.duration._

class CarAdvertsPersitorSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem()

  override def afterAll() {
    Await.result(system.terminate(), Duration.Inf)
  }

  "Car Adverts Persistor" should "correctly persists messages" in {
    val probe = TestProbe()
    val persitorId = UUID.randomUUID().toString
    val persistor = system.actorOf(CarAdvertsPersitor.props(persitorId))

    val carAdvert = CarAdvert(UUID.randomUUID(), "Mercedes E320", Gasoline, 5000, `new` = false, Some(120000),
      Some(OffsetDateTime.of(2003, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC)))
    persistor.tell(GetAdverts, probe.ref)
    persistor.tell(carAdvert, probe.ref)
    probe.expectMsg(carAdvert)

    persistor.tell(GetAdverts(), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.id shouldBe carAdvert.id

    persistor.tell(GetAdvert(carAdvert.id), probe.ref)
    probe.expectMsg(Some(carAdvert))

    persistor.tell(GetAdvert(UUID.randomUUID()), probe.ref)
    probe.expectMsg(None)

    probe.watch(persistor)
    persistor.tell(PoisonPill, probe.ref)
    probe.expectTerminated(persistor, 1.second)

    val persistor2 = system.actorOf(CarAdvertsPersitor.props(persitorId))
    persistor2.tell(GetAdverts(), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.id shouldBe carAdvert.id

    val car2 = carAdvert.copy(title = "Mercedes E320, W211 (2003)")
    persistor2.tell(car2, probe.ref)
    probe.expectMsg(car2)

    persistor2.tell(GetAdvert(carAdvert.id), probe.ref)
    probe.expectMsg(Some(car2))

    persistor2.tell(DeleteAdvert(carAdvert.id), probe.ref)
    probe.expectMsg(carAdvert.id)

    persistor2.tell(GetAdvert(carAdvert.id), probe.ref)
    probe.expectMsg(None)

    persistor2.tell(GetAdverts(), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).length shouldBe 0
  }

  it should "correctly manage multiple messages" in {
    val probe = TestProbe()

    val persitorId = UUID.randomUUID().toString
    val persistor = system.actorOf(CarAdvertsPersitor.props(persitorId))

    val carAdverts = Set(CarAdvert(UUID.randomUUID(), "Mercedes E320", Gasoline, 5000, `new` = false, Some(120000),
      Some(OffsetDateTime.of(2003, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC))),
      CarAdvert(UUID.randomUUID(), "Volvo S60", Gasoline, 15000, `new` = true),
      CarAdvert(UUID.randomUUID(), "Audi A4", Gasoline, 17000, `new` = true))

    carAdverts.foreach(p => persistor.tell(p, probe.ref))
    probe.receiveN(3)
    persistor.tell(GetAdverts(), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).toSet shouldBe carAdverts
  }

  it should "return sorted adverts" in {
    val probe = TestProbe()

    val persitorId = UUID.randomUUID().toString
    val persistor = system.actorOf(CarAdvertsPersitor.props(persitorId))

    val carAdverts = Set(CarAdvert(UUID.randomUUID(), "Mercedes E320", Gasoline, 5000, `new` = false, Some(120000),
      Some(OffsetDateTime.of(2003, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC))),
      CarAdvert(UUID.randomUUID(), "Volvo S60", Diesel, 15000, `new` = true),
      CarAdvert(UUID.randomUUID(), "Audi A4", Gasoline, 17000, `new` = true))

    carAdverts.foreach(p => persistor.tell(p, probe.ref))
    probe.receiveN(3)
    persistor.tell(GetAdverts("title"), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.title shouldBe "Audi A4"

    persistor.tell(GetAdverts("fuel"), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.fuel shouldBe Diesel

    persistor.tell(GetAdverts("price"), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.title shouldBe "Mercedes E320"

    persistor.tell(GetAdverts("new"), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.title shouldBe "Mercedes E320"

    persistor.tell(GetAdverts("mileage"), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.title should not be "Mercedes E320"

    persistor.tell(GetAdverts("firstRegistration"), probe.ref)
    probe.expectMsgClass(classOf[Array[CarAdvert]]).head.title should not be "Mercedes E320"
  }
}
