package com.expr.pentaho.reducer

import org.eclipse.swt.SWT
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.pentaho.di.core.Const
import org.pentaho.di.trans._
import org.pentaho.di.trans.step._
import org.pentaho.di.ui.trans.step._


class ReducerStepDialog(parent: Shell, m: Object, transMeta: TransMeta, stepName: String)
    extends BaseStepDialog(parent, m.asInstanceOf[BaseStepMeta], transMeta, stepName)
    with StepDialogInterface {
  this.shell = parent

  private[this] val stepMeta = m.asInstanceOf[StepMetaInterface]
  private[this] val ourMeta = m.asInstanceOf[ReducerStepMeta]

  val middle = props.getMiddlePct
  val margin = Const.MARGIN

  def open(): String = {
    val parent = getParent()
    val display = parent.getDisplay()

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN )
    shell.setText("Reducer")
    props.setLook(shell)
    setShellImage(shell, stepMeta)

    val layout = new FormLayout
    layout.marginWidth = Const.FORM_MARGIN
    layout.marginHeight = Const.FORM_MARGIN
    shell.setLayout(layout)
    val stepName = makeRow(shell, "Step Name:", stepname, None)
    val groupBy = makeRow(shell, "Group By:", ourMeta.groupBy, Some(stepName))

    val okButton = new Button(shell, SWT.PUSH | SWT.RIGHT | SWT.SHIFT)
    okButton.setText("OK")
    okButton.addListener(SWT.Selection, new Listener() {
      def handleEvent(e: Event): Unit = {
        if (stepName.getText.nonEmpty && groupBy.getText.nonEmpty) {
          stepname = stepName.getText
          ourMeta.groupBy = groupBy.getText
          ourMeta.setChanged(true)
        }
        shell.dispose()
      }
    })

    val cancelButton = new Button(shell, SWT.PUSH)
    cancelButton.setText("Cancel")
    cancelButton.addListener(SWT.Selection, new Listener() {
      def handleEvent(e: Event): Unit = {
        stepname = null
        ourMeta.setChanged(false)
        shell.dispose()
      }
    })
    setButtonPositions(Array(okButton, cancelButton), margin, groupBy)
    setSize()
    shell.pack()
    shell.open()
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep()
      }
    }
    stepname
  }

  def makeRow(shell: Shell, labelText: String, initialValue: String, relativeTo: Option[Control]): Text = {
    val topForm = relativeTo match {
      case Some(control) => new FormAttachment(control, margin)
      case None => new FormAttachment(0, margin)
    }

    val label = new Label(shell, SWT.RIGHT)
    label.setText(labelText)
    props.setLook(label)
    label.setLayoutData {
      val formData = new FormData
      formData.left = new FormAttachment(0,margin)
      formData.top = topForm
      formData.right = new FormAttachment(middle, -margin)
      formData
    }

    val field = new Text(shell, SWT.LEFT | SWT.BORDER)
    field.setText(initialValue)
    props.setLook(field)
    field.setLayoutData {
      val formData = new FormData
      formData.left = new FormAttachment(middle,0)
      formData.top = topForm
      formData.right = new FormAttachment(100, 0)
      formData
    }

    field
  }
}
