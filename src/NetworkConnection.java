import java.util.Observable;

	/**
	* This class describes a connection between two network nodes
	*/
public class NetworkConnection extends Observable {
	
	protected String node1, node2;
	protected Side side1, side2;
	
	public enum Side {
		TOP,
		RIGHT,
		BOTTOM,
		LEFT;
		
		public static Side parseSide(char c) {
			switch (c){
				case 'L': return LEFT;
				case 'R': return RIGHT;
				case 'T': return TOP;
				//case 'B': return Bottom;
				default: return BOTTOM;
			}
		}
		
		@Override
		public String toString() {
			switch(this){
				case LEFT: return "L";
				case RIGHT: return "R";
				case TOP: return "T";
				case BOTTOM: return "B";
				default: return "";// TODO: Keep updated
			}
		}
	}
	
	/**
	* Creates a new connection
	* @param node1 the name of the first node to be connected
	* @param side1 specifies the side of node1 to which the connection is to be attached
	* @param node2 the name of the second node to be connected
	* @param side2 specifies the side of node2 to which the connection is to be attached
	*/
	public NetworkConnection(String node1, Side side1, String node2, Side side2) {
		this.node1 = node1;
		this.side1 = side1;
		this.node2 = node2;
		this.side2 = side2;
	}
	public NetworkConnection(NetworkConnection connection) {
		this.node1 = connection.node1;
		this.side1 = connection.side1;
		this.node2 = connection.node2;
		this.side2 = connection.side2;
	}
	
	@Override
	public String toString() { return "\"" + node1 + "\" " + side1 + " \"" + node2 + "\" " + side2; }
}