package pw.anisimov.adverto.data.model

import java.time.OffsetDateTime
import java.util.UUID

import pw.anisimov.adverto.api.NewCarAdvert

case class CarAdvert(id: UUID, title: String, fuel: Fuel, price: Int, `new`: Boolean, mileage: Option[Int] = None,
                     firstRegistration: Option[OffsetDateTime] = None) {
  if (!`new`){
    require(mileage.isDefined)
    require(firstRegistration.isDefined)
  } else {
    require(mileage.isEmpty)
    require(firstRegistration.isEmpty)
  }
}

object CarAdvert{
  def apply(newCarAdvert: NewCarAdvert): CarAdvert = CarAdvert(UUID.randomUUID(), newCarAdvert.title, newCarAdvert.fuel,
    newCarAdvert.price, newCarAdvert.`new`, newCarAdvert.mileage, newCarAdvert.firstRegistration)
}
