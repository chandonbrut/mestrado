package ddp_mapreduce_akka.matcher

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.LinearRing
import com.vividsolutions.jts.geom.Point
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader

object GeoUtil {
  
	def decimalFromDegree(degreeMinuteDecimal:String) : Double = {
			
            val fields = degreeMinuteDecimal split("\\.")            
            
            val degrees = fields(0).toDouble
            val minutes = fields(1).toDouble
            val	decimalMinutes = fields(2).toDouble
            
            val trueOrientation = fields(3).contains("n") || fields(3).contains("N") || fields(3).contains("e") || fields(3).contains("E")
            val dd = degrees + ((minutes+decimalMinutes/100)/60) 
            
            if (!trueOrientation) {
            	dd * -1
            } else {
            	dd  
            }
            
	}
  
     def coordinatesFrom(posList:String):List[Coordinate] = {
        def reduce(c : List[String]):List[Coordinate] = {
          c match {
          	case x :: y :: Nil => new Coordinate(x.toDouble,y.toDouble) :: Nil  
          	case x :: y :: xs => new Coordinate(x.toDouble,y.toDouble) :: reduce(xs)          	
          }
        }
        
        val coords = posList.split(" ");
        reduce(coords.toList)
    }

    def polygonFromPosList(posList : String) : Polygon = {
    		 val points = coordinatesFrom(posList)
    		 val coordinates = points.toArray
    		 val geometryFactory = new GeometryFactory();
    		 val boundary:LinearRing = geometryFactory.createLinearRing(coordinates)
    		 geometryFactory.createPolygon(boundary,null)    		 
    }
    
    def pointFromWKT(wkt:String) : Point = {
    		 val reader = new WKTReader()
    		 val pt = reader read wkt
    		 pt.asInstanceOf[Point]
    }
    
    
    def geometryFromWKT(wkt:String) : Geometry = {
    		 val reader = new WKTReader()
    		 val pt = reader read wkt
    		 pt
    }
}
