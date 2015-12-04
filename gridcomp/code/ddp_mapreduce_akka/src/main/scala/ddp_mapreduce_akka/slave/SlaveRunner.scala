package ddp_mapreduce_akka.slave

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSelection.toScala
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinPool
import ddp_mapreduce_akka.common.Register
import ddp_mapreduce_akka.common.Start
import ddp_mapreduce_akka.matcher.MatcherActor

object SlaveRunner {  
  def connect(myIpAddress:String,masterIpAddress:String,ddpPath:String) = {    
    val config = ConfigFactory.load().getConfig("slaveSystem")
    val myConfig = ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + myIpAddress)
    implicit val system = ActorSystem.create("slaveSystem",myConfig.withFallback(config));
    
    val remoteActor = system.actorSelection("akka.tcp://masterSystem@" + masterIpAddress + ":2552/user/MasterActor")
    val localActors = system.actorOf(Props(new MatcherActor()).withRouter(RoundRobinPool(nrOfInstances = Runtime.getRuntime.availableProcessors())))
    
    remoteActor ! new Register("akka.tcp://slaveSystem@" + myIpAddress + ":2552/user/" + localActors.path.name)
  }
  
  def run(myIpAddress:String,masterIpAddress:String,filePath:String) = {    
    val config = ConfigFactory.load().getConfig("slaveSystem")
    val myConfig = ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + myIpAddress)
    implicit val system = ActorSystem.create("slaveSystem",myConfig.withFallback(config));
    
    print("filePath:::" + filePath)
    
    val remoteActor = system.actorSelection("akka.tcp://masterSystem@" + masterIpAddress + ":2552/user/MasterActor")
    
    remoteActor ! new Start(filePath)    
    
  }
  
}