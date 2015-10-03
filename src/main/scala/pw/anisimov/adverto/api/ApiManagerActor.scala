package pw.anisimov.adverto.api

import akka.actor.{Props, Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

class ApiManagerActor(host: String, port: Int, val dataActor: ActorRef) extends Actor  with ActorLogging with AdvertsRoute {
  var binding: Option[Http.ServerBinding] = None
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  implicit val dispatcher = context.dispatcher

  override def preStart(): Unit = {
    val selfRef = self
    Http().bindAndHandle(advertsRoute, host, port).foreach(bound => selfRef ! bound)
  }

  override def postStop(): Unit = {
    binding foreach (_.unbind())
  }

  override def receive: Receive = {
    case boundEvent: Http.ServerBinding =>
      log.info(s"Adverto API Started at: ${boundEvent.localAddress.toString}")
      binding = Some(boundEvent)
  }
}

object ApiManagerActor {
  def props(host: String, port: Int, dataActor: ActorRef): Props = Props(classOf[ApiManagerActor], host, port, dataActor)
}
