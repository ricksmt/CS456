import java.awt.Component;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;


public class NetworkFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8591374533222590896L;
	private JFileChooser fileChooser = new JFileChooser(".");
	protected NetworkView view = null;

	public NetworkFrame(String filename) {
		super();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("", "txt"));
		
		MenuBar menuBar = new MenuBar();
		
		// File
		Menu file = new Menu("File");
		
		MenuItem open = new MenuItem("Open...");
		open.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    		try {
						new NetworkFrame(fileChooser.getSelectedFile().getCanonicalPath());
					}
		    		catch (IOException e1) {
						new NetworkFrame(fileChooser.getSelectedFile().getAbsolutePath());
					}
		        }
		    }
		});
		file.add(open);
		
		MenuItem save = new MenuItem("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try { view.getNetworkModel().save(); }
				catch (IOException e1) { }// TODO
			}
		});
		file.add(save);
		
		MenuItem saveAs = new MenuItem("Save As...");
		saveAs.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
		        	NetworkModel model = new NetworkModel(view.getNetworkModel());
		        	try {
						model.setFileName(fileChooser.getSelectedFile().getCanonicalPath());
					}
		        	catch (IOException e1) {
						model.setFileName(fileChooser.getSelectedFile().getAbsolutePath());
					}
		        	try { model.save(); }
		        	catch (IOException e1) { }// TODO
		        	view.setNetworkModel(model);
		        }
		    }
		});
		file.add(saveAs);
		
		menuBar.add(file);
		
		// Edit
		Menu edit = new Menu("Edit");
		
		MenuItem undo = new MenuItem("Undo");
		undo.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	CommandObj.Undo();
		    	MenuItem item = (MenuItem)e.getSource();
		    	Menu menu = (Menu)item.getParent();
		    	MenuBar bar = (MenuBar)menu.getParent();
		    	NetworkFrame frame = (NetworkFrame)bar.getParent();
		    	frame.Refresh();
		    }
		});
		undo.setEnabled(false);// Initially nothing to undo
		edit.add(undo);
		
		MenuItem redo = new MenuItem("Redo");
		redo.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	CommandObj.Redo();
		    	MenuItem item = (MenuItem)e.getSource();
		    	Menu menu = (Menu)item.getParent();
		    	MenuBar bar = (MenuBar)menu.getParent();
		    	NetworkFrame frame = (NetworkFrame)bar.getParent();
		    	frame.Refresh();
		    }
		});
		redo.setEnabled(false);// Initially nothing to redo
		edit.add(redo);
		
		menuBar.add(edit);
		
		// Preparation
		this.setMenuBar(menuBar);
		this.setBounds(100, 100, 600, 400);// Set the position and size of the  frame's window
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {// Setup quitting on close of window
			public void windowClosing(WindowEvent evt) {
				JFrame frame = (JFrame)evt.getSource();
				Component[] components = frame.getContentPane().getComponents();
				for(int i = 0; i < components.length; i++) {
					Component component = components[i];
					if(component instanceof NetworkView) {
						NetworkView view = (NetworkView)component;
						NetworkModel model = view.getNetworkModel();
						if(model.countObservers() == 1 && model.unsavedChanges()) {
							String[] options = new String[] {"Yes", "No", "Cancel"};
							switch (JOptionPane.showOptionDialog(frame, "Would you like to save your changes?", "Save?",
										JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
										null, options, options[2])) {
								case JOptionPane.YES_OPTION:
									try { model.save(); }
									catch (IOException e) { }// TODO
								case JOptionPane.NO_OPTION: break;
								default: return;
							}
						}
						break;
					}
				}
				
				int count = 0;
				Frame[] frames = JFrame.getFrames();
				for(int i = 0; i < frames.length; i++) {
					if(frames[i] instanceof NetworkFrame) {
						NetworkFrame f = (NetworkFrame)frames[i];
						if(f.isVisible()) count++;
					}
				}
				if(count == 1) System.exit(0);
				else frame.dispose();
			}
		});

		File f = new File(filename);
		Frame[] frames = JFrame.getFrames();
	    for (int i = 0; i < frames.length; i++) {
	    	if(!(frames[i] instanceof JFrame)) continue;
	    	JFrame frame = (JFrame)frames[i];
	    	Component[] components = frame.getContentPane().getComponents();
	    	for(int j = 0; j < components.length; j++) {
	    		if(components[j] instanceof NetworkView) {
	    			NetworkView nv = (NetworkView)components[j];
	    			try {
						if(nv.model.getFileName().equals(f.getCanonicalPath())) {
							view = new NetworkView(nv.model);
						}
					}
	    			catch (IOException e1) {
						if(nv.model.getFileName().equals(f.getAbsolutePath())) {
							view = new NetworkView(nv.model);
						}
					}
	    		}
	    	}
	    	if(view != null) break;
	    }
	    if(view == null) {
			try {
				view = new NetworkView(new NetworkModel(f.getCanonicalPath()));
			}
			catch (IOException e1) {
				view = new NetworkView(new NetworkModel(f.getAbsolutePath()));
			}
	    }
		this.setTitle(f.getName());
		this.getContentPane().add(view);// Add our component to the frame
		this.setVisible(true);
	}
	
	public void Refresh() {
		MenuBar bar = getMenuBar();
		for(int i = 0; i < getMenuBar().getMenuCount(); i++) {
			Menu menu = bar.getMenu(i);
			if(menu.getLabel().equalsIgnoreCase("Edit")) {
				for(int j = 0; j < menu.getItemCount(); j++) {
					MenuItem item = menu.getItem(j);
					if(item.getLabel().equalsIgnoreCase("Undo")) {
						item.setEnabled(CommandObj.canUndo());
					}
					else if(item.getLabel().equalsIgnoreCase("Redo")) {
						item.setEnabled(CommandObj.canRedo());
					}
				}
				break;
			}
		}
	}
}
