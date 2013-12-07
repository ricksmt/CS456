
public class AddConnectionCommandObj extends CommandObj {

	NetworkModel model;
	NetworkConnection connection;
	
	public AddConnectionCommandObj(NetworkModel model, NetworkConnection connection) {
		this.model = model;
		this.connection = connection;
	}

	@Override
	protected void execute() { model.addConnection(connection); }

	@Override
	protected void reverse() {
		for(int i = 0; i < model.nConnections(); i++) {
			if(model.getConnection(i).equals(connection)) {
				model.removeConnection(i);
				break;
			}
		}
	}
}
