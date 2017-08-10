package com.lmdb.main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
import org.eclipse.wb.swt.SWTResourceManager;

public class DirectSearch extends Dialog {

	protected Object result;
	protected Shell shlDirectSearch;
	private Text txtID;
	
	Connection c = null;
	Statement stmt = null;
	
	
	//Exchange variables
	public String id,name,name2,descr,type,history,plink,flink,extlink;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DirectSearch(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlDirectSearch.open();
		shlDirectSearch.layout();
		Display display = getParent().getDisplay();
		while (!shlDirectSearch.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	public void msgBoxInfo(String message, String title) {
		MessageBox messageBox = new MessageBox(shlDirectSearch, SWT.ICON_INFORMATION | SWT.OK);
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
	         shlDirectSearch.close();
	         
		} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlDirectSearch = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shlDirectSearch.setImage(SWTResourceManager.getImage(DirectSearch.class, "/com/lmdb/main/cube_grey.png"));
		shlDirectSearch.setMinimumSize(new Point(70, 25));
		shlDirectSearch.setSize(344, 173);
		shlDirectSearch.setText("Direct ID Search");
		shlDirectSearch.setBackgroundMode(SWT.INHERIT_DEFAULT);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		shlDirectSearch.setLocation(dim.width/2-620/2, dim.height/2-568/2);
		
		
		Button btnSearch = new Button(shlDirectSearch, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(txtID.getText().equals("")) return;
				fetchObjectData(txtID.getText());
			}
		});
		btnSearch.setText("Search");
		btnSearch.setBounds(10, 87, 295, 30);

		txtID = new Text(shlDirectSearch, SWT.BORDER);
		txtID.setBounds(203, 33, 102, 23);
		
		Label lblEnterProjectobjectId = new Label(shlDirectSearch, SWT.NONE);
		lblEnterProjectobjectId.setBounds(10, 33, 187, 22);
		lblEnterProjectobjectId.setText("Enter Project/Object ID:");



	}
}
