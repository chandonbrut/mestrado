import spray.json._
import akka.http.scaladsl.server.Directives
import cakesolutions.kafka.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

class RX(producer: KafkaProducer[String,String]) extends Directives with JsonSupport {

  val route = {
    logRequestResult("disease-report-rx") {
      path("disease") {
        get {
          complete(DiseaseReport("patientId","diseaseId",0,0,0))
        } ~ post {
          entity(as[DiseaseReport]) { diseaseReport =>
            complete {
              val msg = new ProducerRecord[String,String]("testTopic",diseaseReport.toJson.toString())
              producer.send(msg)
              diseaseReport
            }
          }
        }
      } ~ path("symptom") {
        get {
          complete(SymptomReport("patientId","symptomId",0,0,0))
        } ~ post {
          entity(as[SymptomReport]) { symptomReport =>
              complete {
                val msg = new ProducerRecord[String,String]("testTopic",symptomReport.toJson.toString())
                producer.send(msg)
                symptomReport
              }
          }
        }
      }
    }
  }
}