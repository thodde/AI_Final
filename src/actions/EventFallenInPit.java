package actions;

import primary.ApplicationController;
import view.ApplicationView;
import gameObjects.GameObjectCreature;
import gameObjects.GameObjectPlayer;

public class EventFallenInPit extends Event {
	GameObjectCreature targetCreature;
	
	public EventFallenInPit(GameObjectCreature newTarget) {
		targetCreature = newTarget;
	}
	
	@Override
	public void processEvent() {
		ApplicationController.getInstance().loggedEvents.add(writeLogString());
		if (targetCreature instanceof GameObjectPlayer) {
			GameObjectPlayer tmpP = (GameObjectPlayer) targetCreature;
			ApplicationView.getInstance().displayMessage("Player '" + tmpP.name + "' has fallen into a pit.  100 points have been deducted.");
			tmpP.setPointsGained(-100, "Pit");
		}
		else
			ApplicationView.getInstance().displayMessage("Creature '" + targetCreature.name + "' has fallen into a pit.");
		
		targetCreature.pitFalls++;
		targetCreature.currentAction = new ActionStuckInPit();
	}

	@Override
	public String writeLogString() {
		return "<EventFallIntoPit creature='" + targetCreature.name + "' " +
				"location='(" + targetCreature.myLocation.x + "," + targetCreature.myLocation.y + ")' " + "/>";
	}

}
