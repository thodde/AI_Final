package actions;

/*
 * Generic class for any planned action
 */
public abstract class Action {
	protected boolean isDone;
	//protected boolean canOverrideAction;
	
	public Action() {
		isDone = true;
		//canOverrideAction = true;
	}
	
	//public boolean getCanOverrideAction() { return canOverrideAction; }
	
	public boolean getIsDone() { return isDone; }
	public void setIsDone(boolean newValue) { isDone = newValue; }
	public abstract void processAction();
	
	public abstract String describeAction();
}
