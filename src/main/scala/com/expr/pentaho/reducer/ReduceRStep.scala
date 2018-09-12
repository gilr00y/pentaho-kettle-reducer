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
    val data = sdi.asInstanceOf[ReducerStepData]
    
    val row: Array[Object] = getRow()

    if (row == null) {
      logBasic("Row is NULL, setting state to DONE")
      setOutputDone()
      return false
    }

    // Get inputRowMeta
    val rowMeta = Option(getInputRowMeta()).getOrElse(new RowMeta)
    if (first) {
      first=false
      // Apply new rowMeta entry
      smi.getFields(rowMeta, getStepname(), null, null, null)
    }
    var jgIdx: Int = rowMeta.indexOfValue("jg-test0")
    logError("JG IDX: " +jgIdx.toString())

    // Init output row with additional element
    val outRow: Array[Object] = RowDataUtil.resizeArray(row, rowMeta.size())
    // Copy old values to output row
    logBasic("IN ROW SIZE: " + row.length.toString())
    logBasic("OUT ROW SIZE: " + outRow.length.toString())
    // for(i <- rowMeta.size() until row.length){
    outRow(jgIdx) = "Test OK"
    // }

    // outRow(row.length) = rowMeta.getValueMeta(outRow.length).convertData(.conversionMeta[i], value)
    // Core row processing loop.
    putRow(rowMeta, outRow)
    incrementLinesOutput()
    return true
  }

  override def dispose(smi: StepMetaInterface, sdi: StepDataInterface): Unit = {
    super.dispose(smi, sdi)
  }
}


class ReducerStepMeta extends BaseStepMeta with StepMetaInterface {
  var outputField: String = "jg-test0"

  def valueMeta() = new ValueMeta(outputField, ValueMetaInterface.TYPE_STRING)

  def getStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) =
    new ReducerStep(smi, sdi, copyNr, transMeta, trans)

  def getStepData() = new ReducerStepData

  def setDefault(): Unit = { outputField = "jg-test0" }

  // override def check(remarks: JList[CheckResultInterface], meta: TransMeta, stepMeta: StepMeta, prev: RowMetaInterface, input: Array[String], output: Array[String], info: RowMetaInterface) = {
  //   // TODO: Implement a check
  // }

  override def getFields(inputRowMeta: RowMetaInterface, name: String, info: Array[RowMetaInterface], nextStep: StepMeta, space: VariableSpace): Unit = {
    val v = valueMeta()
    v.setOrigin(name)
    inputRowMeta.addValueMeta(v)
    logBasic("outgoing valueMeta:" + inputRowMeta.toString())
  }

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
