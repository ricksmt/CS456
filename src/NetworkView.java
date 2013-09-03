import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class NetworkView extends JPanel {
	
	NetworkModel model;
	
	public NetworkView(NetworkModel model) {
		this.model = model;
	}
	
	public void paint(Graphics g) {
        Font font = new Font("Helvetica", Font.PLAIN, 15);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        
		double maxHeight = 0;
		double maxWidth = 0;
		for(int i = 0; i < model.nNodes(); i++) {
			maxHeight = Math.max(maxHeight, fm.stringWidth(model.getNode(i).getName()));
			maxWidth = Math.max(maxWidth, fm.stringWidth(model.getNode(i).getName()));
		}

		for(int i = 0; i < model.nNodes(); i++) {
			NetworkNode node = model.getNode(i);
			g.setColor(Color.GRAY);
			g.drawOval((int)node.getX(), (int)node.getY(), (int)maxWidth, (int)maxHeight);
			g.setColor(Color.BLACK);
			g.drawString(node.getName(), (int)node.getX(), (int)node.getY());
		}
		
		g.setColor(Color.BLACK);
		for(int i = 0; i < model.nConnections(); i++) {
			NetworkConnection connection = model.getConnection(i);
			NetworkNode node1 = null, node2 = null;
			for(int j = 0; j < model.nNodes(); j++) {
				if(node1 != null && node2 != null) break;
				NetworkNode node = model.getNode(j);
				if(node.getName().equals(connection.node1)) node1 = node;
				if(node.getName().equals(connection.node2)) node2 = node;
			}
			double x1 = node1.getX();
			if(connection.side1 == NetworkConnection.Side.Right) x1 += maxWidth;
			else if(connection.side1 != NetworkConnection.Side.Left) x1 += maxWidth / 2;
			double y1 = node1.getY();
			if(connection.side1 == NetworkConnection.Side.Top) y1 += maxHeight;
			else if(connection.side1 != NetworkConnection.Side.Bottom) y1 += maxHeight / 2;
			double x2 = node2.getX();
			if(connection.side2 == NetworkConnection.Side.Right) x2 += maxWidth;
			else if(connection.side2 != NetworkConnection.Side.Left) x2 += maxWidth / 2;
			double y2 = node2.getY();
			if(connection.side2 == NetworkConnection.Side.Top) y2 += maxHeight;
			else if(connection.side2 != NetworkConnection.Side.Bottom) y2 += maxHeight / 2;
			
			g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		}
    }
}
