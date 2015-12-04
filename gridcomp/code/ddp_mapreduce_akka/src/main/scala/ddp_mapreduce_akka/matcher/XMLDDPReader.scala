package ddp_mapreduce_akka.matcher

import scala.xml.XML
import scala.xml.Node

class XMLDDPReader(filename:String) {
  val xml = XML.loadFile(filename)

  def extractPorts:List[String] = {
    val h = for (p <-MockDDP.xml \ "ContractingGovernment" \ "Ports" \ "Port")
    		yield p match {
    			case s @ <Port>{c @ _*}</Port> => "POINT(" + (s \ "Position").text + ")" 
    		}
    h.toList
  }
  
  def extract(typeOfPolygon:String):List[DDPPolygon] = {
    
    def producePolygon(xml:Node) = {
	    xml match {
	      case s @ <Polygon>{contents @ _*}</Polygon> => {
	        val posElem = s \ "PosList"
	        val areaIdElem = s \ "@areaID"
	        val polygon = GeoUtil.polygonFromPosList(posElem.text)
	        new DDPPolygon(areaIdElem.text,polygon.toText())
	      }
	    }
    }
    
    val handled = for (polygon <- xml \ "ContractingGovernment" \ typeOfPolygon \ "Polygon" ) yield producePolygon(polygon)
    handled.toList    
  }
  
  
  val internalWaters:List[DDPPolygon] = extract("InternalWaters")
  val territorialSea:List[DDPPolygon] = extract("TerritorialSea")
  val custom:List[DDPPolygon] = extract("CustomCoastalAreas")
  val seaward1000nm :List[DDPPolygon]= extract("SeawardAreaOf1000NM")
  def polygons:List[DDPPolygon] = internalWaters ::: territorialSea  ::: custom  ::: seaward1000nm
//  def lookup(polygonId : String) : LRITGeometry = new LRITGeometry(polygonId)
  
  val standingOrders:List[(String,String)] = {
    
    def handleStandingOrder(xml:Node) = {
	    xml match {
	      case s @ <StandingOrder>{v}</StandingOrder> => (s \ "@contractingGovernmentID",List(v))
	    }
    }
    
    val handled = for (standingOrder <- xml \ "CoastalStateStandingOrders" \ "StandingOrder" ) yield handleStandingOrder(standingOrder)
	
    val converted = handled.map(a => (a._1.text,a._2.text.split(" ").toList)).toList
    
    def produceStandingOrders(handled : (String,List[String])) : List[(String,String)] = {
      handled match {
        case (cgId:String,x::xs) => {
          val mm = xs.map(a=> List((cgId,a)))
          val t = (cgId,x)
          t :: mm.flatten
        }
        case (cgId:String,x::Nil) => List((cgId,x))
      }
    }
	
    converted.map(so => produceStandingOrders(so)).flatten    
        
  }
  
}