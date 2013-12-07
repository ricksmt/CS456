import java.util.HashMap;




public class ChangeNodeNameCommandObj extends CommandObj {

	NetworkView view;
	NetworkNode node;
	String name1, name2;
	int index1, index2;
	
	public ChangeNodeNameCommandObj(NetworkView view, GeometryDescriptor before, GeometryDescriptor after) {
		this.view = view;
		this.node = (NetworkNode)before.additional.get("Node");
		name1 = (String)before.object;
		name2 = (String)after.object;
		index1 = before.index;
		index2 = after.index;
	}

	@Override
	protected void execute() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Node", node);
		node.setName(name2);
		view.descriptor = new GeometryDescriptor(name2, index2, map);
		view.repaint();
	}

	@Override
	protected void reverse() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Node", node);
		node.setName(name1);
		view.descriptor = new GeometryDescriptor(name1, index1, map);
		view.repaint();
	}
}
