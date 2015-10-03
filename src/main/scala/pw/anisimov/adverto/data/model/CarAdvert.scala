package pw.anisimov.adverto.data.model

import java.time.OffsetDateTime
import java.util.UUID

case class CarAdvert(id: UUID, title: String, fuel: Fuel, price: Int, `new`: Boolean, mileage: Option[Int] = None,
                     firstRegistration: Option[OffsetDateTime] = None)
