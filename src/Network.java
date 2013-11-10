


public class Network {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 0) {
			NetworkModel.Test();
			System.out.println("Testing...PASSED");
			return;
		}

		new NetworkFrame(NetworkModel.DEFAULT_FILENAME);
	}
}
