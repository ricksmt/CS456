import java.util.Observable;

	/**
	* This class describes a connection between two network nodes
	*/
public class NetworkConnection extends Observable {
	
	protected String node1, node2;
	protected Side side1, side2;
	
	public enum Side {
		Left,
		Right,
		Top,
		Bottom;
		
		public static Side parseSide(char c) {
			switch (c){
				case 'L': return Left;
				case 'R': return Right;
				case 'T': return Top;
				//case 'B': return Bottom;
				default: return Bottom;
			}
		}
		
		@Override
		public String toString() {
			switch(this){
				case Left: return "L";
				case Right: return "R";
				case Top: return "T";
				case Bottom: return "B";
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
	
	@Override
	public String toString() { return "\"" + node1 + "\" " + side1 + " \"" + node2 + "\" " + side2; }
}