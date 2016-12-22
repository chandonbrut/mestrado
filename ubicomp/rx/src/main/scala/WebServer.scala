import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.KafkaProducer.Conf
import org.apache.kafka.common.serialization.StringSerializer


object WebServer {


  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val producer = KafkaProducer(
      Conf(new StringSerializer(), new StringSerializer(), bootstrapServers = "localhost:9092")
    )

    val rxService = new RX(producer)
    Http().bindAndHandle(rxService.route, "localhost", 8080)

  }

}
