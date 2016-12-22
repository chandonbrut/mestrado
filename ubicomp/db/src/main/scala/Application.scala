import akka.actor.ActorSystem

/**
  * Created by jferreira on 29/11/16.
  */
object Application extends App {

  val system = ActorSystem.create("MySystem");
  val consumerActor = system.actorOf(ConsumerActor.props())

}
