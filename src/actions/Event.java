package actions;

public abstract class Event {
	public abstract void processEvent();
	
	public abstract String writeLogString();
}
