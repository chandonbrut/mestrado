package ddp_mapreduce_akka.master

import scala.collection.mutable.HashMap

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Address
import akka.actor.AddressFromURIString
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.remote.routing.RemoteRouterConfig
import akka.routing.RoundRobinPool
import ddp_mapreduce_akka.common.AggregatorActor
import ddp_mapreduce_akka.common.End
import ddp_mapreduce_akka.common.Register
import ddp_mapreduce_akka.common.Start
import ddp_mapreduce_akka.matcher.MatcherActor

class MasterActor(outputFile:String,ddpPath:String) extends Actor {
  val addresses = HashMap[String,Address]()
  var workerRouterActor:ActorRef = null
  var busy = false
  val aggregator = context.actorOf(Props(new AggregatorActor(outputFile,self)))
  var tx = 0
  var rx = 0
  var startTime:Long = 0
  
  def receive = {
    case msg:Register => {
      print("Registrando " + msg.workerAddress + "\n")
      addresses.put(msg.workerAddress,AddressFromURIString.parse(msg.workerAddress))

      workerRouterActor =  context.system.actorOf(
          Props(new MatcherActor()).withRouter(
              RemoteRouterConfig(RoundRobinPool(addresses.size), addresses.values)))
    	}
    case msg:Start => {
      if (busy) print("Job already running.\n")
      else {
        startJob(msg)
      }
    }
    case msg:End => {
      rx = rx+1
      if (rx == tx) {
        aggregator ! new End(compat.Platform.currentTime - startTime)
        busy = false
        rx = 0
        tx = 0
      }
      
    }
  }
  def startJob(msg:Start) = {
    
    busy = true
    startTime = compat.Platform.currentTime
    val reports = PositionReportReader.read(msg.fileName).toList
    tx = reports.size
    print("Starting job for " + tx + " reports.\n")
    reports.map( p => { workerRouterActor ! (p,aggregator) } )
  }
}