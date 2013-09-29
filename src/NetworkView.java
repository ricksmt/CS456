import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class NetworkView extends JPanel implements MouseListener {
	
	NetworkModel model;
	
	public NetworkView(NetworkModel model) {
		this.model = model;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final int fontHeight = 16;
        Font font = new Font("Helvetica", Font.PLAIN, fontHeight);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

		g.setColor(Color.BLACK);
		for(int i = 0; i < model.nNodes(); i++) {
			NetworkNode node = model.getNode(i);
			int width = fm.stringWidth(node.getName());
			g.drawOval((int)node.getX() - width, (int)node.getY() - fontHeight, width * 2, fontHeight * 2);
			g.drawString(node.getName(), (int)node.getX() - width / 2, (int)node.getY() + fontHeight / 2);
		}
		
		for(int i = 0; i < model.nConnections(); i++) {
			NetworkConnection connection = model.getConnection(i);
			NetworkNode node1 = null, node2 = null;
			for(int j = 0; j < model.nNodes(); j++) {
				if(node1 != null && node2 != null) break;
				NetworkNode node = model.getNode(j);
				if(node.getName().equals(connection.node1)) node1 = node;
				if(node.getName().equals(connection.node2)) node2 = node;
			}
			
			int width = fm.stringWidth(node1.getName());
			int x1 = (int)node1.getX();
			if(connection.side1 == NetworkConnection.Side.Right) x1 += width;
			else if(connection.side1 == NetworkConnection.Side.Left) x1 -= width;
			int y1 = (int)node1.getY();
			if(connection.side1 == NetworkConnection.Side.Top) y1 -= fontHeight;
			else if(connection.side1 == NetworkConnection.Side.Bottom) y1 += fontHeight;
			
			width = fm.stringWidth(node2.getName());
			int x2 = (int)node2.getX();
			if(connection.side2 == NetworkConnection.Side.Right) x2 += width;
			else if(connection.side2 == NetworkConnection.Side.Left) x2 -= width;
			int y2 = (int)node2.getY();
			if(connection.side2 == NetworkConnection.Side.Top) y2 -= fontHeight;
			else if(connection.side2 == NetworkConnection.Side.Bottom) y2 += fontHeight;
			
			g.drawLine(x1, y1, x2, y2);
		}
    }
	
	
	
	public GeometryDescriptor pointGeometry(Point mouseLoc) throws Exception
	{
		final int ERROR = 5;
		for(int i = model.nConnections() - 1; i >= 0; i--) {
			NetworkConnection c = model.getConnection(i);
			NetworkNode n1 = null, n2 = null;
			for(int j = 0; n1 == null || n2 == null; j++) {
				NetworkNode n = model.getNode(j);
				if(n.getName() == c.node1) n1 = n;
				else if(n.getName() == c.node2) n2 = n;
			}
			Point p1 = new Point(), p2 = new Point();
			p1.setLocation(n1.getX(), n1.getY());
			Font.g;
			switch(c.side1) {
				case Bottom: p1.setLocation(p1.getX(), p1.getY() - );// TODO: Finish
					break;
				case Left:
					break;
				case Right:
					break;
				case Top:
					break;
				default: throw new Exception();
			}
			p2.setLocation(n2.getX(), n2.getY());
			Point min = new Point(), max = new Point();
			min.setLocation(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
			max.setLocation(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()));
			if(mouseLoc.getX() < min.getX() - ERROR) continue;
			else if(mouseLoc.getX() > max.getX() + ERROR) continue;
			else if(mouseLoc.getY() < min.getY() - ERROR) continue;
			else if(mouseLoc.getY() > max.getY() + ERROR) continue;
			else if(Math.abs((p2.getY() - p1.getY()) * mouseLoc.getX() + (p1.getX() - p2.getX()) * mouseLoc.getY() -
					(p2.getY() - p1.getY()) * p1.getX() - (p1.getX() - p2.getX()) * p1.getY()) <= ERROR) {
				return new GeometryDescriptor(i);
			}
		}
		for(int i = model.nNodes() - 1; i >= 0; i--) {
			
		}
		return null;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println(pointGeometry(e.getPoint()));
	}

	@Override
	public void mouseClicked(MouseEvent arg0) { }

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent arg0) { }
}
