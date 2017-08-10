package com.lmdb.main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.*;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.stlviewer.STLViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MainWindow {

	protected Shell shlLightweightModelDatabase;
	
	private Table tableOverview;
	private Table tableCAD;
	private Table tableExp;
	private Table tableDoc;
	
	private Text txtProjName;
	private Text txtProjName2;
	private Text txtProjExtLink;
	private Text txtProjDesc;
	private Text txtObjName;
	private Text txtObjName2;
	private Text txtObjLinkHDD;
	private Text txtObjExtLink;
	private Text txtObjDesc;
	private Text txtObjHistory;
	private Text txtProjID;
	private Text txtObjID;
	private Button btnEditProjProp;
	private Button btnAddObj;
	private Button btnOpenPictureFolder;
	private Button btnEditObjProp;
	private Button btnCheckIn;
	private Button btnCheckOut;
	private Button btnObjOpen;
	private Button btnObjDelete;
	private Button btnCreateSnapshot;
	private Button btnObjCancel;
	private Button btnOpenProjFold;
	private Button btnOpenSnapshots;
	
	private TabFolder tabMain;
	private TabFolder tabLinkedObj;
	
	private Combo comboObjType;
	
	public Boolean editProjActive=false, editObjActive=false;
	public Boolean isnew=false;
	
	public Integer tempID;
	
	Connection c = null;
	Statement stmt = null;
	private Text txtObjLinkedProj;
	public STLViewer stlViewer;
	

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		setObjDisabled();
		setProjDisabled();
		checkDatabase();
		shlLightweightModelDatabase.open();
		shlLightweightModelDatabase.layout();
		while (!shlLightweightModelDatabase.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public int getAutoIncrement(){
		int id=0;
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	         
	         String sql = "SELECT * FROM SQLITE_SEQUENCE";
	         
	         ResultSet rs = stmt.executeQuery(sql);
	         
	         while ( rs.next() ) {
	            id = rs.getInt("seq");
	         }
	         rs.close();
	         stmt.close();
	         c.close();
	         
		} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         return 0;
	    }
		return id;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public void msgBoxInfo(String message, String title) {
		MessageBox messageBox = new MessageBox(shlLightweightModelDatabase, SWT.ICON_INFORMATION | SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText(title);
		messageBox.open();
	}
	
	public void msgBoxWarning(String message, String title) {
		MessageBox messageBox = new MessageBox(shlLightweightModelDatabase, SWT.ICON_WARNING | SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText(title);
		messageBox.open();
	}
	
	public void msgBoxError(String message, String title) {
		MessageBox messageBox = new MessageBox(shlLightweightModelDatabase, SWT.ICON_ERROR | SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText(title);
		messageBox.open();
	}
	
	public void setObjDisabled() {
		txtObjID.setEditable(false);
		txtObjName.setEditable(false);
		txtObjName2.setEditable(false);
		txtObjDesc.setEditable(false);
		txtObjLinkHDD.setEditable(false);
		txtObjExtLink.setEditable(false);
		txtObjHistory.setEditable(false);
		comboObjType.setEnabled(false);
	}
	public void setObjEnabled() {
		txtObjName.setEditable(true);
		txtObjName2.setEditable(true);
		txtObjDesc.setEditable(true);
		txtObjLinkHDD.setEditable(true);
		txtObjExtLink.setEditable(true);
		txtObjHistory.setEditable(true);
		comboObjType.setEnabled(true);
		
	}
	public void clearObjTab(){
		txtObjID.setText("");
		txtObjLinkedProj.setText("");
		txtObjName.setText("");
		txtObjName2.setText("");
		txtObjDesc.setText("");
		txtObjLinkHDD.setText("");
		txtObjExtLink.setText("");
		txtObjHistory.setText("");
		comboObjType.select(0);
	}
	public void setObjBtnNew() {
		btnObjDelete.setEnabled(false);
		btnObjOpen.setEnabled(false);
		btnCheckIn.setEnabled(true);
		btnCheckOut.setEnabled(false);
		btnEditObjProp.setEnabled(true);
		btnCreateSnapshot.setEnabled(false);
		btnObjCancel.setEnabled(true);
	}
	public void setObjBtnEmpty() {
		btnObjDelete.setEnabled(false);
		btnObjOpen.setEnabled(false);
		btnCheckIn.setEnabled(false);
		btnCheckOut.setEnabled(false);
		btnEditObjProp.setEnabled(false);
		btnCreateSnapshot.setEnabled(false);
		btnObjCancel.setEnabled(false);
	}
	public void setObjBtnNormal() {
		btnObjDelete.setEnabled(true);
		btnObjOpen.setEnabled(true);
		btnCheckIn.setEnabled(true);
		btnCheckOut.setEnabled(true);
		btnEditObjProp.setEnabled(true);
		btnCreateSnapshot.setEnabled(true);
		btnObjCancel.setEnabled(true);
	}
	public void setProjDisabled() {
		txtProjName.setEditable(false);
		txtProjName2.setEditable(false);
		txtProjDesc.setEditable(false);
		txtProjExtLink.setEditable(false);
		btnAddObj.setEnabled(false);
		btnOpenPictureFolder.setEnabled(false);
		btnOpenProjFold.setEnabled(false);
		btnEditProjProp.setEnabled(false);
		btnOpenSnapshots.setEnabled(false);
	}
	public void setProjEnabled() {
		txtProjName.setEditable(true);
		txtProjName2.setEditable(true);
		txtProjDesc.setEditable(true);
		txtProjExtLink.setEditable(true);
		btnAddObj.setEnabled(true);
		btnOpenPictureFolder.setEnabled(true);
		btnOpenProjFold.setEnabled(true);
		btnEditProjProp.setEnabled(true);
		btnOpenSnapshots.setEnabled(true);
	}
	public void setProjBtnEnabled() {
		btnAddObj.setEnabled(true);
		btnOpenPictureFolder.setEnabled(true);
		btnOpenProjFold.setEnabled(true);
		btnEditProjProp.setEnabled(true);
		btnOpenSnapshots.setEnabled(true);
	}
	public void setProjBtnEdit() {
		btnAddObj.setEnabled(false);
		btnOpenPictureFolder.setEnabled(false);
		btnOpenProjFold.setEnabled(false);
		btnEditProjProp.setEnabled(true);
		btnOpenSnapshots.setEnabled(false);
	}
	public void clearProjTab(){
		txtProjID.setText("");
		txtProjName.setText("");
		txtProjName2.setText("");
		txtProjDesc.setText("");
		txtProjExtLink.setText("");
	}
	
	public void readAllDBEntries(){
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	         
	         String sql = "SELECT * FROM LMDB_MASTER WHERE TYPE='project'";
	         
	         ResultSet rs = stmt.executeQuery(sql);
	         
	         while ( rs.next() ) {
	            int id = rs.getInt("ID");
	            if(id==1000) continue;
	            String  name = rs.getString("NAME");
	            String name2  = rs.getString("NAME2");
	            
	            TableItem it1 = new TableItem(tableOverview,SWT.NONE);
	            it1.setText(new String[]{Integer.toString(id),name,name2});
	         }
	         rs.close();
	         stmt.close();
	         c.close();
	         
	         if(tableOverview.getItemCount()>0) tableOverview.select(0);
	         
		} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	}
	
	public void readAllObjectEntries(String pNumber) {
		tableCAD.removeAll();
		tableExp.removeAll();
		tableDoc.removeAll();
		
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	         
	         String sql = "SELECT * FROM LMDB_MASTER WHERE PLINK=" + pNumber + " AND (TYPE='cad-3d' OR TYPE='cad-2d' OR TYPE='doc' OR TYPE='export');";
	         
	         ResultSet rs = stmt.executeQuery(sql);
	         
	         while ( rs.next() ) {
	            int id = rs.getInt("ID");
	            if(id==1000) continue;
	            String  name = rs.getString("NAME");
	            String name2  = rs.getString("NAME2");
	            String  linkHDD = rs.getString("FLINK");
	            String objType  = rs.getString("TYPE");
	            
	            if(objType.equals("cad-3d")||objType.equals("cad-2d")){
	            	TableItem it1 = new TableItem(tableCAD,SWT.NONE);
		            it1.setText(new String[]{Integer.toString(id),objType,name,name2,FilenameUtils.getName(linkHDD)});
	            }else if(objType.equals("export")){
	            	TableItem it1 = new TableItem(tableExp,SWT.NONE);
		            it1.setText(new String[]{Integer.toString(id),name,name2,FilenameUtils.getName(linkHDD)});
	            }else if(objType.equals("doc")){
	            	TableItem it1 = new TableItem(tableDoc,SWT.NONE);
		            it1.setText(new String[]{Integer.toString(id),name,name2,FilenameUtils.getName(linkHDD)});
	            }
	            
	         }
	         rs.close();
	         stmt.close();
	         c.close();
	         
	         tabLinkedObj.setSelection(0);
	         if(tableCAD.getItemCount()>0) tableCAD.select(0);
	         
		} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	}
	
	public void checkDatabase() {
		//check db file
		if (!(new File("modeldata.db").exists()))
		{
			msgBoxWarning("No Database file was found. 'modeldata.db' will be created and initialized now.", "No Database file");
			//create file
		      
		      try {
		         Class.forName("org.sqlite.JDBC");
		         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
		         stmt = c.createStatement();
		         
		         String sql = "CREATE TABLE LMDB_MASTER " +
		                        "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
		                        " NAME           TEXT    NOT NULL, " + 
		                        " NAME2          TEXT, " + 
		                        " TYPE           TEXT    NOT NULL, " + 
		                        " DESCR          TEXT, " + 
		                        " HISTORY        TEXT, " + 
		                        " PLINK			 INT, " + 
		                        " FLINK          TEXT, " +
		                        " EXTLINK        TEXT)";
		         
		         stmt.executeUpdate(sql);
		         
		         //insert initial entry
		         sql = "INSERT INTO LMDB_MASTER (ID,NAME,NAME2,TYPE,DESCR,HISTORY,PLINK,FLINK,EXTLINK) " +
	                        "VALUES (1000, 'initial', '', 'project', '', '', '', '', '' );"; 
		         stmt.executeUpdate(sql);
		         
		         stmt.close();
		         //c.commit();
		         c.close();
		         
		      } catch ( Exception e ) {
		         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      }
		}else {
			readAllDBEntries();
		}
	}
	
	public void saveNewProject() {
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	  
	         //insert new Project entry
	         String sql = "INSERT INTO LMDB_MASTER (NAME,NAME2,TYPE,DESCR,HISTORY,PLINK,FLINK,EXTLINK) " +
                       "VALUES ('" + txtProjName.getText() + "', '" + txtProjName2.getText() + "', 'project', '" + txtProjDesc.getText() + "', '', '', '', '" + txtProjExtLink.getText() + "' );"; 
	         stmt.executeUpdate(sql);
	         
	         stmt.close();
	         //c.commit();
	         c.close();
	         
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
		
		//create Project Folders
		File projPath = new File(new File(txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")),"CAD");
		boolean creationSuccessful = projPath.mkdirs();
		projPath = new File(new File(txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")),"Export");
		creationSuccessful = projPath.mkdirs();
		projPath = new File(new File(txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")),"Pictures");
		creationSuccessful = projPath.mkdirs();
		projPath = new File(new File(txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")),"Doc");
		creationSuccessful = projPath.mkdirs();
        if (!creationSuccessful) {
        	msgBoxError("The Project Directory cannot be created (maybe due to File Permissions)." , "Creating Directory failed");
        	return;
        }
        
	}
	
	public void saveNewObject() {
		String filepath_target="";
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	         //set target dolfer for filetype
	         String target_folder="";
	         if(comboObjType.getText().equals("cad-3d")||comboObjType.getText().equals("cad-2d")) target_folder="/CAD/";
	         if(comboObjType.getText().equals("export")) target_folder="/Export/";
	         if(comboObjType.getText().equals("doc")) target_folder="/Doc/";
	         
	         if(!txtObjLinkHDD.getText().isEmpty()) {
	        	 filepath_target = txtProjID.getText() + "_" + txtProjName.getText() + target_folder + txtObjID.getText() + "_" + FilenameUtils.getName(txtObjLinkHDD.getText()).replaceAll(" ", "_");
	         }
	  
	         //insert new Object entry
	         String sql = "INSERT INTO LMDB_MASTER (NAME,NAME2,TYPE,DESCR,HISTORY,PLINK,FLINK,EXTLINK) " +
                       "VALUES ('" + txtObjName.getText() + "', '" + txtObjName2.getText() + "', '" + comboObjType.getText() + "', '" + txtObjDesc.getText() + "', '" + txtObjHistory.getText() + "', '" + txtObjLinkedProj.getText() + "', '"+ filepath_target + "', '" + txtObjExtLink.getText() + "' );"; 
	         stmt.executeUpdate(sql);
	         
	         stmt.close();
	         //c.commit();
	         c.close();
	         
	      } catch ( Exception e ) {
	    	  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	  return;
	      }
		
		//copy file, if link field is not empty
		if(!txtObjLinkHDD.getText().isEmpty()) {	
			try {
				File file_src = new File(txtObjLinkHDD.getText());
				File file_copy = new File(filepath_target);
				
				FileUtils.copyFile(file_src, file_copy);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		//reload Object Tables
		tableCAD.removeAll();
		tableExp.removeAll();
		tableDoc.removeAll();
		readAllObjectEntries(txtObjLinkedProj.getText());
		
		//reload Object Attr
		fetchObjectData(txtObjID.getText(),false);

	}
	
	public void deleteProject(String pNumber, String pName) {
		pName = pName.replaceAll(" ", "_");
		
		MessageBox messageBox = new MessageBox(shlLightweightModelDatabase, SWT.ICON_QUESTION
				| SWT.YES | SWT.NO);
		messageBox.setMessage("The Project '" + pNumber + "_" + pName + "' + all related Objects will be deleted. Sure?");
		messageBox.setText("Delete Project Data");
		int response = messageBox.open();
		if (response == SWT.NO)
			return;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			//Delete Project item
			String sql = "DELETE FROM LMDB_MASTER WHERE ID=" + pNumber + ";";
			stmt.executeUpdate(sql);
			c.commit();
			//Delete all Project related Objects
			sql = "DELETE FROM LMDB_MASTER WHERE PLINK=" + pNumber + ";";
			stmt.executeUpdate(sql);
			c.commit();
			
			stmt.close();
			c.close();
			
			tableOverview.removeAll();
			readAllDBEntries();

			//Delete Directory with Subfolders
			try {
				if (new File(pNumber+"_"+pName).exists())
				{
					File projPath = new File(pNumber+"_"+pName);
					FileUtils.deleteDirectory(projPath);
				}
			} catch( IOException e) {

				return;
			}
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public void deleteObject(String objNumber) {
		MessageBox messageBox = new MessageBox(shlLightweightModelDatabase, SWT.ICON_QUESTION
				| SWT.YES | SWT.NO);
		messageBox.setMessage("The Object '" + objNumber + "' will be deleted. Sure?");
		messageBox.setText("Delete Object Data");
		int response = messageBox.open();
		if (response == SWT.NO)
			return;
		//update object tab
		fetchObjectData(objNumber, true);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			//Delete Project item
			String sql = "DELETE FROM LMDB_MASTER WHERE ID=" + objNumber + ";";
			stmt.executeUpdate(sql);
			c.commit();
			
			stmt.close();
			c.close();
			
			if (new File(txtObjLinkHDD.getText()).exists())
			{
				File objPath = new File(txtObjLinkHDD.getText());
				objPath.delete();
			}
			
			tableCAD.removeAll();
			tableExp.removeAll();
			tableDoc.removeAll();
			if(txtProjID.getText().equals("")){
				setProjDisabled();
				clearProjTab();
				tabMain.setSelection(0);
			}else readAllObjectEntries(txtProjID.getText());

			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		
		tabMain.setSelection(1);
		clearObjTab();
		setObjDisabled();
		setObjBtnEmpty();
	}
	
	public void updateProject() {
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	  
	         String sql = "UPDATE LMDB_MASTER SET NAME='"+txtProjName.getText()+ "',NAME2='"+txtProjName2.getText()+"',DESCR='"+txtProjDesc.getText()+"',FLINK='',EXTLINK='"+txtProjExtLink.getText()+"' WHERE ID="+txtProjID.getText()+ ";";
	         
	         stmt.executeUpdate(sql);
	         
	         stmt.close();
	         c.close();
	         
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
	}
	
	public void updateObject() {
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();

	         String sql = "UPDATE LMDB_MASTER SET NAME='"+txtObjName.getText()+ "',NAME2='"+txtObjName2.getText()+"',TYPE='" + comboObjType.getText() + "',DESCR='"+txtObjDesc.getText()+"',HISTORY='" + txtObjHistory.getText() + "',FLINK='"+txtObjLinkHDD.getText()+"',EXTLINK='"+txtObjExtLink.getText()+"' WHERE ID="+txtObjID.getText()+ ";";
	         
	         stmt.executeUpdate(sql);
	         
	         stmt.close();
	         c.close();
	         
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
	}
	
	public void fetchProjectData(String pNumber) {
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	         
	         String sql = "SELECT * FROM LMDB_MASTER WHERE ID='" + pNumber + "';";
	         
	         ResultSet rs = stmt.executeQuery(sql);
	         
	         while ( rs.next() ) {
	             String  name = rs.getString("NAME");
	             String  name2 = rs.getString("NAME2");
	             String  descr = rs.getString("DESCR");
	             String  thingiverse = rs.getString("EXTLINK");
	             
	        	 txtProjID.setText(pNumber);
	             txtProjName.setText(name);
	             txtProjName2.setText(name2);
	             txtProjDesc.setText(descr);
	             txtProjExtLink.setText(thingiverse);
	             
	         }
	         rs.close();
	         stmt.close();
	         c.close();
	         isnew=false;
	         
	         tabMain.setSelection(1);
	         setProjBtnEnabled();
	         
	         clearObjTab();
	         setObjBtnEmpty();
	         setObjDisabled();
	         
		} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	}
	
	public  void fetchObjectData(String objNumber, Boolean background) {
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
	         stmt = c.createStatement();
	         
	         String sql = "SELECT * FROM LMDB_MASTER WHERE ID='" + objNumber + "';";
	         
	         ResultSet rs = stmt.executeQuery(sql);
	         
	         while ( rs.next() ) {
	             String  name = rs.getString("NAME");
	             String  name2 = rs.getString("NAME2");
	             String  descr = rs.getString("DESCR");
	             String  history = rs.getString("HISTORY");
	             String  hddlink = rs.getString("FLINK");
	             String  linkedProj = rs.getString("PLINK");
	             String  thingiverse = rs.getString("EXTLINK");
	             String  type =rs.getString("TYPE");
	             
	             if(type.equals("cad-3d")) comboObjType.select(0);
	             else if(type.equals("cad-2d")) comboObjType.select(1);
	             else if(type.equals("export")) comboObjType.select(2);
	             else if(type.equals("doc")) comboObjType.select(3);
	             
	        	 txtObjID.setText(objNumber);
	             txtObjName.setText(name);
	             txtObjName2.setText(name2);
	             txtObjDesc.setText(descr);
	             txtObjHistory.setText(history);
	             txtObjLinkHDD.setText(hddlink);
	             txtObjExtLink.setText(thingiverse);
	             txtObjLinkedProj.setText(linkedProj);
	         }
	         
	         rs.close();
	         stmt.close();
	         c.close();
	         
	         if(!background) tabMain.setSelection(2);
	         setObjBtnNormal();
	         
		} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	}
	
	public void fileCheckIn(String filepath, String oldFilepath) {
		String filepath_target="";
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
			stmt = c.createStatement();

			//set target dolfer for filetype
			String target_folder="";
			if(comboObjType.getText().equals("cad-3d")||comboObjType.getText().equals("cad-2d")) target_folder="/CAD/";
			if(comboObjType.getText().equals("export")) target_folder="/Export/";
			if(comboObjType.getText().equals("doc")) target_folder="/Doc/";

			//If ID in Filename -> Remove it, otherwise not skip
			String[] tmp = FilenameUtils.getName(filepath).split("_");
			String filename_in = "";
			if(tmp.length>0){
				if(isInteger(tmp[0])){
					filename_in = FilenameUtils.getName(filepath).replaceAll(" ", "_").substring(5);
				}
				else {
					filename_in = FilenameUtils.getName(filepath).replaceAll(" ", "_");
				}
			}
			
			filepath_target = txtObjLinkedProj.getText() + "_" + txtProjName.getText() + target_folder + txtObjID.getText() + "_" + filename_in;

			//copy file, if link field is not empty
			try {
				//Delete old File
				if (new File(oldFilepath).exists()&&!oldFilepath.isEmpty())
				{
					File objPath = new File(oldFilepath);
					objPath.delete();
				}

				//copy new File
				File file_src = new File(filepath);
				File file_copy = new File(filepath_target);

				FileUtils.copyFile(file_src, file_copy);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			//Write changes in Database
			String timestamp = new SimpleDateFormat("dd-MM-yyyy-HH-mm").format(new Date());
			String history = txtObjHistory.getText() + timestamp + ": Check-in.\n";
			String sql = "UPDATE LMDB_MASTER SET FLINK='"+filepath_target+"',HISTORY='" + history + "' WHERE ID="+txtObjID.getText()+ ";";

			stmt.executeUpdate(sql);

			stmt.close();
			c.close();

		} catch ( Exception e ) {
			e.printStackTrace();
			return;
		}


		//reload Object Tables
		tableCAD.removeAll();
		tableExp.removeAll();
		tableDoc.removeAll();
		readAllObjectEntries(txtObjLinkedProj.getText());

		//reload Object Attr
		fetchObjectData(txtObjID.getText(),false);
	}
	
	public void fileCheckOut(String target) {
		File file_src = new File(txtObjLinkHDD.getText());
		File file_copy = new File(target, FilenameUtils.getName(txtObjLinkHDD.getText()));

		try {
			FileUtils.copyFile(file_src, file_copy);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msgBoxError("Cannot check out the selected file maybe due to filesystem permissions.", "Check out Error");
		}
	}
	
	public void createObjectSnapshot() {
		File dumpDir = new File (txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")+"/CAD/Snapshots");
		if(!dumpDir.exists()){
			boolean creationSuccessful = dumpDir.mkdirs();
			if (!creationSuccessful) {
				msgBoxError("The version snapshot directory cannot be created (maybe due to Filesystem Permissions)." , "Creating Directory failed");
				return;
			}
		}
		
		//copy the object file and appending timestamp to filename
		String timestamp = new SimpleDateFormat("dd-MM-yyyy-HH-mm").format(new Date());
		String copyTargetFilename = txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")+"/CAD/Snapshots/"+FilenameUtils.getBaseName(txtObjLinkHDD.getText())+"_"+timestamp+"."+FilenameUtils.getExtension(txtObjLinkHDD.getText());
		
		try{
			File file_src = new File(txtObjLinkHDD.getText());
			File file_copy = new File(copyTargetFilename);

			FileUtils.copyFile(file_src, file_copy);
			
			msgBoxInfo("Sucessfully created file snapshot:\n"+copyTargetFilename, "Snapshot created");

		} catch(IOException e) {
			e.printStackTrace();
			msgBoxError("The object file cannot be copied (maybe due to File Permissions)." , "File Copy Error");
			return;
		}
	}

	public void createDBSnapshot(){
		//copy and paste the DB-File with timestamp
		String timestamp = new SimpleDateFormat("dd-MM-yyyy-HH-mm").format(new Date());
		String copyTargetFilename = "modeldata_"+ timestamp+".db";

		try{
			File file_src = new File("modeldata.db");
			File file_copy = new File(copyTargetFilename);

			FileUtils.copyFile(file_src, file_copy);

			msgBoxInfo("Sucessfully created database snapshot:\n"+copyTargetFilename, "Snapshot created");

		} catch(IOException e) {
			e.printStackTrace();
			msgBoxError("The database file cannot be copied (maybe due to File Permissions)." , "File Copy Error");
			return;
		}
	}
	
	public void viewSTL(String id){
		//Check if window is already open
		if(stlViewer!=null){
			if(stlViewer.windowOpen) {
				msgBoxInfo("Please close the viewer window before opening a new one.", "Viewer busy");
				return;
			}

		}

		//fetch object file path
		String stlFilePath="";
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:modeldata.db");
			stmt = c.createStatement();

			String sql = "SELECT FLINK FROM LMDB_MASTER WHERE ID='" + id + "';";

			ResultSet rs = stmt.executeQuery(sql);

			while ( rs.next() ) {
				stlFilePath = rs.getString("FLINK");
			}
			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}

		String filext = FilenameUtils.getExtension(stlFilePath);
		if(filext.equals("stl") || filext.equals("STL")){

			if(!new File(stlFilePath).exists()){
				msgBoxError("STL File not found.", "File not found");
				return;
			}

			//Open Preview Window
			stlViewer = new STLViewer(null);
			stlViewer.windowOpen=true;
			stlViewer.loadExtFile(new File(stlFilePath));
			
		}else {
			msgBoxInfo("Sorry, only *.stl files allowed at the moment.", "Wrong file format");
			return;
		}

	}


	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlLightweightModelDatabase = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shlLightweightModelDatabase.setImage(SWTResourceManager.getImage(MainWindow.class, "/com/lmdb/main/cube_grey.png"));
		shlLightweightModelDatabase.setSize(1225, 778);
		shlLightweightModelDatabase.setMinimumSize(1225, 778);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		shlLightweightModelDatabase.setLocation(dim.width/2-1225/2, dim.height/2-778/2);
		shlLightweightModelDatabase.setText("Lightweight Model Database 1.0.15");
		
		tabMain = new TabFolder(shlLightweightModelDatabase, SWT.NONE);
		tabMain.setBounds(10, 10, 1190, 686);
		
		TabItem tbtmOverview = new TabItem(tabMain, SWT.NONE);
		tbtmOverview.setText("Overview");
		
		Composite composite = new Composite(tabMain, SWT.NONE);
		tbtmOverview.setControl(composite);
		
		Button btnAddProj = new Button(composite, SWT.NONE);
		btnAddProj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Check, if a project is already in edit-state
				if(editProjActive||editObjActive){
					msgBoxInfo("A project or object is currently in edit mode.\nPlease finish or cancel it before switching the displayed item.", "Item view");
					return;
				}
				//change view to project and enable input fields
				editProjActive=true;
				clearProjTab();
				tableCAD.removeAll();
				tableExp.removeAll();
				tableDoc.removeAll();
				setProjEnabled();
				setProjBtnEdit();
				tabMain.setSelection(1);
				btnEditProjProp.setText("Save Project Prop");
				isnew=true;
				
				//get next id
				txtProjID.setText(Integer.toString(getAutoIncrement()+1));
			}
		});
		btnAddProj.setBounds(10, 10, 127, 28);
		btnAddProj.setText("Add Project");
		
		Button btnDeleteProj = new Button(composite, SWT.NONE);
		btnDeleteProj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem item = tableOverview.getItem(tableOverview.getSelectionIndex());

				deleteProject(item.getText(0), item.getText(1));
			}
		});
		btnDeleteProj.setBounds(10, 44, 127, 28);
		btnDeleteProj.setText("Delete Project");
		
		tableOverview = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableOverview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//Check, if a project is already in edit-state
				if(editProjActive||editObjActive){
					msgBoxInfo("A project or object is currently in edit mode.\nPlease finish or cancel it before switching the displayed item.", "Item view");
					return;
				}
				if(tableOverview.getSelection().length==0) return;
				TableItem item = tableOverview.getItem(tableOverview.getSelectionIndex());

				fetchProjectData(item.getText(0));
				readAllObjectEntries(item.getText(0));
			}
		});
		tableOverview.setBounds(143, 10, 1029, 633);
		tableOverview.setHeaderVisible(true);
		tableOverview.setLinesVisible(true);
		
		TableColumn tblclmnProjectId = new TableColumn(tableOverview, SWT.NONE);
		tblclmnProjectId.setWidth(70);
		tblclmnProjectId.setText("Project ID");
		
		TableColumn tblclmnProjName = new TableColumn(tableOverview, SWT.NONE);
		tblclmnProjName.setWidth(310);
		tblclmnProjName.setText("Project Name");
		
		TableColumn tblclmnProjName2 = new TableColumn(tableOverview, SWT.NONE);
		tblclmnProjName2.setWidth(310);
		tblclmnProjName2.setText("Project Name 2");
		
		TabItem tbtmProject = new TabItem(tabMain, SWT.NONE);
		tbtmProject.setText("Project");
		
		Composite composite_1 = new Composite(tabMain, SWT.NONE);
		tbtmProject.setControl(composite_1);
		
		tabLinkedObj = new TabFolder(composite_1, SWT.NONE);
		tabLinkedObj.setBounds(10, 127, 812, 502);
		
		tableCAD = new Table(tabLinkedObj, SWT.BORDER | SWT.FULL_SELECTION);
		
		TabItem tbtmCad = new TabItem(tabLinkedObj, SWT.NONE);
		tbtmCad.setText("CAD");
		
		//Create context menu CAD table
		final Menu menuTableCAD = new Menu(tableCAD);
		
		MenuItem mntmOpenInViewer_1 = new MenuItem(menuTableCAD, SWT.NONE);
		mntmOpenInViewer_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tableCAD.getSelection().length==0) return;
				TableItem item = tableCAD.getItem(tableCAD.getSelectionIndex());

				viewSTL(item.getText(0));
			}
		});
		mntmOpenInViewer_1.setText("Open in Viewer");
		MenuItem itemDeleteCad = new MenuItem(menuTableCAD, SWT.PUSH);
		itemDeleteCad.setText("Delete");
		itemDeleteCad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tableCAD.getSelection().length==0) return;
				TableItem item = tableCAD.getItem(tableCAD.getSelectionIndex());

				deleteObject(item.getText(0));
			}
		});
        tableCAD.setMenu(menuTableCAD);
        
        tbtmCad.setControl(tableCAD);
        
		tableCAD.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//Check, if a project is already in edit-state
				if(editProjActive||editObjActive){
					msgBoxInfo("A project or object is currently in edit mode.\nPlease finish or cancel it before switching the displayed item.", "Item view");
					return;
				}
				if(tableCAD.getSelection().length==0) return;
				TableItem item = tableCAD.getItem(tableCAD.getSelectionIndex());

				fetchObjectData(item.getText(0),false);
			}
		});
		
		tableCAD.setHeaderVisible(true);
		tableCAD.setLinesVisible(true);
		
		TableColumn tblclmnObjID = new TableColumn(tableCAD, SWT.NONE);
		tblclmnObjID.setWidth(69);
		tblclmnObjID.setText("ID");
		
		TableColumn tblclmnObjType = new TableColumn(tableCAD, SWT.NONE);
		tblclmnObjType.setWidth(77);
		tblclmnObjType.setText("Type");
		
		TableColumn tblclmnObjName = new TableColumn(tableCAD, SWT.NONE);
		tblclmnObjName.setWidth(165);
		tblclmnObjName.setText("Name");
		
		TableColumn tblclmnObjName2 = new TableColumn(tableCAD, SWT.NONE);
		tblclmnObjName2.setWidth(165);
		tblclmnObjName2.setText("Name 2");
		
		TableColumn tblclmnObjCadFN = new TableColumn(tableCAD, SWT.NONE);
		tblclmnObjCadFN.setWidth(200);
		tblclmnObjCadFN.setText("Filename");
		
		TabItem tbtmExport = new TabItem(tabLinkedObj, SWT.NONE);
		tbtmExport.setText("Export");
		
		tableExp = new Table(tabLinkedObj, SWT.BORDER | SWT.FULL_SELECTION);
		tableExp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//Check, if a project is already in edit-state
				if(editProjActive||editObjActive){
					msgBoxInfo("A project or object is currently in edit mode.\nPlease finish or cancel it before switching the displayed item.", "Item view");
					return;
				}
				if(tableExp.getSelection().length==0) return;
				TableItem item = tableExp.getItem(tableExp.getSelectionIndex());

				fetchObjectData(item.getText(0),false);
			}
		});

		//Create context menu Export table
		final Menu menuTableExp= new Menu(tableExp);
		
		MenuItem mntmOpenInViewer = new MenuItem(menuTableExp, SWT.NONE);
		mntmOpenInViewer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tableExp.getSelection().length==0) return;
				TableItem item = tableExp.getItem(tableExp.getSelectionIndex());

				viewSTL(item.getText(0));
			}
		});
		mntmOpenInViewer.setText("Open in Viewer");
		MenuItem itemDeleteExp = new MenuItem(menuTableExp, SWT.PUSH);
		itemDeleteExp.setText("Delete");
		itemDeleteExp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tableExp.getSelection().length==0) return;
				TableItem item = tableExp.getItem(tableExp.getSelectionIndex());

				deleteObject(item.getText(0));
			}
		});
		tableExp.setMenu(menuTableExp);

		tbtmExport.setControl(tableExp);
		tableExp.setHeaderVisible(true);
		tableExp.setLinesVisible(true);
		
		TableColumn tableColumn = new TableColumn(tableExp, SWT.NONE);
		tableColumn.setWidth(69);
		tableColumn.setText("ID");
		
		TableColumn tableColumn_1 = new TableColumn(tableExp, SWT.NONE);
		tableColumn_1.setWidth(165);
		tableColumn_1.setText("Name");
		
		TableColumn tableColumn_2 = new TableColumn(tableExp, SWT.NONE);
		tableColumn_2.setWidth(165);
		tableColumn_2.setText("Name 2");
		
		TableColumn tblclmnObjExpFN = new TableColumn(tableExp, SWT.NONE);
		tblclmnObjExpFN.setWidth(250);
		tblclmnObjExpFN.setText("Filename");
		
		TabItem tbtmDocuments = new TabItem(tabLinkedObj, SWT.NONE);
		tbtmDocuments.setText("Documents");
		
		tableDoc = new Table(tabLinkedObj, SWT.BORDER | SWT.FULL_SELECTION);
		tableDoc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//Check, if a project is already in edit-state
				if(editProjActive||editObjActive){
					msgBoxInfo("A project or object is currently in edit mode.\nPlease finish or cancel it before switching the displayed item.", "Item view");
					return;
				}
				if(tableDoc.getSelection().length==0) return;
				TableItem item = tableDoc.getItem(tableDoc.getSelectionIndex());

				fetchObjectData(item.getText(0),false);
			}
		});
		tbtmDocuments.setControl(tableDoc);
		tableDoc.setHeaderVisible(true);
		tableDoc.setLinesVisible(true);
		
		//Create context menu Documents table
		final Menu menuTableDoc= new Menu(tableDoc);
		MenuItem itemDeleteDoc = new MenuItem(menuTableDoc, SWT.PUSH);
		itemDeleteDoc.setText("Delete");
		itemDeleteDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tableDoc.getSelection().length==0) return;
				TableItem item = tableDoc.getItem(tableDoc.getSelectionIndex());

				deleteObject(item.getText(0));
			}
		});
		tableDoc.setMenu(menuTableDoc);
		
		TableColumn tableColumn_3 = new TableColumn(tableDoc, SWT.NONE);
		tableColumn_3.setWidth(69);
		tableColumn_3.setText("ID");
		
		TableColumn tableColumn_4 = new TableColumn(tableDoc, SWT.NONE);
		tableColumn_4.setWidth(165);
		tableColumn_4.setText("Name");
		
		TableColumn tableColumn_5 = new TableColumn(tableDoc, SWT.NONE);
		tableColumn_5.setWidth(165);
		tableColumn_5.setText("Name 2");
		
		TableColumn tblclmnObjDocFN = new TableColumn(tableDoc, SWT.NONE);
		tblclmnObjDocFN.setWidth(250);
		tblclmnObjDocFN.setText("Filename");
		
		Label lblProjectName = new Label(composite_1, SWT.NONE);
		lblProjectName.setBounds(10, 38, 123, 20);
		lblProjectName.setText("Project Name:");
		
		Label lblProjectName_1 = new Label(composite_1, SWT.NONE);
		lblProjectName_1.setText("Project Name 2:");
		lblProjectName_1.setBounds(10, 64, 123, 20);
		
		Label lblExternalLinks = new Label(composite_1, SWT.NONE);
		lblExternalLinks.setText("External Link(s):");
		lblExternalLinks.setBounds(10, 90, 123, 20);
		
		Label lblProjectDescription = new Label(composite_1, SWT.NONE);
		lblProjectDescription.setBounds(841, 127, 147, 28);
		lblProjectDescription.setText("Project Description:");
		
		txtProjName = new Text(composite_1, SWT.BORDER);
		txtProjName.setEditable(false);
		txtProjName.setBounds(154, 35, 681, 22);
		
		txtProjName2 = new Text(composite_1, SWT.BORDER);
		txtProjName2.setEditable(false);
		txtProjName2.setBounds(154, 62, 681, 22);
		
		txtProjExtLink = new Text(composite_1, SWT.BORDER);
		txtProjExtLink.setEditable(false);
		txtProjExtLink.setBounds(154, 90, 681, 22);
		
		txtProjDesc = new Text(composite_1, SWT.BORDER | SWT.MULTI);
		txtProjDesc.setEditable(false);
		txtProjDesc.setBounds(841, 155, 317, 331);
		
		btnAddObj = new Button(composite_1, SWT.NONE);
		btnAddObj.setEnabled(false);
		btnAddObj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//change view to object and enable input fields
				clearObjTab();
				setObjEnabled();
				setObjBtnNew();
				tabMain.setSelection(2);
				btnEditObjProp.setText("Save Object Prop");
				isnew=true;
				editObjActive=true;
				
				//get next id
				txtObjID.setText(Integer.toString(getAutoIncrement()+1));
				//link with Project
		        txtObjLinkedProj.setText(txtProjID.getText());
				
			}
		});
		btnAddObj.setText("Add Object");
		btnAddObj.setBounds(842, 519, 146, 28);
		
		btnOpenPictureFolder = new Button(composite_1, SWT.NONE);
		btnOpenPictureFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File projDir = new File (txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")+"/Pictures");
				if(!projDir.exists()) return;
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(projDir);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnOpenPictureFolder.setEnabled(false);
		btnOpenPictureFolder.setText("Open Picture Folder");
		btnOpenPictureFolder.setBounds(993, 561, 165, 28);
		
		btnEditProjProp = new Button(composite_1, SWT.NONE);
		btnEditProjProp.setEnabled(false);
		btnEditProjProp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(editProjActive) {
					//perform save actions
					if(isnew){
						saveNewProject();
						isnew=false;
					}else {
						updateProject();
					}
					
					tableOverview.removeAll();
					readAllDBEntries();
					
					setProjDisabled();
					setProjBtnEnabled();
					btnEditProjProp.setText("Edit Project Prop");
					editProjActive=false;
					
					
				}else {
					setProjEnabled();
					editProjActive=true;
					txtProjName.setEditable(false);
					btnEditProjProp.setText("Save Properties");
				}
			}
		});
		btnEditProjProp.setText("Edit Project Prop");
		btnEditProjProp.setBounds(841, 561, 146, 28);
		
		txtProjID = new Text(composite_1, SWT.BORDER);
		txtProjID.setEditable(false);
		txtProjID.setBounds(154, 10, 97, 22);
		
		Label lblId = new Label(composite_1, SWT.NONE);
		lblId.setText("ID:");
		lblId.setBounds(10, 13, 123, 20);
		
		Button btnProjCancel = new Button(composite_1, SWT.NONE);
		btnProjCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearProjTab();
				setProjDisabled();
				editProjActive=false;
				isnew=false;
				btnEditProjProp.setText("Edit Project Prop");
				tableCAD.removeAll();
				tableExp.removeAll();
				tableDoc.removeAll();
				tabLinkedObj.setSelection(0);
				tabMain.setSelection(0);
			}
		});
		btnProjCancel.setText("Cancel Action");
		btnProjCancel.setBounds(841, 601, 146, 28);
		
		Button btnProjLinkCopy = new Button(composite_1, SWT.NONE);
		btnProjLinkCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StringSelection stringSelection = new StringSelection(txtProjExtLink.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}
		});
		btnProjLinkCopy.setText("Copy");
		btnProjLinkCopy.setBounds(841, 86, 110, 28);
		
		btnOpenProjFold = new Button(composite_1, SWT.NONE);
		btnOpenProjFold.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File projDir = new File (txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_"));
				if(!projDir.exists()) return;
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(projDir);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnOpenProjFold.setText("Open Project Folder");
		btnOpenProjFold.setEnabled(false);
		btnOpenProjFold.setBounds(994, 519, 165, 28);
		
		btnOpenSnapshots = new Button(composite_1, SWT.NONE);
		btnOpenSnapshots.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File dumpDir = new File (txtProjID.getText()+"_"+txtProjName.getText().replaceAll(" ", "_")+"/CAD/Snapshots");
				if(!dumpDir.exists()) return;
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(dumpDir);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnOpenSnapshots.setText("Open Snapshot Folder");
		btnOpenSnapshots.setEnabled(false);
		btnOpenSnapshots.setBounds(993, 601, 165, 28);
		
		Button btnProjURL = new Button(composite_1, SWT.NONE);
		btnProjURL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(txtProjExtLink.getText().equals("")) return;
				try {
					Desktop d=Desktop.getDesktop();
					d.browse(new URI(txtProjExtLink.getText()));
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnProjURL.setText("Open URL");
		btnProjURL.setBounds(957, 86, 110, 28);
		
		TabItem tbtmObject = new TabItem(tabMain, SWT.NONE);
		tbtmObject.setText("Object");
		
		Composite composite_2 = new Composite(tabMain, SWT.NONE);
		tbtmObject.setControl(composite_2);
		
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setBounds(10, 68, 98, 20);
		lblNewLabel.setText("Object Name:");
		
		Label lblObjectName = new Label(composite_2, SWT.NONE);
		lblObjectName.setText("Object Name 2:");
		lblObjectName.setBounds(10, 107, 110, 20);
		
		Label lblFilelinkOnHd = new Label(composite_2, SWT.NONE);
		lblFilelinkOnHd.setText("Filelink on HD:");
		lblFilelinkOnHd.setBounds(10, 142, 110, 20);
		
		Label lblExternalLink = new Label(composite_2, SWT.NONE);
		lblExternalLink.setText("External Link:");
		lblExternalLink.setBounds(10, 173, 98, 20);
		
		Label lblObjectDescription = new Label(composite_2, SWT.NONE);
		lblObjectDescription.setText("Object Description:");
		lblObjectDescription.setBounds(10, 214, 132, 20);
		
		Label lblObjectHistory = new Label(composite_2, SWT.NONE);
		lblObjectHistory.setText("Object History:");
		lblObjectHistory.setBounds(10, 433, 110, 28);
		
		txtObjName = new Text(composite_2, SWT.BORDER);
		txtObjName.setEditable(false);
		txtObjName.setBounds(158, 68, 526, 22);
		
		txtObjName2 = new Text(composite_2, SWT.BORDER);
		txtObjName2.setEditable(false);
		txtObjName2.setBounds(158, 104, 526, 22);
		
		txtObjLinkHDD = new Text(composite_2, SWT.BORDER);
		txtObjLinkHDD.setEditable(false);
		txtObjLinkHDD.setBounds(158, 139, 526, 22);
		
		txtObjExtLink = new Text(composite_2, SWT.BORDER);
		txtObjExtLink.setEditable(false);
		txtObjExtLink.setBounds(158, 174, 526, 22);
		
		txtObjDesc = new Text(composite_2, SWT.BORDER | SWT.MULTI);
		txtObjDesc.setEditable(false);
		txtObjDesc.setBounds(158, 211, 526, 200);
		
		txtObjHistory = new Text(composite_2, SWT.BORDER | SWT.MULTI);
		txtObjHistory.setEditable(false);
		txtObjHistory.setBounds(158, 430, 526, 200);
		
		btnCheckOut = new Button(composite_2, SWT.NONE);
		btnCheckOut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shlLightweightModelDatabase);
				dialog.setText("Please select the check out directory...");
			    String target_path =  dialog.open();
			    if(target_path==null) return;
			    fileCheckOut(target_path);
			}
		});
		btnCheckOut.setEnabled(false);
		btnCheckOut.setBounds(832, 138, 110, 28);
		btnCheckOut.setText("Check OUT");
		
		btnCheckIn = new Button(composite_2, SWT.NONE);
		btnCheckIn.setEnabled(false);
		btnCheckIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(isnew) {
					//Open File Dialog
					FileDialog dialog = new FileDialog(shlLightweightModelDatabase, SWT.SAVE);
					dialog.setText("Select the File you want to check-in...");
					String linkPath = dialog.open();
					if(!(linkPath==null)) txtObjLinkHDD.setText(linkPath);
				}else {
					//Open File Dialog
					FileDialog dialog = new FileDialog(shlLightweightModelDatabase, SWT.SAVE);
					dialog.setText("Select the File you want to check-in...");
					String checkInPath = dialog.open();
					if(checkInPath==null) return;
					MessageBox messageBox = new MessageBox(shlLightweightModelDatabase, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					messageBox.setMessage("Really want to check-in " + checkInPath + " to this Object?");
					messageBox.setText("File Check-in");
					int response = messageBox.open();
					if (response == SWT.NO)
						return;
					if(txtObjLinkHDD.getText().equals("")){
						txtObjLinkHDD.setText(checkInPath);
						fileCheckIn(checkInPath, "");
					} else fileCheckIn(checkInPath, txtObjLinkHDD.getText());

				}

			}
		});
		btnCheckIn.setText("Check IN");
		btnCheckIn.setBounds(716, 138, 110, 28);
		
		btnObjOpen = new Button(composite_2, SWT.NONE);
		btnObjOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Desktop.getDesktop().open(new File(txtObjLinkHDD.getText()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					msgBoxError("The Object File can't be opened. Maybe it's missing or renamed.", "Opening Object File");
				}
			}
		});
		btnObjOpen.setEnabled(false);
		btnObjOpen.setText("Open");
		btnObjOpen.setBounds(716, 433, 225, 28);
		
		btnObjDelete = new Button(composite_2, SWT.NONE);
		btnObjDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteObject(txtObjID.getText());
			}
		});
		btnObjDelete.setEnabled(false);
		btnObjDelete.setText("Delete");
		btnObjDelete.setBounds(716, 465, 225, 28);
		
		btnCreateSnapshot = new Button(composite_2, SWT.NONE);
		btnCreateSnapshot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!txtObjLinkHDD.getText().equals("")) createObjectSnapshot();
			}
		});
		btnCreateSnapshot.setEnabled(false);
		btnCreateSnapshot.setText("Create Snapshot");
		btnCreateSnapshot.setBounds(716, 499, 225, 28);
		
		btnEditObjProp = new Button(composite_2, SWT.NONE);
		btnEditObjProp.setEnabled(false);
		btnEditObjProp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(editObjActive) {
					//perform save actions
					if(isnew){
						saveNewObject();
						isnew=false;
					}else {
						updateObject();
					}

					setObjDisabled();
					btnEditObjProp.setText("Edit Object Prop");
					editObjActive=false;
					setObjBtnNormal();

				}else {
					setObjEnabled();
					editObjActive=true;
					btnEditObjProp.setText("Save Object Prop");
					setObjBtnNew();
				}
			}
		});
		btnEditObjProp.setText("Edit Object Prop");
		btnEditObjProp.setBounds(716, 562, 225, 28);
		
		Label label = new Label(composite_2, SWT.NONE);
		label.setText("ID:");
		label.setBounds(10, 27, 97, 20);
		
		txtObjID = new Text(composite_2, SWT.BORDER);
		txtObjID.setEditable(false);
		txtObjID.setBounds(158, 27, 97, 22);
		
		btnObjCancel = new Button(composite_2, SWT.NONE);
		btnObjCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearObjTab();
				setObjDisabled();
				editObjActive=false;
				isnew=false;
				btnEditObjProp.setText("Edit Object Prop");
				setObjBtnEmpty();
				tabMain.setSelection(0);
			}
		});
		btnObjCancel.setEnabled(false);
		btnObjCancel.setText("Cancel Action");
		btnObjCancel.setBounds(716, 596, 225, 28);
		
		Label lblLinkedWithProject = new Label(composite_2, SWT.NONE);
		lblLinkedWithProject.setText("Linked with Proj. ID:");
		lblLinkedWithProject.setBounds(280, 27, 146, 20);
		
		txtObjLinkedProj = new Text(composite_2, SWT.BORDER);
		txtObjLinkedProj.setEditable(false);
		txtObjLinkedProj.setBounds(432, 27, 97, 22);
		
		Label lblType = new Label(composite_2, SWT.NONE);
		lblType.setText("Type:");
		lblType.setBounds(549, 27, 45, 20);
		
		comboObjType = new Combo(composite_2, SWT.READ_ONLY);
		comboObjType.setItems(new String[] {"cad-3d", "cad-2d", "export", "doc"});
		comboObjType.setBounds(600, 24, 87, 28);
		comboObjType.select(0);
		
		Button btnObjCopy = new Button(composite_2, SWT.NONE);
		btnObjCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StringSelection stringSelection = new StringSelection(txtObjExtLink.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}
		});
		btnObjCopy.setText("Copy");
		btnObjCopy.setBounds(716, 172, 110, 28);
		
		Button btnObjURL = new Button(composite_2, SWT.NONE);
		btnObjURL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(txtObjExtLink.getText().equals("")) return;
				try {
					Desktop d=Desktop.getDesktop();
					d.browse(new URI(txtObjExtLink.getText()));
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnObjURL.setText("Open URL");
		btnObjURL.setBounds(832, 173, 110, 28);
		
		Menu menu = new Menu(shlLightweightModelDatabase, SWT.BAR);
		shlLightweightModelDatabase.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmRefresh = new MenuItem(menu_1, SWT.NONE);
		mntmRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tabMain.getSelectionIndex()==1){
					//Refresh Project Page
					if(!txtProjID.getText().equals("")){
						fetchProjectData(txtProjID.getText());
						readAllObjectEntries(txtProjID.getText());
					}
				}else if(tabMain.getSelectionIndex()==2){
					if(!txtObjID.getText().equals("")){
						fetchObjectData(txtObjID.getText(),false);
					}
				}
			}
		});
		mntmRefresh.setText("Refresh");
		
		MenuItem mntmQuitApplication = new MenuItem(menu_1, SWT.NONE);
		mntmQuitApplication.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int status=0;
				System.exit(status);
			}
		});
		mntmQuitApplication.setText("Quit Application");
		
		MenuItem mntmTools = new MenuItem(menu, SWT.CASCADE);
		mntmTools.setText("Tools");
		
		Menu menu_2 = new Menu(mntmTools);
		mntmTools.setMenu(menu_2);
		
		MenuItem mntmDirectIdSearch = new MenuItem(menu_2, SWT.NONE);
		mntmDirectIdSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectSearch directSearchDialog = new DirectSearch(shlLightweightModelDatabase,0);
				directSearchDialog.open();
				if(directSearchDialog.name==null) return;
				if(directSearchDialog.type.equals("project")){
					tabMain.setSelection(1);
					clearProjTab();
					setProjBtnEnabled();
					readAllObjectEntries(directSearchDialog.id);
					txtProjDesc.setText(directSearchDialog.descr);
					txtProjExtLink.setText(directSearchDialog.extlink);
					txtProjName.setText(directSearchDialog.name);
					txtProjName2.setText(directSearchDialog.name2);
					txtProjID.setText(directSearchDialog.id);
				}else{
					tabMain.setSelection(2);
					clearObjTab();
					setObjBtnNormal();
					txtObjDesc.setText(directSearchDialog.descr);
					txtObjHistory.setText(directSearchDialog.history);
					txtObjExtLink.setText(directSearchDialog.extlink);
					txtObjName.setText(directSearchDialog.name);
					txtObjName2.setText(directSearchDialog.name2);
					txtObjID.setText(directSearchDialog.id);
					txtObjLinkHDD.setText(directSearchDialog.flink);
					txtObjLinkedProj.setText(directSearchDialog.plink);
				}
			}
		});
		mntmDirectIdSearch.setText("Direct ID Search");
		
		MenuItem mntmDbSnapshot = new MenuItem(menu_2, SWT.NONE);
		mntmDbSnapshot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createDBSnapshot();
			}
		});
		mntmDbSnapshot.setText("DB Snapshot");
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_3 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_3);
		
		MenuItem mntmOpenWebHelp = new MenuItem(menu_3, SWT.NONE);
		mntmOpenWebHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
		            Desktop d=Desktop.getDesktop();
		            d.browse(new URI("http://www.encorecad.de/index.php/2017-03-14-20-16-33/lightweight-model-database-manual")); 
		        }
		        catch(Exception ex) {
		            ex.printStackTrace();
		        }
			}
		});
		mntmOpenWebHelp.setText("Open Web Help");
		
		MenuItem mntmAbout = new MenuItem(menu_3, SWT.NONE);
		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				About about_dialog = new About(shlLightweightModelDatabase, 0);
				about_dialog.open();
			}
		});
		mntmAbout.setText("About...");

	}
}
