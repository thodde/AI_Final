package gameObjects;

import actions.Action;
import aiModels.AIModel;

/**
 * Controls all aspects of GameObjects that are capable of independent action
 * @author Andrew
 *
 */
public class GameObjectCreature extends GameObject {
	public AIModel myAIModel;
	public Action currentAction;
	public int stepsTaken;
	public int touchedByGhost;
	public int berriesPickedUp;
	public static enum CreatureAlliance { PLAYER, GHOST, UNDEFINED; }
	public CreatureAlliance myAlliance;	
	public int pitFalls;
	public int wallCollisions;
	
	public GameObjectCreature(AIModel myAIModel) {
		super();
		canBlockMovement = false;
		currentAction = null;
		myAIModel.assignToCreature(this);
		myType = GameObjectType.CREATURE;
		stepsTaken = 0;
		touchedByGhost = 0;
		pitFalls = 0;
		wallCollisions = 0;
		berriesPickedUp = 0;
		myAlliance = CreatureAlliance.GHOST;
	}
	
	public GameObject generateClone(GameObject newObject, AIModel newAIModel) {
		GameObject tmpObject;
		
		if (newObject == null)
			tmpObject = new GameObjectCreature(newAIModel);
		else
			tmpObject = newObject;
		
		tmpObject = super.generateClone(tmpObject);
		
		GameObjectCreature tmpO = (GameObjectCreature) tmpObject;
		tmpO.myAIModel = myAIModel;
		
		return tmpObject;
	}
	
	public void planNextMove() {
		if (myAIModel == null) {
			return;
		}
		currentAction = myAIModel.planNextMove();
	}
	
	public void clearTarget(GameObject target) {
		if (myAIModel == null) {
			return;
		}
		myAIModel.clearTarget(target);
	}
	
	public String describeAIState() {
		if (myAIModel == null)
			return "No AI model set";
		else {
			return myAIModel.describeActionPlan();
		}
	}
}
