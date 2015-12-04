name := "ddp_mapreduce_akka"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.vividsolutions" % "jts" % "1.13"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.7"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.0"

val projectMainClass = "ddp_mapreduce_akka.common.DDP"

mainClass in (Compile, run) := Some(projectMainClass)
