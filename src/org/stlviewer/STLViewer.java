package org.stlviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.sun.j3d.utils.universe.SimpleUniverse;

import hall.collin.christopher.stl4j.STLParser;
import hall.collin.christopher.stl4j.Triangle;

public class STLViewer extends JDialog implements ActionListener, WindowListener {
	
	PCanvas3D canvas;
	JLabel lstatusline;
	PModel model;
	SimpleUniverse universe;
	public Boolean windowOpen=false;
	
	JCheckBoxMenuItem mnstrp;
	
	public STLViewer(Frame parent) throws HeadlessException {

		createwin();
		addWindowListener(this);
	}

	public void createwin() {

		
		setPreferredSize(new Dimension(1024, 768));
		
		JMenuBar mbar = new JMenuBar();
		JMenu mview = new JMenu("View");
		JMenuItem mres = new JMenuItem("Home", KeyEvent.VK_H);
		mres.setActionCommand("VHOME");
		mres.addActionListener(this);
		mview.add(mres);
		mbar.add(mview);
		JMenu mtools = new JMenu("Tools");
		mnstrp = new JCheckBoxMenuItem("Regen Normals/Connect strips",true);
		mnstrp.addActionListener(this);
		mtools.add(mnstrp);
		mbar.add(mtools);
		
		setJMenuBar(mbar);

		getContentPane().setLayout(new BorderLayout());
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new PCanvas3D(config);
		getContentPane().add(canvas, BorderLayout.CENTER);		
		
		lstatusline = new JLabel(" ");
		getContentPane().add(lstatusline, BorderLayout.SOUTH);
		
		universe = new SimpleUniverse(canvas);
		canvas.initcanvas(universe);
						
		pack();			
		setLocationRelativeTo(null);
		setVisible(true);
		toFront();
		setModal(true);
		setAlwaysOnTop(true);
		repaint();
				
	}
	
	private File currdir;
	
	private File askForFile(){
		
		JFileChooser jfc = new JFileChooser(currdir);
		int action = jfc.showOpenDialog(null);
		if(action != JFileChooser.APPROVE_OPTION){
			return null;
		}
		File file = jfc.getSelectedFile();
		if (file.getParentFile() != null) currdir = file.getParentFile();		
		return file;
	}
	
	public void loadExtFile(File file){
		if(file == null) return;
		
		// read file to array of triangles
		try {
			
			List<Triangle> mesh = new STLParser().parseSTLFile(file.toPath());			
			
			if (mesh == null || mesh.isEmpty()) {
				lstatusline.setText("no data read, possible file error");
				return;
			} else
				lstatusline.setText(" ");
			
			if(model != null)
				model.cleanup();			
			model = new PModel();
			model.setBnormstrip(mnstrp.isSelected());
			model.addtriangles(mesh);
			
			canvas.rendermodel(model, universe);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			lstatusline.setText("no data read, possible file error");
			Logger.getLogger(STLViewer.class.getName()).log(Level.WARNING, e.getMessage());
		}
	}
	
	private void loadfile() {
		File file = askForFile();
		if(file == null) return;
		
		// read file to array of triangles
		try {
			
			List<Triangle> mesh = new STLParser().parseSTLFile(file.toPath());			
			
			if (mesh == null || mesh.isEmpty()) {
				lstatusline.setText("no data read, possible file error");
				return;
			} else
				lstatusline.setText(" ");
			
			if(model != null)
				model.cleanup();			
			model = new PModel();
			model.setBnormstrip(mnstrp.isSelected());
			model.addtriangles(mesh);
			
			canvas.rendermodel(model, universe);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			lstatusline.setText("no data read, possible file error");
			Logger.getLogger(STLViewer.class.getName()).log(Level.WARNING, e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("FOPEN")) {
			loadfile();			
		} else if(e.getActionCommand().equals("VHOME")) {
			canvas.homeview(universe);
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		universe.removeAllLocales();
		universe.cleanup();
		windowOpen=false;
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

}
