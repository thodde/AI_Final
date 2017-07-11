package actions;

import aiModels.AIModel.PolicyMove;
import primary.PhysicsEngine;
import primary.Point;
import gameObjects.GameObjectCreature;

/*
 * The action container for any Move action
 */
public class ActionMove extends Action {
	public int targetX;
	public int targetY;
	public PolicyMove moveDirection;
	public GameObjectCreature initiator;
	
	public ActionMove(int newX, int newY, GameObjectCreature newInitiator) {
		super();
		
		targetX = newX;
		targetY = newY;
		initiator = newInitiator;
		determineMovePolicy();
	}
	
	public ActionMove(Point targetLocation, GameObjectCreature newInitiator, PolicyMove targetDirection) {
		super();
		
		targetX = targetLocation.x;
		targetY = targetLocation.y;
		initiator = newInitiator;
		if (targetDirection == PolicyMove.UNKNOWN)
			determineMovePolicy();
		else
			moveDirection = targetDirection;
	}

	public void processAction() {
		PhysicsEngine.moveCreature(this);
	}
	
	private void determineMovePolicy() {
		int sourceX = initiator.myLocation.x;
		int sourceY = initiator.myLocation.y;
		
		if (sourceX > targetX && sourceY < targetY) 
			moveDirection = PolicyMove.DOWNLEFT;
		else if (sourceX == targetX && sourceY < targetY) 
			moveDirection = PolicyMove.DOWN;
		else if (sourceX > targetX && sourceY == targetY) 
			moveDirection = PolicyMove.LEFT;
		else if (sourceX > targetX && sourceY > targetY) 
			moveDirection = PolicyMove.UPLEFT;
		else if (sourceX == targetX && sourceY > targetY) 
			moveDirection = PolicyMove.UP;
		else if (sourceX < targetX && sourceY == targetY) 
			moveDirection = PolicyMove.RIGHT;
		else if (sourceX < targetX && sourceY < targetY) 
			moveDirection = PolicyMove.DOWNRIGHT;
		else if (sourceX < targetX && sourceY > targetY) 
			moveDirection = PolicyMove.UPRIGHT;
		else if (sourceX == targetX && sourceY == targetY) 
			moveDirection = PolicyMove.NOWHERE;
		else 
			moveDirection = PolicyMove.UNKNOWN;
	}

	@Override
	public String describeAction() {
		String retval = initiator.name + " moving to (" + targetX + ", " + targetY + ")";
		return retval;
	}

}
