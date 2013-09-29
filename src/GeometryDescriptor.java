
public class GeometryDescriptor {
	
	int index;
	
	public GeometryDescriptor(int index) { this.index = index; }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Index: " + index);
		return sb.toString();
	}
}
