package pw.anisimov.adverto.data

import java.util.UUID

import akka.actor.Props
import akka.persistence.PersistentActor
import pw.anisimov.adverto.data.CarAdvertsPersistor.{DeleteAdvert, GetAdvert, GetAdverts}
import pw.anisimov.adverto.data.model.CarAdvert

class CarAdvertsPersistor(val persistenceId: String) extends PersistentActor {

  var adverts = Map[UUID, CarAdvert]()

  def updateState(carAdvert: CarAdvert): Unit = {
    adverts = adverts + (carAdvert.id.get -> carAdvert)
  }

  def deleteElement(uuid: UUID): Unit = {
    adverts = adverts - uuid
  }

  override def receiveRecover: Receive = {
    case ca: CarAdvert => updateState(ca)
    case deleteAdvert: DeleteAdvert => deleteElement(deleteAdvert.uuid)
  }

  override def receiveCommand: Receive = {
    case ca: CarAdvert =>
      val senderActor = sender()
      val newId = ca.id match {
        case Some(id) =>
          if (adverts.get(id).isDefined) {
            Some(id)
          } else {
            None
          }
        case None =>
          Some(UUID.randomUUID())
      }
      if (newId.isDefined) {
        persist(ca.copy(id = newId)) { data =>
          updateState(data)
          senderActor ! newId
        }
      } else {
        senderActor ! None
      }
    case deleteAdvert: DeleteAdvert =>
      val senderActor = sender()
      adverts.get(deleteAdvert.uuid) match {
        case Some(adv) =>
          persist(deleteAdvert) { data =>
            deleteElement(data.uuid)
            senderActor ! Some(data.uuid)
          }
        case None =>
          senderActor ! None
      }
    case GetAdvert(uuid) =>
      sender() ! adverts.get(uuid)
    case GetAdverts(sortBy) =>
      sortBy match {
        case "id" => sender() ! adverts.values.toArray.sortBy(_.id.toString)
        case "title" => sender() ! adverts.values.toArray.sortBy(_.title)
        case "fuel" => sender() ! adverts.values.toArray.sortBy(_.fuel.toString)
        case "price" => sender() ! adverts.values.toArray.sortBy(_.price)
        case "new" => sender() ! adverts.values.toArray.sortBy(_.`new`)
        case "mileage" => sender() ! adverts.values.toArray.sortBy(_.mileage)
        case "firstRegistration" => sender() ! adverts.values.toArray.sortBy(_.firstRegistration)
        case _ => sender() ! adverts.values.toArray.sortBy(_.id.toString)
      }
  }
}

object CarAdvertsPersistor {

  case class GetAdvert(uuid: UUID)

  case class DeleteAdvert(uuid: UUID)

  case class GetAdverts(sortBy: String = "id")

  def props(persistenceId: String): Props = Props(classOf[CarAdvertsPersistor], persistenceId)
}
