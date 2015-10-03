package pw.anisimov.adverto.data

import java.util.UUID

import akka.actor.Props
import akka.persistence.PersistentActor
import pw.anisimov.adverto.data.model.CarAdvert

class CarAdvertsPersitor extends PersistentActor {
  override def persistenceId: String = "car-advert-persistence"

  var adverts = Map[UUID, CarAdvert]()

  def updateState(carAdvert: CarAdvert): Unit = {
    adverts = adverts + (carAdvert.id -> carAdvert)
  }

  def deleteElement(uuid: UUID): Unit = {
    adverts = adverts - uuid
  }

  override def receiveRecover: Receive = {
    case ca: CarAdvert => updateState(ca)
  }

  override def receiveCommand: Receive = {
    case ca: CarAdvert =>
      val senderActor = sender()
      persist(ca) { data =>
        updateState(data)
        senderActor ! ca
      }
  }
}

object CarAdvertsPersitor {
  def props(): Props = Props[CarAdvertsPersitor]
}