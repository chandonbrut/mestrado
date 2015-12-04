package ddp_mapreduce_akka.common

import scala.reflect.io.File
import scala.reflect.io.Path.string2path
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala

class AggregatorActor(outFileName:String,master:ActorRef) extends Actor {
  
  val outfile = File(outFileName)
  def receive = {
    case pair:(String,List[String]) => {
      for (areaId <- pair._2)
        outfile.appendAll(pair._1+"@"+areaId+"\n")
        
      master ! new End(0)
    }
    case msg:End => 
      print("Took " + (msg.time/1000) + "s")
      outfile.appendAll("Took " + (msg.time/1000) + "s")
  }
}