package pw.anisimov.adverto.api

import akka.actor.{Props, Actor, ActorLogging, ActorRef}

class ApiManagerActor(host: String, port: Int, dataActor: ActorRef) extends Actor  with ActorLogging {
  override def receive: Receive = ???
}

object ApiManagerActor {
  def props(host: String, port: Int, dataActor: ActorRef): Props = Props(classOf[ApiManagerActor], host, port, dataActor)
}
