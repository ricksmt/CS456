import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Objects of this class contain information about a network nodes and their connections.  
 */
public class NetworkModel extends Observable implements Observer
{
	public static final String DEFAULT_FILENAME = "Default.txt";
	private String filename;
	private List<NetworkNode> nodes = new ArrayList<NetworkNode>();
	private List<NetworkConnection> connections = new ArrayList<NetworkConnection>();
	private boolean change = false;
	
	/**
	 * Creates an empty network model that has a unique default file name and no contents
	 */
	public NetworkModel () {
		setFileName(DEFAULT_FILENAME);
		parseFile();
	}
	/**
	 * Copy constructor
	 * @param model The NetworkModel to copy
	 */
	public NetworkModel(NetworkModel model) {
		for(int i = 0; i < model.nNodes(); i++) {
			addNode(new NetworkNode(model.getNode(i)));
		}
		for(int i = 0; i < model.nConnections(); i++) {
			addConnection(new NetworkConnection(model.getConnection(i)));
		}
		setFileName(model.getFileName());
	}
	/**
	 * Reads the specific file and creates a new NetworkModel object that contains all of the 
	 * information in the file. If there is no such file then an exception should be thrown.
	 * @param fileName the name of the file to be read.
	 */
	public NetworkModel(String fileName) {
		setFileName(fileName);
		parseFile();
		change = false;
	}
	/**
	 * Returns the name of the file associated with this model.
	 */
	public String getFileName() { return filename; }
	/**
	 * Changes the file name associated with this model
	 * @param newFileName the new file name
	 */
	public void setFileName(String newFileName) {
		change = true;
		filename = newFileName;
		setChanged();
		notifyObservers(filename);
	}
	/**
	 * Saves the contents of this model to its file.
	 * @throws IOException 
	 */
	public void save() throws IOException {
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(filename));
		for(NetworkNode n: nodes) {
			bw.write("N " + n);
			bw.newLine();
		}
		for(NetworkConnection c: connections) {
			bw.write("C " + c);
			bw.newLine();
		}
		bw.close();
		change = false;
	}
	/**
	 * Returns true if there are unsaved changes.
	 */
	public boolean unsavedChanges() { return change; }
	/**
	 * Adds the specified NetworkNode to the list of network objects
	 * @param newNode
	 */
	public void addNode(NetworkNode newNode) {
		newNode.setNetwork(this);
		change = nodes.add(newNode) || change;
		newNode.addObserver(this);
		setChanged();
		notifyObservers(newNode);
	}
	/**
	 * Returns the number of network node objects in this model.
	 */
	public int nNodes() { return nodes.size(); }
	/**
	 * Returns the specified NetworkNode. Indexes begin at zero.
	 * @param i index of the desired object. Must be less than nNodes()
	 */
	public NetworkNode getNode(int i) { return nodes.get(i); }
	/**
	 * Removes the specified object from the list of nodes.
	 * @param i the index of the object to be removed.
	 */
	public void removeNode(int i) {
		if(i >= nodes.size() || i < 0) return;
		change = true;
		NetworkNode node = nodes.remove(i);
		node.setNetwork(null);
		node.deleteObserver(this);
		setChanged();
		notifyObservers(node);
	}
	/**
	 * Adds the specified NetworkConnection to the list of connections
	 * @param newConnection
	 */
	public void addConnection(NetworkConnection newConnection) {
		change = connections.add(newConnection) || change;
		newConnection.addObserver(this);
		setChanged();
		notifyObservers(newConnection);
	}
	/**
	 * Returns the number of network connections in this model.
	 */
	public int nConnections() { return connections.size(); }
	/**
	 * Returns the specified NetworkConnection. Indexes begin at zero.
	 * @param i index of the desired object. Must be less than nConnections()
	 */
	public NetworkConnection getConnection(int i) { return connections.get(i); }
	/**
	 * Removes the specified object from the list of connections
	 * @param i the index of the object to be removed.
	 */
	public void removeConnection(int i) {
		if(i >= connections.size() || i < 0) return;
		change = true;
		NetworkConnection connection = connections.remove(i);
		connection.deleteObserver(this);
		setChanged();
		notifyObservers(connection);
	}
	
	private void parseFile() {
		nodes.clear();
		connections.clear();
		
		try {
			Scanner scanner = new Scanner(new FileReader(filename));
			if (!scanner.hasNext()) {
				scanner.close();
				return;
			}

			while (scanner.hasNext()) {
				String type = scanner.next();
				switch(type){
					case "N":
						double x = scanner.nextDouble();
						double y = scanner.nextDouble();
						String name = scanner.findInLine(Pattern.compile("\"[^\"]*\""));
						int index = name.lastIndexOf('"');
						if (index > 1) name = name.substring(1, index);
						else if (index == 1) name = "";
						addNode(new NetworkNode(name, x, y));
						break;
					case "C":
						String node1 = scanner.findInLine(Pattern.compile("\"[^\"]*\""));
						int index1 = node1.lastIndexOf('"');
						if (index1 > 1) node1 = node1.substring(1, index1);
						else if (index1 == 1) node1 = "";
						NetworkConnection.Side side1 = NetworkConnection.Side.parseSide(scanner.next().charAt(0));
						String node2 = scanner.findInLine(Pattern.compile("\"[^\"]*\""));
						int index2 = node2.lastIndexOf('"');
						if (index2 > 1) node2 = node2.substring(1, index2);
						else if (index2 == 1) node2 = "";
						NetworkConnection.Side side2 = NetworkConnection.Side.parseSide(scanner.next().charAt(0));
						addConnection(new NetworkConnection(node1, side1, node2, side2));
						break;
					default: break;
				}
				if(scanner.hasNext()) scanner.nextLine();
				else break;
			}
			scanner.close();
		}
		catch (FileNotFoundException e) {
			nodes.clear();
			connections.clear();
		}
		setChanged();
		notifyObservers();
	}
	
	/**
	* This method is a regression test to verify that this class is
	* implemented correctly. It should test all of the methods including
	* the exceptions. It should be completely self checking. This 
	* should write "testing NetworkModel" to System.out before it
	* starts and "NetworkModel OK" to System.out when the test
	* terminates correctly. Nothing else should appear on a correct
	* test. Other messages should report any errors discovered.
	**/
	public static void Test() {
		NetworkModel model = new NetworkModel();
		AssertModel(model, 0, 0, false);
		
		model.addNode(new NetworkNode("A", 0, 0));
		AssertModel(model, 1, 0, true);
		
		try { model.save(); }
		catch (IOException e) { assert(false); }
		AssertModel(model, 1, 0, false);
		
		NetworkNode node = model.getNode(0);
		AssertNode(node, "A", 0, 0);
		
		model.addNode(new NetworkNode("B", 1, 2));
		AssertModel(model, 2, 0, true);
		
		node = model.getNode(1);
		AssertNode(node, "B", 1, 2);
		
		model.addConnection(new NetworkConnection("A", NetworkConnection.Side.TOP, 
												  "B", NetworkConnection.Side.BOTTOM));
		AssertModel(model, 2, 1, true);
		
		NetworkConnection connection = model.getConnection(0);
		AssertConnection(connection, "A", NetworkConnection.Side.TOP,
									 "B", NetworkConnection.Side.BOTTOM);
		
		try { model.save(); }
		catch (IOException e) { assert(false); }
		AssertModel(model, 2, 1, false);
		
		model.removeConnection(0);
		AssertModel(model, 2, 0, true);
		
		try { model.save(); }
		catch (IOException e) { assert(false); }
		AssertModel(model, 2, 0, false);
		
		model.removeConnection(0);
		AssertModel(model, 2, 0, false);
		
		model.removeNode(0);
		AssertModel(model, 1, 0, true);
		AssertNode(model.getNode(0), "B", 1, 2);
		
		model = new NetworkModel(DEFAULT_FILENAME);
		AssertModel(model, 2, 0, false);
		AssertNode(model.getNode(0), "A", 0, 0);
		AssertNode(model.getNode(1), "B", 1, 2);
		
		model.removeNode(2);
		AssertModel(model, 2, 0, false);
		
		node = model.getNode(0);
		node.setName("C");
		AssertNode(node, "C", 0, 0);
		
		node.setLocation(10, 20);
		AssertNode(node, "C", 10, 20);
		
		model = new NetworkModel("Empty.txt");
		AssertModel(model, 0, 0, false);

		model.setFileName(DEFAULT_FILENAME);
		try { model.save(); }
		catch (IOException e) { assert(false); }
		AssertModel(model, 0, 0, false);
		
		model.addConnection(new NetworkConnection("A", NetworkConnection.Side.LEFT,
												  "B", NetworkConnection.Side.RIGHT));
		AssertModel(model, 0, 1, true);
		AssertConnection(connection, "A", NetworkConnection.Side.LEFT,
				 					 "B", NetworkConnection.Side.RIGHT);

		try { model.save(); }
		catch (IOException e) { assert(false); }
		AssertModel(model, 0, 1, false);
		
		model = new NetworkModel(DEFAULT_FILENAME);
		AssertModel(model, 0, 1, true);
		AssertConnection(connection, "A", NetworkConnection.Side.LEFT,
				 					 "B", NetworkConnection.Side.RIGHT);
		
		model = new NetworkModel("Empty.txt");
		AssertModel(model, 0, 0, false);

		// Clear Default file for future testing.
		model.setFileName(DEFAULT_FILENAME);
		try { model.save(); }
		catch (IOException e) { assert(false); }
		AssertModel(model, 0, 0, false);
	}
	
	private static boolean AssertModel(NetworkModel model, int nodes, int connections, boolean change){
		assert(model.nodes.size() == nodes);
		assert(model.connections.size() == connections);
		assert(model.change == change);
		assert(model.nNodes() == nodes);
		assert(model.nConnections() == connections);
		return true;
	}
	
	private static boolean AssertNode(NetworkNode node, String name, double x, double y){
		assert(node.getName().equals(name));
		assert(node.getX() == x);
		assert(node.getY() == y);
		return true;
	}
	
	private static boolean AssertConnection(NetworkConnection connection,
			String node1, NetworkConnection.Side side1, String node2, NetworkConnection.Side side2) {
		assert(connection.node1.equals(node1));
		assert(connection.side1 == side1);
		assert(connection.node2.equals(node2));
		assert(connection.side2 == side2);
		return true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof NetworkNode) {
			NetworkNode node = (NetworkNode)o;
			if(arg instanceof String) {// Name change
				String name = (String)arg;
				for(int i = 0; i < nConnections(); i++) {
					NetworkConnection connection = getConnection(i);
					if(connection.node1.equals(name)) connection.node1 = node.getName();
					if(connection.node2.equals(name)) connection.node2 = node.getName();
				}
			}
		}
		else if(o instanceof NetworkConnection);
		else return;// Nothing updated, so don't worry others
		setChanged();
		notifyObservers(o);
	}
}
