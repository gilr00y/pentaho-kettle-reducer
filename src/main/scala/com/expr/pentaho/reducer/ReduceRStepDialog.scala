package com.expr.pentaho.reducer

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CCombo
import org.eclipse.swt.events.{ModifyEvent, ModifyListener, SelectionAdapter, SelectionEvent}
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.pentaho.di.core.Const
import org.pentaho.di.trans._
import org.pentaho.di.trans.step._
import org.pentaho.di.ui.core.widget.{ColumnInfo, TableView}
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

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN)
    shell.setText("Reducer")
    props.setLook(shell)
    setShellImage(shell, stepMeta)

    val layout = new FormLayout
    layout.marginWidth = Const.FORM_MARGIN
    layout.marginHeight = Const.FORM_MARGIN
    shell.setLayout(layout)
    val stepName = makeRow(shell, "Step Name:", stepname, None)
    val groupBy = makeRow(shell, "Group By:", initialValue= ourMeta.groupBy, Some(stepName))
//    val tableContainer = makeRow(shell, "", "", Some(groupBy))
    //
//    var tableView: TableView = new TableView(
//      transMeta,
//      shell,
//      SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
//
//    )
    val wType = new CCombo(shell, 3 * margin)

    val wlKeys1 = new Label(shell, SWT.NONE)
    wlKeys1.setText("Group By:")
    props.setLook(wlKeys1)
    val fdlKeys1 = new FormData
    fdlKeys1.left = new FormAttachment(0, 0)
    fdlKeys1.top = new FormAttachment(wType, margin)
    wlKeys1.setLayoutData(fdlKeys1)

    val nrKeyRows1 = 10

    val ciKeys1 = Array[ColumnInfo](
      new ColumnInfo("Field:", ColumnInfo.COLUMN_TYPE_TEXT, false))

    val tableMod = new ModifyListener() {
      override def modifyText(e: ModifyEvent): Unit = {
        val targetWidget = e.widget.asInstanceOf[Text]
        val table = targetWidget.getParent.asInstanceOf[Table]

//        table.getItems => Array[TableItem]
        // TableItem:
        val cols = table.getColumns
        print(s"First column getData: ${cols(0).getData}")
        val items = table.getItems.asInstanceOf[Array[TableItem]]
        for ((item, idx) <- items zipWithIndex) {

          // item.getText => returns row "name" (eg 1,2,3,4...)
          // item.getData => returns ???
          print(s"Item $idx : ${item.getData}")
        }
        print(s"Table Items: ${table.getItems}")

      }
    }
    val wKeys1 = new TableView(
      transMeta,
      shell,
      SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
      ciKeys1,
      nrKeyRows1,
      tableMod,
      props)


    val fdKeys1 = new FormData
    fdKeys1.top = new FormAttachment(wlKeys1, margin)
    fdKeys1.left = new FormAttachment(0, 0)
    fdKeys1.bottom = new FormAttachment(100, -70)
    fdKeys1.right = new FormAttachment(50, -margin)
    wKeys1.setLayoutData(fdKeys1)

    val wbKeys1 = new Button(shell, SWT.PUSH)
    wbKeys1.setText("Get Fields")
    val fdbKeys1 = new FormData
    fdbKeys1.top = new FormAttachment(wKeys1, margin)
    fdbKeys1.left = new FormAttachment(0, 0)
    fdbKeys1.right = new FormAttachment(50, -margin)
    wbKeys1.setLayoutData(fdbKeys1)
    wbKeys1.addSelectionListener(new SelectionAdapter() {
      override def widgetSelected(e: SelectionEvent): Unit = {
        print("wbKeys1 selection Listener!!!")
      }
    })


    import org.eclipse.swt.SWT
    import org.eclipse.swt.events.{SelectionAdapter, SelectionEvent}
    import org.eclipse.swt.layout.{FormAttachment, FormData}
    import org.pentaho.di.ui.core.widget.ColumnInfo
    // THE KEYS TO MATCH for second step// THE KEYS TO MATCH for second step

    val wlKeys2 = new Label(shell, SWT.NONE)
    wlKeys2.setText("Reduce by:")
    props.setLook(wlKeys2)
    val fdlKeys2 = new FormData
    fdlKeys2.left = new FormAttachment(50, 0)
    fdlKeys2.top = new FormAttachment(wType, margin)
    wlKeys2.setLayoutData(fdlKeys2)

    val nrKeyRows2 = 10

    val ciKeys2 = Array[ColumnInfo](
      new ColumnInfo("Field", ColumnInfo.COLUMN_TYPE_TEXT, false),
      new ColumnInfo("Strategy", ColumnInfo.COLUMN_TYPE_TEXT_BUTTON , false)
    )

    val wKeys2 = new TableView(
      transMeta,
      shell,
      SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
      ciKeys2,
      nrKeyRows2,
      tableMod,
      props)

    val fdKeys2 = new FormData
    fdKeys2.top = new FormAttachment(wlKeys2, margin)
    fdKeys2.left = new FormAttachment(50, 0)
    fdKeys2.bottom = new FormAttachment(100, -70)
    fdKeys2.right = new FormAttachment(100, 0)
    wKeys2.setLayoutData(fdKeys2)

    val wbKeys2 = new Button(shell, SWT.PUSH)
    wbKeys2.setText("wbkeys2 button")
    val fdbKeys2 = new FormData
    fdbKeys2.top = new FormAttachment(wKeys2, margin)
    fdbKeys2.left = new FormAttachment(50, 0)
    fdbKeys2.right = new FormAttachment(100, 0)
    wbKeys2.setLayoutData(fdbKeys2)
    wbKeys2.addSelectionListener(new SelectionAdapter() {
      override def widgetSelected(e: SelectionEvent): Unit = {
        print("WBKeys2 selection listener fired!")
      }
    })

    // Some buttons
    wOK = new Button(shell, SWT.PUSH)
    wOK.setText("wOK Button")
    wCancel = new Button(shell, SWT.PUSH)
    wCancel.setText("wCancel button")

    setButtonPositions(Array[Button](wOK, wCancel), margin, wbKeys1)
/////////
//OLD IMPL
/////////
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
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
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
      formData.left = new FormAttachment(0, margin)
      formData.top = topForm
      formData.right = new FormAttachment(middle, -margin)
      formData
    }

    val field = new Text(shell, SWT.LEFT | SWT.BORDER)
    field.setText(initialValue)
    props.setLook(field)
    field.setLayoutData {
      val formData = new FormData
      formData.left = new FormAttachment(middle, 0)
      formData.top = topForm
      formData.right = new FormAttachment(100, 0)
      formData
    }

    field
  }
}
