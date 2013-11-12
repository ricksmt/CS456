import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Observable;

/**
* Objects of this class describe a single node in a network.
**/

public class NetworkNode extends Observable
{
	private String name;
	private double x;
	private double y;
	private NetworkModel model = null;
	
	/**
	* Creates a network node
	* @param nodeName the name that the node will be identified by.
	* 	Names are exact and case sensitive.
	* @param xCenter the X coordinate of the center of the node in pixels
	* @param yCenter the Y coordinate of the center of the node in pixels
	*/
	public NetworkNode(String nodeName, double xCenter, double yCenter)
	{
		setName(nodeName);
		setLocation(xCenter, yCenter);
	}
	public NetworkNode(NetworkNode node) {
		setName(node.getName());
		setLocation(node.getX(), node.getY());
		setNetwork(node.getNetwork());
	}
	
	// Getters and Setters
	/**
	* @return name of the node
	*/
	public String getName() { return name; }
	/**
	* @return the X coordinate of the center of the node
	*/
	public double getX() { return x; }
	/**
	* @return the Y coordinate of the center of the node
	*/
	public double getY() { return y; }
	/**
	* @return the network that this node belongs to
	*/
	public NetworkModel getNetwork() { return model; }
	
	/**
	* Changes the name of the node
	* @param newName
	*/
	public void setName(String newName) {
		String old = name;
		name = newName;
		setChanged();
		notifyObservers(old);
	}
	/**
	* Changes the location of the center of the node
	*/
	public void setLocation(double xCenter, double yCenter) {
		Point point = new Point();
		point.setLocation(x, y);
		x = xCenter;
		y = yCenter;
		setChanged();
		notifyObservers(point);
	}
	/**
	* Sets a reference to the network model that this node belongs to
	* @param network
	*/
	public void setNetwork(NetworkModel network) {
		NetworkModel nm = model;
		model = network;
		setChanged();
		notifyObservers(nm);
	}
	
	// Methods	
	@Override
	public String toString() { return x + " " + y + " \"" + name + "\""; }
}