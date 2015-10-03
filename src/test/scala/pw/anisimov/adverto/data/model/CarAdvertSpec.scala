package pw.anisimov.adverto.data.model

import java.time.{ZoneOffset, OffsetDateTime}
import java.util.UUID

import org.scalatest.{Matchers, FlatSpec}

class CarAdvertSpec extends FlatSpec with Matchers{
  "Car Adverts" should "validate new fields" in {
    CarAdvert(UUID.randomUUID(), "Mercedes E320", Gasoline, 5000, `new` = false, Some(120000),
      Some(OffsetDateTime.of(2003, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC)))
    CarAdvert(UUID.randomUUID(), "Mercedes E320", Gasoline, 5000, `new` = true)
  }

  it should "fail on not new car without required fields" in {
    a [IllegalArgumentException] should be thrownBy {
      CarAdvert(UUID.randomUUID(), "Mercedes E320", Gasoline, 5000, `new` = false)
    }
  }
}
