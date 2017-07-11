package aiModels;

import actions.ActionMove;

public class AIModelPlayer extends AIModel {
	@Override
	public ActionMove planNextMove() {
		return null;
	}	
	
	@Override
	public String describeActionPlan() { 
		return "Human controlled AI"; 
	}
}
