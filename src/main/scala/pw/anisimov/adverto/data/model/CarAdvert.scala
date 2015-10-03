package pw.anisimov.adverto.data.model

import java.time.OffsetDateTime
import java.util.UUID

case class CarAdvert(title: String, fuel: Fuel, price: Int, `new`: Boolean, mileage: Option[Int] = None,
                     firstRegistration: Option[OffsetDateTime] = None, id: Option[UUID] = None) {
  if (!`new`){
    require(mileage.isDefined)
    require(firstRegistration.isDefined)
  } else {
    require(mileage.isEmpty)
    require(firstRegistration.isEmpty)
  }
}
