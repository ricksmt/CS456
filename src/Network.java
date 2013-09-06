import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class Network {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 0) {
			NetworkModel.Test();
			System.out.println("Testing...PASSED");
			return;
		}

		JFrame F = new JFrame("Program 1"); // Create the frame with a title
		F.setBounds(100, 100, 300, 400);// Set the position and size of the  frame's window
		F.addWindowListener(
			new WindowAdapter() {// Setup quitting on close of window
				public void windowClosing(WindowEvent evt) { System.exit(0); }
			}
		);
		
		NetworkView view = new NetworkView(new NetworkModel(args[0]));
		F.getContentPane().add(view);// Add our component to the frame
		F.setVisible(true);
	}
}
