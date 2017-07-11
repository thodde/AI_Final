package actions;

public class ActionStuckInPit extends Action {
	public int turnsStuck;
	
	public ActionStuckInPit() {
		turnsStuck = 5;
		//canOverrideAction = false;
		isDone = false;
	}
	
	@Override
	public void processAction() {
		if (!isDone)
			turnsStuck--;
		
		if (turnsStuck <= 0)
			isDone = true;
		
	}

	@Override
	public String describeAction() {
		return "Stuck in pit for " + turnsStuck + " turns.";
	}

}
