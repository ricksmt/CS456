import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;


public class Network {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 0) {
			NetworkModel.Test();
			System.out.println("Testing...PASSED");
			return;
		}

		JFrame F = new JFrame("Program"); // Create the frame with a title
		F.setBounds(100, 100, 300, 400);// Set the position and size of the  frame's window
		F.addWindowListener(
			new WindowAdapter() {// Setup quitting on close of window
				public void windowClosing(WindowEvent evt) {
					JFrame frame = (JFrame)evt.getSource();
					Component[] components = frame.getContentPane().getComponents();
					for(int i = 0; i < components.length; i++) {
						Component component = components[i];
						if(component instanceof NetworkView) {
							NetworkView view = (NetworkView)component;
							NetworkModel model = view.getNetworkModel();
							if(model.countObservers() == 1 && model.unsavedChanges()) {
								try { model.save(); }
								catch (IOException e) { }// TODO
							}
							break;
						}
					}
					
					int count = 0;
					Frame[] frames = JFrame.getFrames();
					for(int i = 0; i < frames.length; i++) {
						if(frames[i] instanceof JFrame) {
							JFrame f = (JFrame)frames[i];
							if(f.isVisible()) count++;
						}
					}
					if(count == 1) System.exit(0);
				}
			}
		);
		
		File file = new File(args[0]);
		NetworkView view;
		try {
			view = new NetworkView(new NetworkModel(file.getCanonicalPath()));
		} catch (IOException e) {
			view = new NetworkView(new NetworkModel(file.getAbsolutePath()));
		}
		F.setTitle(file.getName());
		F.getContentPane().add(view);// Add our component to the frame
		F.setVisible(true);
	}
}
