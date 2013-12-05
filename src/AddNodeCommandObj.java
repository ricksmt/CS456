
public class AddNodeCommandObj extends ModelCommandObj {

	NetworkNode node;
	
	public AddNodeCommandObj(NetworkModel model, NetworkNode node) {
		super(model);
		this.node = node;
	}

	@Override
	protected void execute() { model.addNode(node); }

	@Override
	protected void reverse() {
		for(int i = 0; i < model.nNodes(); i++) {
			if(model.getNode(i).equals(node)) {
				model.removeNode(i);
				break;
			}
		}
	}
}
