package actions;

import gameObjects.GameObjectCreature;
import gameObjects.GameObjectPlayer;
import primary.Point;

public class EventMove extends Event {
	public String creatureName;
	public GameObjectCreature creature;
	Point destination;

	public EventMove(GameObjectCreature creature, Point newDestination) {
		this.creature = creature;
		destination = new Point(newDestination);
	}
	
	@Override
	public void processEvent() {
		if(creature instanceof GameObjectPlayer) {
			GameObjectPlayer tmpP = (GameObjectPlayer) creature;
			tmpP.setPointsGained(-100, "Open");
		}
		return;
	}

	@Override
	public String writeLogString() {
		return "<EventMove creature='" + creature.name + "' target='(" + destination.x + "," + destination.y + ")' />";
	}
}
