import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class SymptomReport(patientId:String, symptomURI:String, timestamp:Long, latitude:Long, longitude:Long)
case class EpidemicAlert(numberOfCases:Long,diseaseURI:String, timestamp:Long, latitude:Long, longitude:Long)
case class DiseaseReport(patientId:String, diseaseURI:String, timestamp:Long, latitude:Long, longitude:Long)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val symptomReportFormat = jsonFormat5(SymptomReport)
  implicit val epidemicAlertFormat = jsonFormat5(EpidemicAlert)
  implicit val diseaseReportFormat = jsonFormat5(DiseaseReport)
}
