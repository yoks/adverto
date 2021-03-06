package pw.anisimov.adverto

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import pw.anisimov.adverto.api.ApiManagerActor
import pw.anisimov.adverto.data.CarAdvertsPersistor

object Main extends App {
  val config = ConfigFactory.load()

  val system = ActorSystem()

  val dataActor = system.actorOf(CarAdvertsPersistor.props("car-advert-persistor"))
  val apiManager = system.actorOf(ApiManagerActor.props(config.getString("adverto.hostname"), config.getInt("adverto.port"),
    dataActor))
}
