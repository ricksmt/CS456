import java.awt.Point;


public class MoveNodeCommandObj extends CommandObj {

	NetworkNode node;
	Point before = new Point(), after = new Point();
	
	public MoveNodeCommandObj(NetworkNode node, Point p) {
		this.node = node;
		before.setLocation(node.getX(), node.getY());
		after.setLocation(p);
	}

	@Override
	protected void execute() {
		node.setLocation(after.getX(), after.getY());
	}

	@Override
	protected void reverse() {
		node.setLocation(before.getX(), before.getY());
	}
}
