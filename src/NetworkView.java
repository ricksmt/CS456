import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class NetworkView extends JPanel implements KeyListener, MouseListener, MouseMotionListener, Observer, ChangeListener {

	static final int fontHeight = 16;
	
	NetworkModel model = null;
	GeometryDescriptor descriptor = new GeometryDescriptor();
	MouseEvent lastEvent = null;
	boolean drawing = false;
	
	enum State {
		SELECT,
		NODE,
		CONNECTION,
	}
	State state = State.SELECT;
	
	public NetworkView(NetworkModel model) {
		setFont(new Font("Helvetica", Font.PLAIN, fontHeight));

		setNetworkModel(model);
		this.setFocusable(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setLayout(new BorderLayout());

		JToolBar toolbar = new JToolBar();
		toolbar.setOrientation(JToolBar.VERTICAL);
		ButtonGroup group = new ButtonGroup();
		JToggleButton select = new JToggleButton(UIManager.getIcon("OptionPane.questionIcon"));
		select.setName("Select");
		select.setToolTipText("Select");
		select.addChangeListener(this);
		select.doClick();
		group.add(select);
		toolbar.add(select);
		JToggleButton node = new JToggleButton(UIManager.getIcon("OptionPane.warningIcon"));
		node.setName("Node");
		node.setToolTipText("Node");
		node.addChangeListener(this);
		group.add(node);
		toolbar.add(node);
		JToggleButton connection = new JToggleButton(UIManager.getIcon("OptionPane.errorIcon"));
		connection.setName("Connection");
		connection.setToolTipText("Connection");
		connection.addChangeListener(this);
		group.add(connection);
		toolbar.add(connection);
		this.add(toolbar, BorderLayout.WEST);
	}
	
	public NetworkModel getNetworkModel() { return this.model; }

	public void setNetworkModel(NetworkModel model) {
		if(this.model != null) this.model.deleteObserver(this);
		this.model = model;
		this.model.addObserver(this);
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
			else if(descriptor.object instanceof String && node.equals(descriptor.additional.get("Node"))) {
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
			if(connection.side1 == NetworkConnection.Side.RIGHT) x1 += width;
			else if(connection.side1 == NetworkConnection.Side.LEFT) x1 -= width;
			int y1 = (int)node1.getY();
			if(connection.side1 == NetworkConnection.Side.TOP) y1 -= fontHeight;
			else if(connection.side1 == NetworkConnection.Side.BOTTOM) y1 += fontHeight;
			
			width = fm.stringWidth(node2.getName());
			int x2 = (int)node2.getX();
			if(connection.side2 == NetworkConnection.Side.RIGHT) x2 += width;
			else if(connection.side2 == NetworkConnection.Side.LEFT) x2 -= width;
			int y2 = (int)node2.getY();
			if(connection.side2 == NetworkConnection.Side.TOP) y2 -= fontHeight;
			else if(connection.side2 == NetworkConnection.Side.BOTTOM) y2 += fontHeight;
			
			CubicCurve2D.Double curve = new CubicCurve2D.Double(
					x1, y1, x1 + (x1 - node1.getX()), y1 + (y1 - node1.getY()),
					x2 + (x2 - node2.getX()), y2 + (y2 - node2.getY()), x2, y2);
			((Graphics2D)g).draw(curve);
			if(descriptor != null && connection.equals(descriptor.object)) {
				g.fill3DRect(x1 - 2, y1 - 2, 4, 4, false);
				g.fill3DRect(x2 - 2, y2 - 2, 4, 4, false);
			}
		}
		
		if (descriptor != null && !descriptor.equals(GeometryDescriptor.NULL_DESCRIPTOR)) {
			g.drawString(descriptor.toString(), 50, 20);
			if(state == State.CONNECTION) {
				NetworkNode n = (NetworkNode)descriptor.object;
				int width = fm.stringWidth(n.getName());
				g.setColor(Color.RED);
				Point p = new Point();
				switch((NetworkConnection.Side)descriptor.additional.get("Side")) {
					case TOP: p.setLocation(n.getX(), n.getY() - fontHeight); break;
					case RIGHT: p.setLocation(n.getX() + width, n.getY()); break;
					case BOTTOM: p.setLocation(n.getX(), n.getY() + fontHeight); break;
					case LEFT: p.setLocation(n.getX() - width, n.getY()); break;
				}
				g.fill3DRect((int)p.getX() - 2, (int)p.getY() - 2, 4, 4, false);
				g.setColor(Color.BLACK);
				if(drawing) {
					CubicCurve2D.Double curve = new CubicCurve2D.Double(
							p.getX(), p.getY(), p.getX() + (p.getX() - n.getX()), p.getY() + (p.getY() - n.getY()),
							lastEvent.getX() + lastEvent.getX(), lastEvent.getY() + lastEvent.getY(),
							lastEvent.getX(), lastEvent.getY());
					((Graphics2D)g).draw(curve);
					if(descriptor.additional.containsKey("Target")) {
						GeometryDescriptor target = (GeometryDescriptor)descriptor.additional.get("Target");
						NetworkNode nT = (NetworkNode)target.object;
						int widthT = fm.stringWidth(n.getName());
						g.setColor(Color.RED);
						Point pT = new Point();
						switch((NetworkConnection.Side)target.additional.get("Side")) {
							case TOP: pT.setLocation(nT.getX(), nT.getY() - fontHeight); break;
							case RIGHT: pT.setLocation(nT.getX() + widthT, nT.getY()); break;
							case BOTTOM: pT.setLocation(nT.getX(), nT.getY() + fontHeight); break;
							case LEFT: pT.setLocation(nT.getX() - widthT, nT.getY()); break;
						}
						g.fill3DRect((int)pT.getX() - 2, (int)pT.getY() - 2, 4, 4, false);
						g.setColor(Color.BLACK);
					}
				}
			};
		}
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
				case BOTTOM: p1.setLocation(p1.getX(), p1.getY() + fm.getHeight()); break;
				case LEFT: p1.setLocation(p1.getX() - fm.stringWidth(n1.getName()), p1.getY()); break;
				case RIGHT: p1.setLocation(p1.getX() + fm.stringWidth(n1.getName()), p1.getY()); break;
				case TOP: p1.setLocation(p1.getX(), p1.getY() - fm.getHeight()); break;
			}
			p2.setLocation(n2.getX(), n2.getY());
			switch(c.side2) {
				case BOTTOM: p2.setLocation(p2.getX(), p2.getY() + fm.getHeight()); break;
				case LEFT: p2.setLocation(p2.getX() - fm.stringWidth(n2.getName()), p2.getY()); break;
				case RIGHT: p2.setLocation(p2.getX() + fm.stringWidth(n2.getName()), p2.getY()); break;
				case TOP: p2.setLocation(p2.getX(), p2.getY() - fm.getHeight()); break;
			}
			
			CubicCurve2D.Double curve = new CubicCurve2D.Double(
					p1.getX(), p1.getY(), p1.getX() + (p1.getX() - n1.getX()), p1.getY() + (p1.getY() - n1.getY()),
					p2.getX() + (p2.getX() - n2.getX()), p2.getY() + (p2.getY() - n2.getY()), p2.getX(), p2.getY());
			
			// Bounds check
			Point current = new Point();
			for(PathIterator iterator = curve.getPathIterator(null, 0); !iterator.isDone(); iterator.next()) {
				double[] coords = new double[6];
				switch(iterator.currentSegment(coords)) {
					case PathIterator.SEG_MOVETO:
						current.setLocation(coords[0], coords[1]);
						break;
					case PathIterator.SEG_LINETO:
						Point next = new Point();
						next.setLocation(coords[0], coords[1]);
						Line2D line = new Line2D.Double(current, next);
						if(line.ptSegDist(mouseLoc) < ERROR) return new GeometryDescriptor(c, i);
						current.setLocation(next);
						break;
				}
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
						if (min.getX() + size > mouseLoc.getX()) {
							Map<String, Object> map = new TreeMap<String, Object>();
							map.put("Node", n);
							return new GeometryDescriptor(n.getName(), j, map);
						}
					}
				}
				else return new GeometryDescriptor(n, i);
			}
		}
		return new GeometryDescriptor();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof NetworkModel) {
			repaint();
			if(arg instanceof String) {
				String s = (String)arg;
				JFrame frame = (JFrame)this.getParent().getParent().getParent().getParent();
				frame.setTitle(s);
			}
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void mouseReleased(MouseEvent e) {
		switch(state) {
			case CONNECTION:
				drawing = false;
				lastEvent = null;
				GeometryDescriptor start = descriptor;
				mouseMoved(e);
				if(!descriptor.equals(GeometryDescriptor.NULL_DESCRIPTOR)) {
					if(!start.object.equals(descriptor.object)) {
						getNetworkModel().addConnection(new NetworkConnection(
								((NetworkNode)start.object).getName(),
								(NetworkConnection.Side)start.additional.get("Side"),
								((NetworkNode)descriptor.object).getName(),
								(NetworkConnection.Side)descriptor.additional.get("Side")));
					}
					descriptor = GeometryDescriptor.NULL_DESCRIPTOR;
					lastEvent = null;
				}
				repaint();
				break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(state) {
			case NODE:
				getNetworkModel().addNode(new NetworkNode("Node", e.getX(), e.getY()));
				repaint();
				break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@SuppressWarnings("incomplete-switch")
	@Override
	public void mousePressed(MouseEvent e) {
		lastEvent = e;
		switch(state) {
			case SELECT:
				GeometryDescriptor gd = pointGeometry(e.getPoint());
				if(gd.equals(GeometryDescriptor.NULL_DESCRIPTOR)) descriptor = GeometryDescriptor.NULL_DESCRIPTOR;
				else if(gd.equals(descriptor)) return;
				else descriptor = gd;
				this.requestFocusInWindow();
				repaint();
				break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void mouseDragged(MouseEvent e) {
		final int THRESHOLD = 3;
		switch(state) {
			case SELECT:
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
				break;
			case CONNECTION:
				drawing = true;
				if(e.getPoint().distance(lastEvent.getPoint()) > THRESHOLD) {
					MouseEvent current = e;
					lastEvent = null;
					GeometryDescriptor start = descriptor;
					mouseMoved(e);
					if(lastEvent != null && (start.additional.containsKey("Target") ||
							start.object.equals(descriptor.object))) {
						try {
							Robot robot = new Robot();
							Point origin = getLocationOnScreen(), p = lastEvent.getPoint();
							p.translate((int)origin.getX(), (int)origin.getY());
							robot.mouseMove((int)p.getX(), (int)p.getY());
						}
						catch (AWTException e1) { }
					}
					if(!descriptor.equals(GeometryDescriptor.NULL_DESCRIPTOR) && !descriptor.object.equals(start.object)) {
						start.additional.put("Target", descriptor);
					}
					else start.additional.remove("Target");
					descriptor = start;
					lastEvent = current;
					repaint();
				}
				break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void mouseMoved(MouseEvent e) {
		final int ERROR = 15;
		switch(state) {
			case CONNECTION:
				FontMetrics fm = getFontMetrics(getFont());
				Point p = new Point();
				for(int i = 0; i < getNetworkModel().nNodes(); i++) {
					NetworkNode n = getNetworkModel().getNode(i);
					Rectangle r = getNetworkNodeBounds(n);
					r.grow(ERROR, ERROR);
					if(!r.contains(e.getPoint())) continue;
					r.grow(-2 * ERROR, -2 * ERROR);
					if(r.contains(e.getPoint())) continue;
					int code = r.outcode(e.getPoint());
					if((code & Rectangle.OUT_TOP) != 0) {
						p.setLocation(n.getX(), n.getY() - fm.getHeight());
						if(p.distance(e.getPoint()) <= ERROR) {
							if(lastEvent == null || lastEvent.getPoint().distance(p) > ERROR) {
								lastEvent = e;
								lastEvent.getPoint().setLocation(p);
								Map<String, Object> map = new TreeMap<String, Object>();
								map.put("Side", NetworkConnection.Side.TOP);
								descriptor = new GeometryDescriptor(n, i, map);
								try {
									Robot robot = new Robot();
									Point origin = getLocationOnScreen();
									p.translate((int)origin.getX(), (int)origin.getY());
									robot.mouseMove((int)p.getX(), (int)p.getY());
								}
								catch (AWTException e1) { }
								repaint();
							}
							break;
						}
					}
					if((code & Rectangle.OUT_RIGHT) != 0) {
						p.setLocation(n.getX() + fm.stringWidth(n.getName()), n.getY());
						if(p.distance(e.getPoint()) <= ERROR) {
							if(lastEvent == null || lastEvent.getPoint().distance(p) > ERROR) {
								lastEvent = e;
								lastEvent.getPoint().setLocation(p);
								Map<String, Object> map = new TreeMap<String, Object>();
								map.put("Side", NetworkConnection.Side.RIGHT);
								descriptor = new GeometryDescriptor(n, i, map);
								try {
									Robot robot = new Robot();
									Point origin = getLocationOnScreen();
									p.translate((int)origin.getX(), (int)origin.getY());
									robot.mouseMove((int)p.getX(), (int)p.getY());
								}
								catch (AWTException e1) { }
								repaint();
							}
							break;
						}
					}
					if((code & Rectangle.OUT_BOTTOM) != 0) {
						p.setLocation(n.getX(), n.getY() + fm.getHeight());
						if(p.distance(e.getPoint()) <= ERROR) {
							if(lastEvent == null || lastEvent.getPoint().distance(p) > ERROR) {
								lastEvent = e;
								lastEvent.getPoint().setLocation(p);
								Map<String, Object> map = new TreeMap<String, Object>();
								map.put("Side", NetworkConnection.Side.BOTTOM);
								descriptor = new GeometryDescriptor(n, i, map);
								try {
									Robot robot = new Robot();
									Point origin = getLocationOnScreen();
									p.translate((int)origin.getX(), (int)origin.getY());
									robot.mouseMove((int)p.getX(), (int)p.getY());
								}
								catch (AWTException e1) { }
								repaint();
							}
							break;
						}
					}
					if((code & Rectangle.OUT_LEFT) != 0) {
						p.setLocation(n.getX() - fm.stringWidth(n.getName()), n.getY());
						if(p.distance(e.getPoint()) <= ERROR) {
							if(lastEvent == null || lastEvent.getPoint().distance(p) > ERROR) {
								lastEvent = e;
								lastEvent.getPoint().setLocation(p);
								Map<String, Object> map = new TreeMap<String, Object>();
								map.put("Side", NetworkConnection.Side.LEFT);
								descriptor = new GeometryDescriptor(n, i, map);
								try {
									Robot robot = new Robot();
									Point origin = getLocationOnScreen();
									p.translate((int)origin.getX(), (int)origin.getY());
									robot.mouseMove((int)p.getX(), (int)p.getY());
								}
								catch (AWTException e1) { }
								repaint();
							}
							break;
						}
					}
				}
				if(lastEvent != null && !descriptor.equals(GeometryDescriptor.NULL_DESCRIPTOR)) {
					NetworkNode n = (NetworkNode)descriptor.object;
					int width = fm.stringWidth(n.getName());
					Point targeting = new Point();
					switch((NetworkConnection.Side)descriptor.additional.get("Side")) {
						case TOP: targeting.setLocation(n.getX(), n.getY() - fontHeight); break;
						case RIGHT: targeting.setLocation(n.getX() + width, n.getY()); break;
						case BOTTOM: targeting.setLocation(n.getX(), n.getY() + fontHeight); break;
						case LEFT: targeting.setLocation(n.getX() - width, n.getY()); break;
					}
					if(e.getPoint().distance(targeting) > ERROR) {
						lastEvent = null;
						descriptor = GeometryDescriptor.NULL_DESCRIPTOR;
						repaint();
					}
				}
				break;
		}
	}
	
	
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
		else if(descriptor.object instanceof NetworkNode) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
					for(int i = 0; i < model.nNodes(); i++) {
						NetworkNode temp = model.getNode(i);
						if(temp.equals(descriptor.object)) {
							getNetworkModel().removeNode(i);
							descriptor = GeometryDescriptor.NULL_DESCRIPTOR;
							break;
						}
					}
					break;
			}
		}
		else if(descriptor.object instanceof NetworkConnection) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
					for(int i = 0; i < model.nConnections(); i++) {
						NetworkConnection temp = model.getConnection(i);
						if(temp.equals(descriptor.object)) {
							getNetworkModel().removeConnection(i);
							descriptor = GeometryDescriptor.NULL_DESCRIPTOR;
							break;
						}
					}
					break;
			}
		}
		else if(descriptor.object instanceof String) {
			NetworkNode node = (NetworkNode)descriptor.additional.get("Node");
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

	
	@Override
	public void stateChanged(ChangeEvent e) {
		JToggleButton button = (JToggleButton)e.getSource();
		if(button.getName().equalsIgnoreCase("Select")) {
			if(button.isSelected()) state = State.SELECT;
			else {
				descriptor = GeometryDescriptor.NULL_DESCRIPTOR;
				repaint();
			}
		}
		else if(button.getName().equalsIgnoreCase("Node")) {
			if(button.isSelected()) state = State.NODE;
		}
		else if(button.getName().equalsIgnoreCase("Connection")) {
			if(button.isSelected()) state = State.CONNECTION;
		}
	}

	
	private Rectangle getNetworkNodeBounds(NetworkNode n) {
		FontMetrics fm = getFontMetrics(getFont());
		int width = fm.stringWidth(n.getName());
		Rectangle r = new Rectangle(new Point((int)n.getX(), (int)n.getY()));
		r.add(n.getX(), n.getY() - (fm.getHeight() - 2));
		r.add(n.getX() + width, n.getY());
		r.add(n.getX(), n.getY() + (fm.getHeight() - 2));
		r.add(n.getX() - width, n.getY());
		return r;
	}
	
	
	@SuppressWarnings("unused")
	private Rectangle getNetworkNodeLabelBounds(NetworkNode n) {
		FontMetrics fm = getFontMetrics(getFont());
		Rectangle r = new Rectangle();
		r.setFrameFromCenter(n.getX(), n.getY(), n.getX() - fm.stringWidth(n.getName()) / 2, n.getY() - fm.getHeight() / 2);
		return r;
	}
	
	
	@SuppressWarnings("unused")
	private Rectangle getNetworkConnectionBounds(NetworkConnection c) {
		NetworkNode n1 = null, n2 = null;
		for(int j = 0; (n1 == null || n2 == null) && j < model.nNodes(); j++) {
			NetworkNode n = model.getNode(j);
			if(n.getName().equals(c.node1)) n1 = n;
			else if(n.getName().equals(c.node2)) n2 = n;
		}
		if (n1 == null || n2 == null) return new Rectangle();
		
		// Get corners
		FontMetrics fm = getFontMetrics(getFont());
		Point p1 = new Point(), p2 = new Point();
		p1.setLocation(n1.getX(), n1.getY());
		switch(c.side1) {
			case BOTTOM: p1.setLocation(p1.getX(), p1.getY() + fm.getHeight()); break;
			case LEFT: p1.setLocation(p1.getX() - fm.stringWidth(n1.getName()), p1.getY()); break;
			case RIGHT: p1.setLocation(p1.getX() + fm.stringWidth(n1.getName()), p1.getY()); break;
			case TOP: p1.setLocation(p1.getX(), p1.getY() - fm.getHeight()); break;
		}
		p2.setLocation(n2.getX(), n2.getY());
		switch(c.side2) {
			case BOTTOM: p2.setLocation(p2.getX(), p2.getY() + fm.getHeight()); break;
			case LEFT: p2.setLocation(p2.getX() - fm.stringWidth(n2.getName()), p2.getY()); break;
			case RIGHT: p2.setLocation(p2.getX() + fm.stringWidth(n2.getName()), p2.getY()); break;
			case TOP: p2.setLocation(p2.getX(), p2.getY() - fm.getHeight()); break;
		}
		
		CubicCurve2D.Double curve = new CubicCurve2D.Double(
				p1.getX(), p1.getY(), p1.getX() + (p1.getX() - n1.getX()), p1.getY() + (p1.getY() - n1.getY()),
				p2.getX() + (p2.getX() - n2.getX()), p2.getY() + (p2.getY() - n2.getY()), p2.getX(), p2.getY());
		
		return curve.getBounds();
	}
}
