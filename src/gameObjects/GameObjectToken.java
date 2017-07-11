package gameObjects;

public class GameObjectToken extends GameObject {
	public int pointValue;
	
	GameObjectToken() {
		super();
		canBlockMovement = true;
		myType = GameObjectType.TOKEN;
		pointValue = 100;
	}
	
	public GameObject generateClone(GameObject newObject) {
		GameObject tmpObject;
		
		if (newObject == null)
			tmpObject = new GameObjectToken();
		else
			tmpObject = newObject;
		
		tmpObject = super.generateClone(tmpObject);
		GameObjectToken tmpO = (GameObjectToken) tmpObject;
		
		tmpO.pointValue = pointValue;
		
		return tmpObject;
	}

}
