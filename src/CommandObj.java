import java.util.Stack;

/**
 * It is expected this class will be extended.
 * 
 * Expected usage:
 * 
 * 		CommandObj command = new DerivedCommandObj(args);
 * 		command.execute();
 * 
 * Or more concisely:
 * 
 * 		(new DerivedCommandObj(args)).execute();
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
	
	public CommandObj() { undo.push(this); }
	
	protected void execute() { }
	protected void reverse() { }
}
