package ddp_mapreduce_akka.common

import ddp_mapreduce_akka.slave.SlaveRunner
import ddp_mapreduce_akka.master.MasterRunner

object DDP {
  def main(args:Array[String]) = {
    args(0) match {
      case "-slave" => {
        val myIpAddress = args(1)
        val masterIpAddress = args(2)
        val ddpPath = args(3)
        SlaveRunner.connect(myIpAddress, masterIpAddress, ddpPath)
      }
      case "-master" => {
        val myIpAddress = args(1)
        val outputFile = args(2)
        val ddpPath = args(3)
        MasterRunner.run(myIpAddress, outputFile,ddpPath)        
      }
      case "-start" => {
        val myIpAddress = args(1)
        val masterIpAddress = args(2)
        val filePath = args(3)
        SlaveRunner.run(myIpAddress, masterIpAddress, filePath)        
      }
      case _ => {
        printUsage
      }
    }  
  }
  def printUsage = {
    print("Wrong arguments. Please use -slave or -master\n")
  }
}