package actions;

import primary.ApplicationController;
import view.ApplicationView;
import gameObjects.*;

public class EventGhostTouchPlayer extends Event {
	GameObjectCreature toucher;
	GameObjectPlayer touchee;
	
	public EventGhostTouchPlayer(GameObjectCreature toucher, GameObjectPlayer touchee) {
		this.toucher = toucher;
		this.touchee = touchee;
	}

	@Override
	public void processEvent() {
		ApplicationController.getInstance().loggedEvents.add(writeLogString());
		touchee.touchedByGhost++;
		ApplicationView.getInstance().displayMessage("Player '" + touchee.name + "' has been touched by '" + toucher.name + "'." );
		
		if (ApplicationController.getInstance().myLoadConfiguration.killOnGhostTouch) {
			ApplicationView.getInstance().displayMessage("Game Over, touched by ghost." );
			ApplicationController.getInstance().finishGame("touched by ghost");
		}
	}

	@Override
	public String writeLogString() {
		return "<EventGhostTouch player='" + touchee.name + "' " +
				"ghost='" + toucher.name + "' location='(" + touchee.myLocation.x + "," + touchee.myLocation.y + ")' " + "/>";
	}

}
