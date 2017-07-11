package gameObjects;

import aiModels.AIModel;

public class GameObjectPlayer extends GameObjectCreature {
	public Board board;
	private int pointsGained;
	
	public GameObjectPlayer(AIModel newModel) {
		super(newModel);
		pointsGained = 0;
		myType = GameObjectType.PLAYER;
		myAlliance = GameObjectCreature.CreatureAlliance.PLAYER;
	}
	
	public GameObject generateClone(GameObject newObject, AIModel newModel) {
		GameObject tmpObject;
		
		if (newObject == null)
			tmpObject = new GameObjectPlayer(newModel);
		else
			tmpObject = newObject;
		
		tmpObject = super.generateClone(tmpObject);
		
		GameObjectPlayer tmpO = (GameObjectPlayer) tmpObject;
		tmpO.board = board;
		tmpO.pointsGained = pointsGained;
		
		return tmpObject;
	}
	
	public int getPointsGained() {
		return pointsGained;
	}
	
	public void setPointsGained(double points, String itemName) {
		pointsGained += points;
		this.myAIModel.receiveFeedbackFromEnvironment(points, itemName);
	}
}
