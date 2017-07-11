package aiModels;

import java.util.ArrayList;

import aiModels.AIModel.PolicyMove;

import primary.ApplicationModel;
import primary.Constants;
import primary.Point;
import gameObjects.GameObjectCreature;

public abstract class AIModelSelfAware extends AIModel {
	GameObjectCreature mySelf;
	
	@Override
	public void assignToCreature(GameObjectCreature newSelf) { 
		mySelf = newSelf;
		newSelf.myAIModel = this;
	}

	@Override
	PolicyMove determineBestMove(ArrayList<PolicyMove> bestList) {
		for (int i = 0; i < bestList.size(); i++) {
			PolicyMove testPolicy = bestList.get(i);
			Point targetPoint = Constants.outcomeOfMove(testPolicy, mySelf.myLocation);
			
			if (!ApplicationModel.getInstance().myBoard.myGO[targetPoint.y][targetPoint.x].canBlockMovement)
				return testPolicy;
		}
		
		return null;
	}

}
