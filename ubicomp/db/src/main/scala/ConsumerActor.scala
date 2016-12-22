import akka.actor.{Actor, ActorLogging, Props}
import cakesolutions.kafka.akka.KafkaConsumerActor.{Confirm, Subscribe, Unsubscribe}
import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.akka.KafkaConsumerActor.Subscribe.AutoPartition
import cakesolutions.kafka.akka.{ConsumerRecords, KafkaConsumerActor}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringDeserializer

object ConsumerActor {

  def props() = Props(new ConsumerActor())
  val extractor = ConsumerRecords.extractor[String, String]

}

class ConsumerActor extends Actor with ActorLogging {

  val config = ConfigFactory.parseString(
    s"""
       | bootstrap.servers = "localhost:9092",
       | group.id = "dbGroup",
       | topics = ["testTopic"]
      """.stripMargin)

  private val kafkaConsumerActor = context.actorOf(
    KafkaConsumerActor.props(
      consumerConf = KafkaConsumer.Conf(
        config,
        keyDeserializer = new StringDeserializer,
        valueDeserializer = new StringDeserializer
      ),
      actorConf = KafkaConsumerActor.Conf(config),
      self
    ),
    "KafkaConsumer"
  )

  override def preStart() = {
    super.preStart()
    kafkaConsumerActor ! AutoPartition(Seq("testTopic"))
  }

  override def postStop() = {
    kafkaConsumerActor ! Unsubscribe
    super.postStop()
  }

  override def receive = {

    case a:ConsumerRecords[String,String] => {
      for (record <- a.recordsList) {
        Manager.broadcast(record.value)
      }
      kafkaConsumerActor ! Confirm(a.offsets, commit = true)
    }

  }

}
