package ddp_mapreduce_akka.master

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props

object MasterRunner {
  
  def run(myIpAddress:String,outputFile:String,ddpPath:String) = {    
    val config = ConfigFactory.load().getConfig("masterSystem")
    val myConfig = ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + myIpAddress)
    implicit val system = ActorSystem.create("masterSystem",myConfig.withFallback(config));
    val masterActor = system.actorOf(Props(new MasterActor(outputFile,ddpPath)), name = "MasterActor");    
    
  }

}