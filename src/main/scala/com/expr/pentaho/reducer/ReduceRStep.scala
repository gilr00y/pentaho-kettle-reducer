package com.expr.pentaho.reducer

import java.util.{ List => JList, Map => JMap }
import scala.math

import org.pentaho.di.core.{ CheckResultInterface, Counter }
import org.pentaho.di.core.row._
import org.pentaho.di.core.variables.VariableSpace
import org.pentaho.di.repository.{ ObjectId, Repository }
import org.pentaho.di.trans._
import org.pentaho.di.trans.step._

class ReduceRule() {

}

object Reducer {
  def go(rowMeta: RowMetaInterface, agg: Array[Object], nxt: Array[Object]): Array[Array[Object]] = {
    val freqMaxIdx: Int = rowMeta.indexOfValue("max_freq")
    val freqMinIdx: Int = rowMeta.indexOfValue("min_freq")

    var combined: Boolean = false
    if((rowMeta.getInteger(nxt, freqMinIdx) <= rowMeta.getInteger(agg, freqMaxIdx)) & (rowMeta.getInteger(nxt, freqMaxIdx) >= rowMeta.getInteger(agg, freqMinIdx))) {
      combined = true
      val min: java.lang.Double = math.min(rowMeta.getInteger(agg, freqMinIdx), rowMeta.getInteger(nxt, freqMinIdx))
      val max: java.lang.Double = math.max(rowMeta.getInteger(agg, freqMaxIdx), rowMeta.getInteger(nxt, freqMaxIdx))
      agg(freqMinIdx) = min
      agg(freqMaxIdx) = max
      return Array(agg)
    }
    Array(agg, nxt)
  }
}

class ReducerStep(smi: StepMeta, sdi: StepDataInterface, copyNr: Int, transMeta: TransMeta, trans: Trans) extends BaseStep(smi, sdi, copyNr, transMeta, trans) {

  def valOrNull(s: String) = if (s.isEmpty) null else s
  override def init(smi: StepMetaInterface, sdi: StepDataInterface) = {
    super.init(smi, sdi)
  }

  override def processRow(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = {
    val meta = smi.asInstanceOf[ReducerStepMeta]
    val data = sdi.asInstanceOf[ReducerStepData]

    var lastRow: Array[Object] = getRow() // First row in the group.. need to initialize with something to retrieve `rowMeta`
    val rowMeta = Option(getInputRowMeta()).getOrElse(new RowMeta)
    val metaList = rowMeta.getValueMetaList()

    val groupBys = Array("_id")
    while(true) {
      if(first) {
        first = false
        // End loop here... nothing to reduce!
      } else {
        var nextRow = getRow()
        if(nextRow != null) {
          var grp = true
          
          // Check if all group-by conditions are met
          for (i <- 0 until groupBys.length) {
            val gbIdx = rowMeta.indexOfValue(groupBys(i))
            grp &= rowMeta.getString(lastRow, gbIdx) == rowMeta.getString(nextRow, gbIdx)
          }
          if(grp) {
            val aggArr: Array[Array[Object]] = Reducer.go(rowMeta, lastRow, nextRow)
            if(aggArr.length > 1) {
              putRow(rowMeta, aggArr(0))
              lastRow = aggArr(1)
            } else {
              lastRow = aggArr(0)
            }
          } else {
            // Write lastRow
            putRow(rowMeta, lastRow)
            lastRow = nextRow
            // 
          }
        } else {
          setOutputDone()
          return false
        }
      }
    }
    false
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
