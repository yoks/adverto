package pw.anisimov.adverto.data.model

import java.time.OffsetDateTime
import java.util.UUID

case class CarAdvert(id: UUID, title: String, fuel: Fuel, price: Integer, `new`: Boolean, mileage: Option[Integer] = None,
                     firstRegistration: Option[OffsetDateTime] = None)
