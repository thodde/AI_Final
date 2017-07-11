package aiModels;

import java.util.ArrayList;
import java.util.Collections;

import primary.ApplicationModel;
import primary.Constants;
import primary.Point;
import gameObjects.Board;
import gameObjects.GameObject;
import gameObjects.GameObjectCreature;
import gameObjects.GameObjectCreature.CreatureAlliance;
import actions.ActionMove;

/**
 * Move directly at the target with 1 square look ahead
 *
 * @author Trevor - primary author
 * @author Andrew - minor updates
 */
public class AIModelDirectMove extends AIModelSelfAware {
	GameObject myTarget;

	public AIModelDirectMove() {
		myTarget = null;
	}
	
	@Override
	public ActionMove planNextMove() {
		if (myTarget == null) {
			
			if (mySelf.myAlliance == CreatureAlliance.PLAYER) {
				Board myBoard = ApplicationModel.getInstance().myBoard;
				if (myBoard.myTokens.isEmpty()) 
					return null;
				
				for (int i = 0; i < myBoard.myTokens.size(); i++) {
					if (visibleSquares[myBoard.myTokens.get(i).myLocation.y][myBoard.myTokens.get(i).myLocation.x]) {
						myTarget = myBoard.myTokens.get(i);
						break;
					}
				}
			}
			else { //this is a ghost
					myTarget = ApplicationModel.getInstance().myPlayer;
			}
		}


		ArrayList<PolicyMove> bestMoveList;
		
		if ((myTarget == null) || (myTarget != null && !visibleSquares[myTarget.myLocation.y][myTarget.myLocation.x])) { 
			// No target visible, Randomly move
			bestMoveList = getRandomMoveList();
		}
		else
			bestMoveList = populateBestMoveDeterministicList(mySelf.myLocation.x, mySelf.myLocation.y, myTarget.myLocation.x, myTarget.myLocation.y);
		PolicyMove moveTarget = determineBestMove(bestMoveList);
		
		if (moveTarget == null)
			return null;
		
		Point targetP = Constants.outcomeOfMove(moveTarget, mySelf.myLocation);
		
		if (targetP == null)
			return null;
		
		return new ActionMove(targetP, mySelf, moveTarget);
	}
	
	@Override
	public void clearTarget(GameObject oldTarget) {
		if (myTarget == null)
			return;
		
		if (oldTarget == myTarget) {
			myTarget = null;
		}
	}
		
	@Override
	public String describeActionPlan() {
		String retval = "Direct Move - Move directly to the target object with look ahead of 1 square for blocking squares only";
		
		if (myTarget != null) 
			retval += "  Target: " + myTarget.name + " at (" + myTarget.myLocation.x + ", " + myTarget.myLocation.y + ")";
		
		return retval;
	}
}
