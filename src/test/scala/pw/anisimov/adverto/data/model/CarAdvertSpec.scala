package pw.anisimov.adverto.data.model

import java.time.{OffsetDateTime, ZoneOffset}

import org.scalatest.{FlatSpec, Matchers}

class CarAdvertSpec extends FlatSpec with Matchers{
  "Car Adverts" should "validate new fields" in {
    CarAdvert("Mercedes E320", Gasoline, 5000, `new` = false, Some(120000),
      Some(OffsetDateTime.of(2003, 8, 10, 0, 0, 0, 0, ZoneOffset.UTC)))
    CarAdvert("Mercedes E320", Gasoline, 5000, `new` = true)
  }

  it should "fail on not new car without required fields" in {
    a [IllegalArgumentException] should be thrownBy {
      CarAdvert("Mercedes E320", Gasoline, 5000, `new` = false)
    }
  }
}
