package com.lmdb.main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wb.swt.SWTResourceManager;

public class About extends Dialog {

	protected Object result;
	protected Shell shlAbout;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public About(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlAbout.open();
		shlAbout.layout();
		Display display = getParent().getDisplay();
		while (!shlAbout.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlAbout = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shlAbout.setImage(SWTResourceManager.getImage(About.class, "/com/lmdb/main/cube_grey.png"));
		shlAbout.setMinimumSize(new Point(70, 25));
		shlAbout.setSize(859, 613);
		shlAbout.setText("About this App...");
		shlAbout.setBackgroundMode(SWT.INHERIT_DEFAULT);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		shlAbout.setLocation(dim.width/2-620/2, dim.height/2-568/2);
		
		
		Button btnClose = new Button(shlAbout, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlAbout.close();
			}
		});
		btnClose.setText("Close");
		btnClose.setBounds(272, 538, 295, 30);
		formToolkit.adapt(btnClose, true, true);
		
		Label lblEnterProjectobjectId = new Label(shlAbout, SWT.WRAP);
		lblEnterProjectobjectId.setBounds(153, 10, 700, 501);
		lblEnterProjectobjectId.setText("This Application was developed by Julian Schweigert, 2017.\r\n\r\nIt is published under the GNU General Public License (GPL).\r\n-> https://www.gnu.org/licenses/gpl-3.0.de.html\r\n\r\nThe following third party libraries were used and included into this .jar:\r\n- Apache Commons IO 2.5: https://commons.apache.org/proper/commons-io/\r\n- SQLite JDBC Driver: https://github.com/xerial/sqlite-jdbc\r\n- STLViewer: Copyright (c) 2017 Andrew GohTHE  , http://sourceforge.net/projects/stl-viewer4j\r\n\nLibraries used in STLViewer:\nThis app the java3d and jogl implementations maintained by the jogamp community and partly are derived works, in particular of java3d from sun/oracle.\r\n\nOthers:\nhttp://jogamp.org/\nhttps://gouessej.wordpress.com/2012/08/01/java-3d-est-de-retour-java-3d-is-back/\nhttps://github.com/hharrison/java3d-core\nhttps://github.com/hharrison/java3d-utils\nhttps://github.com/hharrison/vecmath\nhttp://jogamp.org/deployment/v2.3.2/fat/jogamp-fat.jar\nSTL parser by cyanobacterium: https://github.com/cyanobacterium/STL-Parser-for-Java\nPartially derived from cpedrinaci/STL-Loader: https://github.com/cpedrinaci/STL-Loader");
		
		Label label = new Label(shlAbout, SWT.NONE);
		label.setImage(SWTResourceManager.getImage(About.class, "/com/lmdb/main/cube_grey.png"));
		label.setBounds(10, 10, 137, 160);



	}
}
