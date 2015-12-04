package ddp_mapreduce_akka.matcher

import akka.actor.Actor
import akka.actor.actorRef2Scala
import ddp_mapreduce_akka.common.PositionReport
import akka.actor.ActorRef


class MatcherActor extends Actor {
  
  val ddpStore = DDPStore
  val ddpPath = System.getProperties.getProperty("ddpPath").toString()
  
  def receive = {
    case (p:PositionReport,aggregator:ActorRef) => {
      aggregator ! findPolygonsForReport(p)
    }
  }
  
  def findPolygonsForReport(rep:PositionReport):(String,List[String]) = {
    try {
      val ddp = ddpStore.get(rep.ddpVersionNumber,ddpPath)
      (rep.messageId,ddp.polygons.filter(p => GeoUtil.geometryFromWKT(p.wkt).contains(rep.position)).map(s=>s.areaId))
    } catch {
      case e:Exception => {
        ("Skipped: " + rep.messageId,Nil)
      }
    }
    
  }
}