import java.util.Stack;

/**
 * It is expected this class will be extended.
 * 
 * Expected usage:
 * 
 * 		CommandObj command = new DerivedCommandObj(args);
 * 		command.Execute();
 * 
 * Or more concisely:
 * 
 * 		(new DerivedCommandObj(args)).Execute();
 * 
 * @author ricksmt
 */
public class CommandObj {

	static private Stack<CommandObj> undo = new Stack<CommandObj>();
	static private Stack<CommandObj> redo = new Stack<CommandObj>();
	
	static public boolean canUndo() { return undo.size() > 0; }
	static public boolean canRedo() { return redo.size() > 0; }
	
	static public void Undo() {
		if(!canUndo()) return;
		CommandObj command = undo.pop();
		command.reverse();
		redo.add(command);
	}
	
	static public void Redo() {
		if(!canRedo()) return;
		CommandObj command = redo.pop();
		command.execute();
		undo.add(command);
	}
	
	public void Execute() {
		execute();
		undo.push(this);
	}
	
	/**
	 * NOT intended for public use
	 */
	protected void execute() { }
	/**
	 * NOT intended for public use
	 */
	protected void reverse() { }
}
