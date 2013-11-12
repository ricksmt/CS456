import java.util.Map;
import java.util.TreeMap;

public class GeometryDescriptor {
	
	Object object = null;
	int index = -1;
	Map<String, Object> additional = new TreeMap<String, Object>();
	public static final GeometryDescriptor NULL_DESCRIPTOR = new GeometryDescriptor();
	
	public GeometryDescriptor() { }
	public GeometryDescriptor(GeometryDescriptor gd) {
		this.object = gd.object;
		this.index = gd.index;
		this.additional.putAll(gd.additional);
	}
	public GeometryDescriptor(Object object, int index) {
		this.object = object;
		this.index = index;
	}
	public GeometryDescriptor(Object object, int index, Map<String, Object> map) {
		this.object = object;
		this.index = index;
		this.additional.putAll(map);
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof GeometryDescriptor) {
			GeometryDescriptor gd = (GeometryDescriptor)other;
			if(index != gd.index) return false;
			else if(object == null && gd.object == null) return true;
			else if(object == null || gd.object == null) return false;
			else if(!object.equals(gd.object)) return false;
			else if(!additional.equals(gd.additional)) return false;
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
