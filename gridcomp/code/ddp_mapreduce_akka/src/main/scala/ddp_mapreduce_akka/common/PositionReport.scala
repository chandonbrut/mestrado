package ddp_mapreduce_akka.common

import com.vividsolutions.jts.geom.Point
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.Coordinate

case class PositionReport(x:Double,y:Double,ddpVersionNumber:String,messageId:String) {
 
  def position:Point = (new GeometryFactory()).createPoint(new Coordinate(x,y))
  
  override def toString(): String = {
    messageId + "(" + ddpVersionNumber + ")@" + x + "," + y
  }
  
}