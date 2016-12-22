import java.nio.file.Paths

import collection.JavaConverters._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{IRI, OWLOntology}
import org.semanticweb.owlapi.search.EntitySearcher
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl

import scala.collection.mutable.ListBuffer

abstract class Symptom {
  def label:String
}

abstract class Disease

case class DefinedSymptom(val label:String, uri:String) extends Symptom
case class UndefinedSymptom(val label:String) extends Symptom
case class DefinedDisease(label:String,uri:String,symptoms:List[Symptom]) extends Disease
case class UndefinedDisease(uri:String) extends Disease

object OntologyLinker {

  private val HAS_ONTOLOGY_SYMPTOM = "http://purl.obolibrary.org/obo/doid#has_symptom"
  private val dataFactory = OWLManager.getOWLDataFactory

  def main(args: Array[String]): Unit = {


    val ontologyManager = OWLManager.createOWLOntologyManager()
    val symptomOntology = ontologyManager.loadOntologyFromOntologyDocument(Paths.get("/home/jferreira/Downloads/symp.owl").toFile)

    val diseaseOntology = ontologyManager.loadOntologyFromOntologyDocument(Paths.get("/home/jferreira/Downloads/doid.owl").toFile)

    val symptomClasses = symptomOntology.getClassesInSignature().asScala

    val symptomsMapping = symptomClasses.map(ontologyClass => {
      (EntitySearcher.getAnnotations(ontologyClass,symptomOntology, dataFactory.getRDFSLabel()).findFirst().get().getValue().asLiteral().get().getLiteral() -> ontologyClass.getIRI.getIRIString)
    }).toMap

    val diseaseClasses = diseaseOntology.getClassesInSignature().asScala

    val descriptionProperty = new OWLAnnotationPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"))

    val hasSymptomProperty = new OWLAnnotationPropertyImpl(IRI.create(HAS_ONTOLOGY_SYMPTOM))

    // Extraindo palavras chaves
    val diseaseList:scala.collection.mutable.Set[Disease] = for (ontologyClass <- diseaseClasses) yield {

      val a = EntitySearcher.getAnnotations(ontologyClass,diseaseOntology, descriptionProperty).findFirst()
      val label = EntitySearcher.getAnnotations(ontologyClass,diseaseOntology, dataFactory.getRDFSLabel()).findFirst()
      val symptoms = if (a.isPresent && label.isPresent) extractSymptoms(a.get().getValue.asLiteral().get().getLiteral)
                     else Nil

      val base:List[Symptom] = symptoms match {
        case Nil => Nil
        case s:List[String] => searchSymptoms(s,symptomsMapping)
      }

      val disease:Disease = if (a.isPresent && label.isPresent) DefinedDisease(label.get().getValue.asLiteral().get().getLiteral,ontologyClass.getIRI.getIRIString, base)
                            else UndefinedDisease( ontologyClass.getIRI().getIRIString() )

      disease
    }


    def expandOntology(diseaseOntology:OWLOntology, symptomOntology:OWLOntology, diseases:Seq[Disease]) = {

      for (disease <- diseases) {
        disease match {
          case d:DefinedDisease => {
            val diseaseClass = IRI.create(d.uri)
            d.symptoms.map{
              case s:DefinedSymptom => {
                val symptomClass = IRI.create(s.uri)
                val linkProperty = dataFactory.getOWLAnnotationProperty(IRI.create(HAS_ONTOLOGY_SYMPTOM))
                val axiom = dataFactory.getOWLAnnotationAssertionAxiom(linkProperty,diseaseClass,symptomClass)
                ontologyManager.addAxiom(diseaseOntology,axiom)
              }
              case _ =>
            }
        }
        case _ =>
        }
      }
    }

//    producePredicates(filterValidDiseases(diseaseList.toSeq)).map(println _)
    expandOntology(diseaseOntology,symptomOntology,filterValidDiseases(diseaseList.toSeq))

    diseaseOntology.saveOntology()

  }

  def extractSymptoms(description:String):List[String] = {
    val fields = description.replaceAll(" has_symptom(s){0,1} ","#").split("#").toList
    val symptoms:List[String] = if (fields.size > 1) {
      for (s <- fields.drop(1))
        yield clearSymptomString(s)
      } else {
      Nil
    }
    symptoms
  }


  // Filtra por doenças que tenham pelo menos um sintoma válido (mapeado)
  def filterValidDiseases(unfilteredDiseases:Seq[Disease]) : Seq[Disease] = {
    def filterSymptoms(symptoms:List[Symptom]) : List[Symptom] = {
      symptoms.filter {
        case s:DefinedSymptom => true
        case s:UndefinedSymptom => false
      }
    }

    unfilteredDiseases.filter {
      case d: DefinedDisease => (d.symptoms.size > 0) && (filterSymptoms(d.symptoms).size > 0)
      case _ => false
    }

  }

  def clearSymptomString(symptomString:String):String = {
    symptomString match {
      case s if s.endsWith(",") => s.replaceAll(",$","")
      case s if s.endsWith(", ") => s.replaceAll(", ","")
      case s if s.endsWith(".") => s.replace(".","")
      case s if s.endsWith(". ") => s.replace(". $","")
      case s if s.endsWith(", and") => s.replaceAll(", and$","")
      case s if s.endsWith(" and") => s.replaceAll(" and$","")
      case s if s.endsWith(" or ") => s.replaceAll(" or $","")
      case s if s.endsWith(" or") => s.replaceAll(" or","")
      case s:String => s
    }
  }

  def searchSymptoms(names:Seq[String],mapping:Map[String,String]):List[Symptom] = {
    names match {
      case x :: xs => if (mapping.contains(x)) DefinedSymptom(x,mapping(x)) :: searchSymptoms(xs,mapping)
                      else UndefinedSymptom(x) :: searchSymptoms(xs,mapping)
//      case x :: Nil => if (mapping.contains(x)) DefinedSymptom(x,mapping(x)) :: Nil
//                      else UndefinedSymptom(x) :: Nil
      case Nil => Nil
    }
  }

  def producePredicates(diseases:Seq[Disease]) : Seq[(String,String,String)] = {

    var predicates = new ListBuffer[(String, String, String)]

    for (disease <- diseases) {
      disease match {
        case d:DefinedDisease => d.symptoms.map{
          case s:DefinedSymptom => {
            predicates +=  (("<" + d.uri + ">",HAS_ONTOLOGY_SYMPTOM, "<"+ s.uri +">"))
          }
          case _ =>
        }
        case _ =>
      }
    }

    predicates.toSeq

  }





}