name := "ontolinker"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "org.apache.jena" % "jena-core" % "3.1.0",
  "net.sourceforge.owlapi" % "owlapi-distribution" % "5.0.4",
  "com.hermit-reasoner" % "org.semanticweb.hermit" % "1.3.8.4"
)
