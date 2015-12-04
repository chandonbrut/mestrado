package ddp_mapreduce_akka.master

import scala.io.Codec.string2codec
import scala.io.Source
import ddp_mapreduce_akka.common.PositionReport



object PositionReportReader {
  /*
   * Header:
   * messageid,messagetype,schemaversion,senttimestamp,state,success,test,aspid,cspid,datauserprovider,datauserrequestor,dcid,ddpversionnum,imonum,latitude,longitude,mmsinum,referenceid,responsetype,shipname,shipborneequipmentid,timestamp1,timestamp2,timestamp3,timestamp4,timestamp5
   * 
   */
	def read(fileName:String) : Iterator[PositionReport] = {
			Source.fromFile(fileName)("UTF-8").getLines().map(line => {			  
			  val words = line.replace(" ","").replace("|",",").split(",")
			  PositionReport(strDMStoDD(words(15)),strDMStoDD(words(14)),words(12),words(0)) 			
			})
	}
	
	def strDMStoDD(dmsStr:String):Double = {
	  val words = dmsStr split "\\."
	  words(3) match {
	    case "S" => -(words(0).toDouble + (words(1).toDouble  + (words(2).toDouble / 60.0))/60.0) 
	    case "W" => -(words(0).toDouble + (words(1).toDouble  + (words(2).toDouble / 60.0))/60.0)
	    case _ => (words(0).toDouble + (words(1).toDouble  + (words(2).toDouble / 60.0))/60.0)
	  }
	  
	}
}