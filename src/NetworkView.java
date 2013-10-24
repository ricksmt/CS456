import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class NetworkView extends JPanel implements KeyListener, MouseListener, MouseMotionListener, Observer {

	static final int fontHeight = 16;
	
	NetworkModel model;
	GeometryDescriptor descriptor = new GeometryDescriptor();
	MouseEvent lastEvent = null;
	
	public NetworkView(NetworkModel model) {
		this.model = model;
		setFont(new Font("Helvetica", Font.PLAIN, fontHeight));

		this.model.addObserver(this);
		this.setFocusable(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
        FontMetrics fm = g.getFontMetrics();
		for(int i = 0; i < model.nNodes(); i++) {
			NetworkNode node = model.getNode(i);
			int width = fm.stringWidth(node.getName());
			g.drawOval((int)node.getX() - width, (int)node.getY() - fontHeight, width * 2, fontHeight * 2);
			if(descriptor == null);
			else if (node.equals(descriptor.object)) {
				g.fill3DRect((int)node.getX() - width - 2, (int)node.getY() - 2, 4, 4, false);
				g.fill3DRect((int)node.getX() + width - 2, (int)node.getY() - 2, 4, 4, false);
				g.fill3DRect((int)node.getX() - 2, (int)node.getY() - fontHeight - 2, 4, 4, false);
				g.fill3DRect((int)node.getX() - 2, (int)node.getY() + fontHeight - 2, 4, 4, false);
			}
			else if(node.getName().equals(descriptor.object)) {
				int cursorX = fm.stringWidth(node.getName().substring(0, descriptor.index));
				g.drawLine((int)node.getX() - width / 2 + cursorX, (int)node.getY() + fontHeight / 2,
						(int)node.getX() - width / 2 + cursorX, (int)node.getY() - fontHeight / 2);
				g.drawString(node.getName(), (int)node.getX() - width / 2, (int)node.getY() + fontHeight / 2);
			}
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
			if(descriptor != null && connection.equals(descriptor.object)) {
				g.fill3DRect(x1 - 2, y1 - 2, 4, 4, false);
				g.fill3DRect(x2 - 2, y2 - 2, 4, 4, false);
			}
		}
		
		if (descriptor != null && !descriptor.equals(GeometryDescriptor.NULL_DESCRIPTOR)) g.drawString(descriptor.toString(), 5, 20);
    }
	
	public GeometryDescriptor pointGeometry(Point mouseLoc) {
		final int ERROR = 5;
		for(int i = model.nConnections() - 1; i >= 0; i--) {
			// Get pieces
			NetworkConnection c = model.getConnection(i);
			NetworkNode n1 = null, n2 = null;
			for(int j = 0; (n1 == null || n2 == null) && j < model.nNodes(); j++) {
				NetworkNode n = model.getNode(j);
				if(n.getName().equals(c.node1)) n1 = n;
				else if(n.getName().equals(c.node2)) n2 = n;
			}
			if (n1 == null || n2 == null) continue;
			
			// Get corners
			FontMetrics fm = getFontMetrics(getFont());
			Point p1 = new Point(), p2 = new Point();
			p1.setLocation(n1.getX(), n1.getY());
			switch(c.side1) {
				case Bottom: p1.setLocation(p1.getX(), p1.getY() + fm.getHeight()); break;
				case Left: p1.setLocation(p1.getX() - fm.stringWidth(n1.getName()), p1.getY()); break;
				case Right: p1.setLocation(p1.getX() + fm.stringWidth(n1.getName()), p1.getY()); break;
				case Top: p1.setLocation(p1.getX(), p1.getY() - fm.getHeight()); break;
			}
			p2.setLocation(n2.getX(), n2.getY());
			switch(c.side2) {
				case Bottom: p2.setLocation(p2.getX(), p2.getY() + fm.getHeight()); break;
				case Left: p2.setLocation(p2.getX() - fm.stringWidth(n2.getName()), p2.getY()); break;
				case Right: p2.setLocation(p2.getX() + fm.stringWidth(n2.getName()), p2.getY()); break;
				case Top: p2.setLocation(p2.getX(), p2.getY() - fm.getHeight()); break;
			}
			
			// Get box
			Point min = new Point(), max = new Point();
			min.setLocation(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
			max.setLocation(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()));
			if(mouseLoc.getX() < min.getX() - ERROR) continue;
			else if(mouseLoc.getX() > max.getX() + ERROR) continue;
			else if(mouseLoc.getY() < min.getY() - ERROR) continue;
			else if(mouseLoc.getY() > max.getY() + ERROR) continue;
			else if(Math.abs(((p2.getY() - p1.getY()) * mouseLoc.getX() + (p1.getX() - p2.getX()) * mouseLoc.getY() + p2.getX() * p1.getY() - p1.getX() * p2.getY()) /
					(Math.pow(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2), 0.5))) <= ERROR) {
				return new GeometryDescriptor(c, i);
			}
		}
		for(int i = model.nNodes() - 1; i >= 0; i--) {
			NetworkNode n = model.getNode(i);

			// Get outer box
			FontMetrics fm = getFontMetrics(getFont());
			Point min = new Point(), max = new Point();
			min.setLocation(n.getX() - fm.stringWidth(n.getName()), n.getY() - fm.getHeight());
			max.setLocation(n.getX() + fm.stringWidth(n.getName()), n.getY() + fm.getHeight());
			
			if(Math.pow((mouseLoc.getX() - n.getX()) / fm.stringWidth(n.getName()), 2) + 
					Math.pow((mouseLoc.getY() - n.getY()) / fm.getHeight(), 2) <= 1) {
				
				// Bounds check
				if(mouseLoc.getX() < min.getX()) continue;
				else if(mouseLoc.getX() > max.getX()) continue;
				else if(mouseLoc.getY() < min.getY()) continue;
				else if(mouseLoc.getY() > max.getY()) continue;
				
				// Get inner box
				min.setLocation(n.getX() - fm.stringWidth(n.getName()) / 2, n.getY() - fm.getHeight() / 2);
				max.setLocation(n.getX() + fm.stringWidth(n.getName()) / 2, n.getY() + fm.getHeight() / 2);
				if(mouseLoc.getX() > min.getX() && mouseLoc.getX() < max.getX() &&
						mouseLoc.getY() > min.getY() && mouseLoc.getY() < max.getY()) {
					for(int j = 0, size = fm.stringWidth(n.getName().substring(0, 1)); j < n.getName().length();
							size = fm.stringWidth(n.getName().substring(0, ++j + 1))) {
						if (min.getX() + size > mouseLoc.getX()) return new GeometryDescriptor(n.getName(), j);
					}
				}
				else return new GeometryDescriptor(n, i);
			}
		}
		return new GeometryDescriptor();
	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) {
		lastEvent = e;
		GeometryDescriptor gd = pointGeometry(e.getPoint());
		if(gd.equals(GeometryDescriptor.NULL_DESCRIPTOR)) return;
		else if(gd.equals(descriptor)) return;
		else descriptor = gd;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		final int THRESHOLD = 3;
		
		if(descriptor.equals(GeometryDescriptor.NULL_DESCRIPTOR)) return;
		else if(descriptor.object instanceof NetworkConnection) return;
		else if(descriptor.object instanceof String) {
			for(int i = 0; i < model.nNodes(); i++) {
				NetworkNode node = model.getNode(i);
				if(node.getName().equals(descriptor.object)) {
					descriptor = new GeometryDescriptor(node, i);
					repaint();
					break;
				}
			}
		}
		
		if(e.getPoint().distance(lastEvent.getPoint()) > THRESHOLD) {
			double deltaX = e.getPoint().getX() - lastEvent.getPoint().getX();
			double deltaY = e.getPoint().getY() - lastEvent.getPoint().getY();
			lastEvent = e;
			NetworkNode node = (NetworkNode)descriptor.object;
			node.setLocation(node.getX() + deltaX, node.getY() + deltaY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) { }
	

	@Override
	public void keyTyped(KeyEvent e) { }
	

	@Override
	public void keyPressed(KeyEvent e) { }
	

	@Override
	public void keyReleased(KeyEvent e) {
		if(descriptor == null) return;
		else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			descriptor = null;
			repaint();
		}
		else if(descriptor.object instanceof String) {
			NetworkNode node = null;
			for(int i = 0; i < model.nNodes(); i++) {
				NetworkNode temp = model.getNode(i);
				if(temp.getName().equals(descriptor.object)) {
					node = temp;
					break;
				}
			}
			if(node == null) descriptor = null;
			else {
				String name = node.getName();
				switch(e.getKeyCode()) {
					case KeyEvent.VK_BACK_SPACE:
						if(descriptor.index <= 0) break;
						String backspace = name.substring(0, descriptor.index - 1) + name.substring(descriptor.index);
						descriptor.object = backspace;
						descriptor.index--;
						node.setName(backspace);
						break;
					case KeyEvent.VK_DELETE:
						if(descriptor.index >= name.length()) break;
						String delete = name.substring(0, descriptor.index) + name.substring(descriptor.index + 1);
						descriptor.object = delete;
						node.setName(delete);
						break;
					case KeyEvent.VK_LEFT:
						if(descriptor.index > 0) {
							descriptor.index--;
							repaint();
						}
						break;
					case KeyEvent.VK_RIGHT:
						if(descriptor.index < name.length()) {
							descriptor.index++;
							repaint();
						}
						break;
					default:
						if(e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) break;
						String result = name.substring(0, descriptor.index) + e.getKeyChar() + name.substring(descriptor.index);
						descriptor.object = result;
						descriptor.index++;
						node.setName(result);
						break;
				}
			}
		}
	}
}
