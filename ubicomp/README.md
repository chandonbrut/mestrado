ubicomp-ontolinker
===================

Introduction
------------
This is a very brief project to bootstrap an application aimed at monitoring epidemics using 
ubiquitous computing and semantic web concepts. The project is organized as follows:

	*db: The service in charge of consuming from the log and persisting data in a RDBMS.
	*inference: The service in charge of discovering new knowledge using data from the log and ontologies.
	*notification: The service in charge of notifying clients in case of new disease or epidemics found.
	*ontolinker: The service in charge of integrating and enriching the SYMP and DOID ontologies.
	*rx: The service in charge of receiving data (using REST) and publishing it to a log.

The log module is an apache kafka instance, with a topic of name "testTopic". The default installation should
suffice, but in any case, this is the script I'm using to start the broker.


	[jferreira@alekhine env]$ cat start-broker.sh 
	cd kafka_2.11-0.10.1.0
	screen -dmS "zoo" bin/zookeeper-server-start.sh config/zookeeper.properties
	sleep 60
	screen -dmS "kafka" bin/kafka-server-start.sh config/server.properties
	# bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
	screen -dmS "consumer" bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic testTopic --from-beginning


The project is written in Scala, using Akka and SBT.


Protocol
--------------------
Since this is just a PoC, the protocol consists of three case classes.

case class SymptomReport(patientId:String, symptomURI:String, timestamp:Long, latitude:Long, longitude:Long)
case class EpidemicAlert(numberOfCases:Long,diseaseURI:String, timestamp:Long, latitude:Long, longitude:Long)
case class DiseaseReport(patientId:String, diseaseURI:String, timestamp:Long, latitude:Long, longitude:Long)

They are transformed to JSON using the Spray JsonSupport class. You should use REST HTTP requests to send data to the RX services.

I use [httpie](https://github.com/jkbrzt/httpie) for that.


Ontology Integration
--------------------
I followed [this](http://ieeexplore.ieee.org/document/6476567/) approach to integrate the two ontologies (symptoms ontology - SYMP, and human diseases ontology - DOID). The article suggests using an external service to do disambiguation and to match symptoms names at the description of the diseases with its (the symptoms) ids, but I wanted a local solution, so I'm using it's own annotations (at the disease ontologie) and doing a string match in order to find its (the symptoms) ids at the symptoms ontologies, and then generating the predicates linking the diseases with the symptoms.
Due to the "quick and dirty" approach (plain string matching), lack of standards for the annotations, and, some data deprecation (yeah, the disease ontology contains a lot of "deprecated" classes), a large number of symptoms and diseases can't be properly identified,but there is enough for an application (diseases such as dengue fever and its symptoms are properly identified).

The numbers are:

	*Undefined diseases: 7636
	*Defined diseases: 3858
	*Undefined symptoms: 562
	*Defined symptoms: 146

Current State
-------------
This project is discontinued. I was building this as an assignment for a course, but I got personal problem (sh1t happens, right?) that kept me from class, I did a pretty bad test and lost another one, voilá, I got kicked out.
There are another projects I do keep: 
[Akka Ship Simulator](https://github.com/chandonbrut/akka-ship-simulator), which is a ship simulator I built to test the Brazil LRIT RDC.
[DDP Akka](https://github.com/chandonbrut/mestrado/tree/master/gridcomp/code/ddp_mapreduce_akka), which is a software I built to check if ships were sailing in conformance to some polygons. This one I built for a Grid Computing course, I can go on, just contact me and we can chat.
[Bio](https://github.com/chandonbrut/bio/), Bioinformatics stuff. Built those to solve the problems for Rosalind and the Coursera Bioinformatics Course.

Cheers 
