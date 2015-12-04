package ddp_mapreduce_akka.matcher

object DDPStore {
  val data = new collection.mutable.HashMap[String,XMLDDPReader]
  val access = new collection.mutable.HashMap[String,Long]
  def get(ddpVersionNumber:String,ddpPath:String) : XMLDDPReader = {
    val xml = data.getOrElseUpdate(ddpVersionNumber, new XMLDDPReader(ddpPath + "/" + ddpVersionNumber.replace(":","_") + ".xml"))
    val nowTime:Long = compat.Platform.currentTime;
    access.put(ddpVersionNumber, nowTime)
    
    for (key <- access.keys) {
      val diff:Long = (nowTime - access.getOrElse(key, 0L))
      if (diff > 60*1000) {
        access.remove(key)
        data.remove(key)
      }
    }
    
    xml
  }
}