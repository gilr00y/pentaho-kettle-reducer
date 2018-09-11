package com.expr.pentaho.reducer

import java.util.{ List => JList, Map => JMap }

import org.pentaho.di.core.{ CheckResultInterface, Counter }
import org.pentaho.di.core.row._
import org.pentaho.di.core.variables.VariableSpace
import org.pentaho.di.repository.{ ObjectId, Repository }
import org.pentaho.di.trans._
import org.pentaho.di.trans.step._

class ReducerStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) extends BaseStep(smi, sdi, copyNr, transMeta, trans) {
  def valOrNull(s: String) = if (s.isEmpty) null else s

  // Dummy wrapper for logging.
  override def init(smi: StepMetaInterface, sdi: StepDataInterface) = {
    logBasic("Initting RecuderStep < BaseStep")
    super.init(smi, sdi)
  }


  override def processRow(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = {
    val meta = smi.asInstanceOf[ReducerStepMeta]
    val rowMeta = Option(getInputRowMeta()).getOrElse(new RowMeta)
    logError("ROW META:")
    logError(rowMeta.toString())
    
    // Not sure what I'm supposed to do with this...
    val smiFields = smi.getFields(rowMeta, getStepname(), null, null, null)
    logError("SMI FIELDS")
    logError(smiFields.toString())
    //

    // Core row processing loop.
    try {
      while (true) {
        val row: Array[Object] = getRow()
        logError("RETRIEVE ROW")
        logError(row.toString())
        putRow(rowMeta, row)
        incrementLinesOutput()
      }
    } catch {
      case e: Exception => logError(e.toString())
    }
    // End row processing
    setOutputDone()
    true
  }

  override def dispose(smi: StepMetaInterface, sdi: StepDataInterface): Unit = {
    logError("DISPOSING in ReducerStep")
    super.dispose(smi, sdi)
  }
}


class ReducerStepMeta extends BaseStepMeta with StepMetaInterface {
  // If Kettle wants to live dangerously, I will, too!
  var token: String = ""
  var projectId: String = ""
  var queue: String = ""
  var outputField: String = "message"

  // def valueMeta() = new ValueMeta(outputField, ValueMetaInterface.TYPE_STRING)

  def getStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) =
    new ReducerStep(smi, sdi, copyNr, transMeta, trans)

  def getStepData() = new ReducerStepData

  def setDefault(): Unit = { token = ""; outputField = "message" }

  // override def check(remarks: JList[CheckResultInterface], meta: TransMeta, stepMeta: StepMeta, prev: RowMetaInterface, input: Array[String], output: Array[String], info: RowMetaInterface) = {
  //   // TODO: Implement a check
  // }

  // override def getFields(inputRowMeta: RowMetaInterface, name: String, info: Array[RowMetaInterface], nextStep: StepMeta, space: VariableSpace): Unit = {
  //   val v = valueMeta()
  //   v.setOrigin(name)
  //   inputRowMeta.addValueMeta(v)
  // }

  // override def getXML() =
  //   s"<settings><token>${token}</token><projectId>${projectId}</projectId><queue>${queue}</queue><outputField>${outputField}</outputField></settings>"

  // override def loadXML(node: org.w3c.dom.Node, databases: JList[DatabaseMeta], counters: JMap[String, Counter]): Unit = {
  //   println(s"Loading XML from $node")
  //   import javax.xml.xpath._
  //   val xpath = XPathFactory.newInstance.newXPath

  //   token = xpath.evaluate("//settings/token", node)
  //   projectId = xpath.evaluate("//settings/projectId", node)
  //   queue = xpath.evaluate("//settings/queue", node)
  //   outputField = xpath.evaluate("//settings/outputField", node)

  //   println(s"Loaded params: token = $token, projectId = $projectId, queue = $queue, outputField = $outputField")
  // }

  // override def readRep(rep: Repository, stepId: ObjectId, databases: JList[DatabaseMeta], counters: JMap[String, Counter]): Unit = {
  //   throw new UnsupportedOperationException("readRep")
  // }

  // override def saveRep(rep: Repository, transformId: ObjectId, stepId: ObjectId): Unit = {
  //   throw new UnsupportedOperationException("readRep")
  // }
}


class ReducerStepData extends BaseStepData with StepDataInterface
