package pw.anisimov.adverto.api

import java.time.OffsetDateTime
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pw.anisimov.adverto.data.model.{Gasoline, Diesel, Fuel, CarAdvert}
import spray.json._

trait AdvertoJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object uuidFormat extends JsonFormat[UUID] {
    override def write(obj: UUID): JsValue = JsString(obj.toString)

    override def read(json: JsValue): UUID = json match {
      case JsString(value) => UUID.fromString(value)
      case x => deserializationError(s"Correct uuid expected, got $x")
    }
  }
  implicit object fuelFormat extends RootJsonFormat[Fuel] {
    override def write(fuel: Fuel): JsValue = JsString(fuel.toString)

    override def read(json: JsValue): Fuel = json match {
      case JsString(value) => value match {
        case "Diesel" => Diesel
        case "Gasoline" => Gasoline
      }
      case x => deserializationError(s"Correct fuel expected, got $x")
    }
  }

  implicit object offsetDateTimeFormat extends JsonFormat[OffsetDateTime] {
    override def write(dt: OffsetDateTime): JsValue = JsString(dt.toString)

    override def read(json: JsValue): OffsetDateTime = json match {
      case JsString(value) => OffsetDateTime.parse(value)
      case x => deserializationError(s"Correct date expected, got $x")
    }
  }

  implicit val carJsonFormat = jsonFormat7(CarAdvert.apply)
}
