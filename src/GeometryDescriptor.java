
public class GeometryDescriptor {
	
	Object object = null;
	int index = -1;
	public static final GeometryDescriptor NULL_DESCRIPTOR = new GeometryDescriptor();
	
	public GeometryDescriptor() { }
	public GeometryDescriptor(Object object, int index) {
		this.object = object;
		this.index = index;
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof GeometryDescriptor) {
			GeometryDescriptor gd = (GeometryDescriptor)other;
			if(index != gd.index) return false;
			else if(object == null && gd.object == null) return true;
			else if(object == null || gd.object == null) return false;
			else if(!object.equals(gd.object)) return false;
			else return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(object == null) sb.append("Null");
		else if(object instanceof NetworkConnection) sb.append("Connection");
		else if(object instanceof NetworkNode) sb.append("Node");
		else if(object instanceof String) sb.append("Character");
		sb.append(" (" + index + ')');
		return sb.toString();
	}
}
