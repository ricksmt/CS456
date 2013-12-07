import java.awt.geom.AffineTransform;


public class TransformCommandObj extends CommandObj {

	NetworkView view;
	AffineTransform before, after;
	
	public TransformCommandObj(NetworkView view, AffineTransform before, AffineTransform after) {
		this.view = view;
		this.before = new AffineTransform(before);
		this.after = new AffineTransform(after);
	}

	@Override
	protected void execute() {
		view.transform = after;
		view.repaint();
	}

	@Override
	protected void reverse() {
		view.transform = before;
		view.repaint();
	}
}
