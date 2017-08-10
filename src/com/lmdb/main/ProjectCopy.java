package com.lmdb.main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

public class ProjectCopy extends Dialog {

	protected Object result;
	protected Shell shlProjectCopy;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text txtID;
	
	Connection c = null;
	Statement stmt = null;
	
	
	//Exchange variables
	public String id,name,name2,descr,type,history,plink,flink,extlink;
	private Text txtName;
	private Text txtCopyLog;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ProjectCopy(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlProjectCopy.open();
		shlProjectCopy.layout();
		Display display = getParent().getDisplay();
		while (!shlProjectCopy.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	public void msgBoxInfo(String message, String title) {
		//JOptionPane.showMessageDialog(null,message,title,JOptionPane.INFORMATION_MESSAGE);
		
		MessageBox messageBox = new MessageBox(shlProjectCopy, SWT.ICON_INFORMATION | SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText(title);
		messageBox.open();
	}
	
	public void fetchObjectData(String objNumber) {
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	         
	         String sql = "SELECT * FROM LMDB_MASTER WHERE ID='" + objNumber + "';";
	         
	         ResultSet rs = stmt.executeQuery(sql);
	         if(!rs.isBeforeFirst()){
	        	 msgBoxInfo("Sorry, no object entry found for ID '"+ objNumber + "'.", "No DB entry");
	        	 return;
	         }
	         
	         while ( rs.next() ) {
	             name = rs.getString("NAME");
	             name2 = rs.getString("NAME2");
	             descr = rs.getString("DESCR");
	             history = rs.getString("HISTORY");
	             flink = rs.getString("FLINK");
	             plink = rs.getString("PLINK");
	             extlink = rs.getString("EXTLINK");
	             type =rs.getString("TYPE");
	             id = objNumber;
	         }
	         
	         rs.close();
	         stmt.close();
	         c.close();
	         shlProjectCopy.close();
	         
		} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlProjectCopy = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shlProjectCopy.setMinimumSize(new Point(70, 25));
		shlProjectCopy.setSize(441, 604);
		shlProjectCopy.setText("Copy a Project / Object");
		shlProjectCopy.setBackgroundMode(SWT.INHERIT_DEFAULT);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		shlProjectCopy.setLocation(dim.width/2-620/2, dim.height/2-568/2);
		
		
		Button btnCopy = new Button(shlProjectCopy, SWT.NONE);
		btnCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fetchObjectData(txtID.getText());
			}
		});
		btnCopy.setText("Start Copy");
		btnCopy.setBounds(10, 331, 193, 30);
		formToolkit.adapt(btnCopy, true, true);
		
		txtID = new Text(shlProjectCopy, SWT.BORDER);
		txtID.setBounds(227, 33, 102, 22);
		formToolkit.adapt(txtID, true, true);
		
		Label lblEnterProjectobjectId = new Label(shlProjectCopy, SWT.NONE);
		lblEnterProjectobjectId.setBounds(10, 33, 193, 22);
		formToolkit.adapt(lblEnterProjectobjectId, true, true);
		lblEnterProjectobjectId.setText("Enter source Project/Object ID:");
		
		Label lblWhichContentsDo = new Label(shlProjectCopy, SWT.NONE);
		lblWhichContentsDo.setBounds(10, 134, 421, 22);
		formToolkit.adapt(lblWhichContentsDo, true, true);
		lblWhichContentsDo.setText("Which contents do you want to copy (if the ID is a Project)?");
		
		Button checkLinkedCAD = new Button(shlProjectCopy, SWT.CHECK);
		checkLinkedCAD.setSelection(true);
		checkLinkedCAD.setBounds(10, 176, 193, 22);
		formToolkit.adapt(checkLinkedCAD, true, true);
		checkLinkedCAD.setText("All linked CAD-Objects");
		
		Button checkLinkedExport = new Button(shlProjectCopy, SWT.CHECK);
		checkLinkedExport.setText("All linked Export-Objects");
		checkLinkedExport.setBounds(10, 200, 193, 22);
		formToolkit.adapt(checkLinkedExport, true, true);
		
		Button checkLinkedDoc = new Button(shlProjectCopy, SWT.CHECK);
		checkLinkedDoc.setText("All linked Document-Objects");
		checkLinkedDoc.setBounds(10, 224, 193, 22);
		formToolkit.adapt(checkLinkedDoc, true, true);
		
		Button checkPicFolder = new Button(shlProjectCopy, SWT.CHECK);
		checkPicFolder.setText("The 'Pictures' folder");
		checkPicFolder.setBounds(10, 248, 193, 22);
		formToolkit.adapt(checkPicFolder, true, true);
		
		Label lblNewProjectName = new Label(shlProjectCopy, SWT.NONE);
		lblNewProjectName.setText("New Project/Object Name:");
		lblNewProjectName.setBounds(10, 82, 193, 22);
		formToolkit.adapt(lblNewProjectName, true, true);
		
		txtName = new Text(shlProjectCopy, SWT.BORDER);
		txtName.setBounds(227, 79, 204, 22);
		formToolkit.adapt(txtName, true, true);
		
		Button btnCancel = new Button(shlProjectCopy, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlProjectCopy.close();
			}
		});
		btnCancel.setText("Cancel");
		btnCancel.setBounds(238, 331, 193, 30);
		formToolkit.adapt(btnCancel, true, true);
		
		Label lblNoteAnObject = new Label(shlProjectCopy, SWT.NONE);
		lblNoteAnObject.setBounds(10, 300, 421, 22);
		formToolkit.adapt(lblNoteAnObject, true, true);
		lblNoteAnObject.setText("NOTE: An Object will be linked to the same Project as the source Object.");
		
		txtCopyLog = new Text(shlProjectCopy, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtCopyLog.setEditable(false);
		txtCopyLog.setBounds(10, 367, 421, 205);
		formToolkit.adapt(txtCopyLog, true, true);



	}
}
