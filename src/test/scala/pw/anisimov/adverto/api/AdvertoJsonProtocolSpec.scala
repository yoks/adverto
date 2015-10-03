package pw.anisimov.adverto.api

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}
import pw.anisimov.adverto.data.model.{Gasoline, CarAdvert}
import spray.json._

class AdvertoJsonProtocolSpec extends FlatSpec with Matchers with AdvertoJsonProtocol {
  "Adverto Json Protocol" should "correctly marshal messages" in {
    CarAdvert("Volvo S60", Gasoline, 15000, `new` = true, id = Some(UUID.fromString("fc4a7e15-9499-4f8b-b80b-9bf38429c1ba"))).toJson.toString shouldBe
    """{"price":15000,"fuel":"Gasoline","id":"fc4a7e15-9499-4f8b-b80b-9bf38429c1ba","new":true,"title":"Volvo S60"}"""

    CarAdvert("Mercedes E320", Gasoline, 5000, `new` = false, Some(120000),
      Some(OffsetDateTime.of(2003, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC)), Some(UUID.fromString("fc4a7e15-9499-4f8b-b80b-9bf38429c1ba"))).toJson.toString shouldBe
      """{"mileage":120000,"price":5000,"fuel":"Gasoline","id":"fc4a7e15-9499-4f8b-b80b-9bf38429c1ba","new":false,"firstRegistration":"2003-08-10T00:00Z","title":"Mercedes E320"}"""
  }
}
