package aiModels;

import java.util.ArrayList;
import java.util.Collections;

import primary.ApplicationController;
import primary.ApplicationModel;
import primary.Point;
//import primary.Constants.PolicyMove;
import view.PrintListNode;
import gameObjects.Board;
import gameObjects.GameObject;
import gameObjects.GameObjectCreature;
import actions.ActionMove;

public abstract class AIModel {
	public static enum PolicyMove {UP, UPLEFT, LEFT, DOWNLEFT, DOWN, DOWNRIGHT, RIGHT, UPRIGHT, NOWHERE, UNKNOWN; }
	public boolean visibleSquares[][];
	
    class PolicyInterim {
		public double utility;
		public int count;
	}
	
	class PolicyNode {
		public double utility;
		public PolicyMove myPolicy;
		public boolean utilityFixed;
		public boolean unreachableSquare;
		
		PolicyNode() {
			utility = 0.0;
			myPolicy = PolicyMove.UNKNOWN;
			utilityFixed = false;
			unreachableSquare = false;
		}
		
		PolicyNode(PolicyNode dupe) {
			utility = dupe.utility;
			myPolicy = dupe.myPolicy;
			utilityFixed = dupe.utilityFixed;
			unreachableSquare = dupe.unreachableSquare;
		}
	}
	
	public void setInitialValues(boolean initialValue) {
		Board localBoard = ApplicationModel.getInstance().myBoard;
		visibleSquares = new boolean[localBoard.height][localBoard.width];
		for (int y = 0; y < visibleSquares.length; y++)
			for (int x = 0; x < visibleSquares[y].length; x++)
				visibleSquares[y][x] = initialValue;
	}
	
	public void setVisibleSquares(Point centerPoint) {
		if (ApplicationController.getInstance().myLoadConfiguration.visibleWorld)
			return;
		
		int sightRange = ApplicationController.VISIBILITYRANGE;
		
		for (int y = centerPoint.y - sightRange; y <= (centerPoint.y + sightRange); y++)
			for (int x = centerPoint.x - sightRange; x <= (centerPoint.x + sightRange); x++) {
				if (x >= 0 && x < visibleSquares[0].length && y >= 0 && y < visibleSquares.length) {
					visibleSquares[y][x] = true;
				}
			}
	}
	
	
	/**
	 * This action returns the next move to be done.  However, this doesn't limit the AI Model
	 * to only level deep action planning.  Internal representations can store multiple actions,
	 * and this method can pop them off one at a time.
	 * 
	 * @return
	 */
	public abstract ActionMove planNextMove();
	public void clearTarget(GameObject oldTarget) { return; }
	public String describeActionPlan() { return "undefined"; }
	
	/**
	 * This sets the more advanced view features such as coloring and utility/heuristic values.  It is up to the 
	 * child AIModel to determine how this is used.
	 * 
	 * @param myPL
	 */
	public void setAdvancedView(PrintListNode[][] myPL) { return; } 
	
	/**
	 * This sets the more advanced view features for policies.  This is only implemented on models that actually 
	 * do some level of policy determination.  It is up to the child AIModel to determine how this is used.  This guy
	 * should be overridden second to the setAdvancedView function above.
	 * 
	 * @param myPL
	 */
	public void setPolicyView(PrintListNode[][] myPL) { return; } 
	
	/**
	 * This method assigns the AI model to the creature and creates any necessary linking back in the AI model
	 * UPDATE (11/8/12): This is a change from the old code.  This replaces the multiple different constructors we were
	 * using.  This is set up so that we can create the AI model before the creature is created.
	 * 
	 * @param newSelf
	 */
	public void assignToCreature(GameObjectCreature newSelf) { newSelf.myAIModel = this; }
	
	/**
	 * This method is used for communicating information from the environment back to the AI model since
	 * not all the attributes of a model or a game object are public and we need to share feedback somehow
	 * in order to help the player learn about its environment as it explores.
	 * 
	 * @param feedback
	 */
	public void receiveFeedbackFromEnvironment(double feedback, String itemName) {	}
	
	public ArrayList<PolicyMove> getRandomMoveList() {
		ArrayList<PolicyMove> bestMoveList = new ArrayList<PolicyMove>();
		bestMoveList.add(PolicyMove.UP);
		bestMoveList.add(PolicyMove.DOWN);
		bestMoveList.add(PolicyMove.LEFT);
		bestMoveList.add(PolicyMove.RIGHT);
		bestMoveList.add(PolicyMove.UPLEFT);
		bestMoveList.add(PolicyMove.UPRIGHT);
		bestMoveList.add(PolicyMove.DOWNLEFT);
		bestMoveList.add(PolicyMove.DOWNRIGHT);
		Collections.shuffle(bestMoveList);
		return bestMoveList;
	}

	PolicyMove determineBestMove(ArrayList<PolicyMove> bestList) {
		return bestList.get(0);
	}
	
	public ArrayList<PolicyMove> populateBestMoveDeterministicList(int sourceX, int sourceY, int targetX, int targetY) {
		ArrayList<PolicyMove> bestMoveList = new ArrayList<PolicyMove>();
		
		if (sourceX > targetX && sourceY < targetY) {
			bestMoveList.add(PolicyMove.DOWNLEFT);
			bestMoveList.add(PolicyMove.DOWN);
			bestMoveList.add(PolicyMove.LEFT);
			bestMoveList.add(PolicyMove.DOWNRIGHT);
			bestMoveList.add(PolicyMove.UPLEFT);
			bestMoveList.add(PolicyMove.RIGHT);
			bestMoveList.add(PolicyMove.UP);
			bestMoveList.add(PolicyMove.UPRIGHT);
		}
		else if (sourceX == targetX && sourceY < targetY) {
			bestMoveList.add(PolicyMove.DOWN);
			bestMoveList.add(PolicyMove.DOWNLEFT);
			bestMoveList.add(PolicyMove.DOWNRIGHT);
			bestMoveList.add(PolicyMove.LEFT);
			bestMoveList.add(PolicyMove.RIGHT);
			bestMoveList.add(PolicyMove.UPLEFT);
			bestMoveList.add(PolicyMove.UPRIGHT);
			bestMoveList.add(PolicyMove.UP);
		}
		else if (sourceX > targetX && sourceY == targetY) {
			bestMoveList.add(PolicyMove.LEFT);
			bestMoveList.add(PolicyMove.DOWNLEFT);
			bestMoveList.add(PolicyMove.UPLEFT);
			bestMoveList.add(PolicyMove.UP);
			bestMoveList.add(PolicyMove.DOWN);
			bestMoveList.add(PolicyMove.DOWNRIGHT);
			bestMoveList.add(PolicyMove.UPRIGHT);
			bestMoveList.add(PolicyMove.RIGHT);
		}
		else if (sourceX > targetX && sourceY > targetY) {
			bestMoveList.add(PolicyMove.UPLEFT);
			bestMoveList.add(PolicyMove.LEFT);
			bestMoveList.add(PolicyMove.UP);
			bestMoveList.add(PolicyMove.DOWNLEFT);
			bestMoveList.add(PolicyMove.UPRIGHT);
			bestMoveList.add(PolicyMove.DOWN);
			bestMoveList.add(PolicyMove.RIGHT);
			bestMoveList.add(PolicyMove.DOWNRIGHT);
		}
		else if (sourceX == targetX && sourceY > targetY) {
			bestMoveList.add(PolicyMove.UP);
			bestMoveList.add(PolicyMove.UPLEFT);
			bestMoveList.add(PolicyMove.UPRIGHT);
			bestMoveList.add(PolicyMove.LEFT);
			bestMoveList.add(PolicyMove.RIGHT);
			bestMoveList.add(PolicyMove.DOWNLEFT);
			bestMoveList.add(PolicyMove.DOWNRIGHT);
			bestMoveList.add(PolicyMove.DOWN);
		}
		else if (sourceX < targetX && sourceY == targetY) {
			bestMoveList.add(PolicyMove.RIGHT);
			bestMoveList.add(PolicyMove.DOWNRIGHT);
			bestMoveList.add(PolicyMove.UPRIGHT);
			bestMoveList.add(PolicyMove.UP);
			bestMoveList.add(PolicyMove.DOWN);
			bestMoveList.add(PolicyMove.DOWNLEFT);
			bestMoveList.add(PolicyMove.UPLEFT);
			bestMoveList.add(PolicyMove.LEFT);
		}
		else if (sourceX < targetX && sourceY < targetY) {
			bestMoveList.add(PolicyMove.DOWNRIGHT);
			bestMoveList.add(PolicyMove.DOWN);
			bestMoveList.add(PolicyMove.RIGHT);
			bestMoveList.add(PolicyMove.DOWNLEFT);
			bestMoveList.add(PolicyMove.UPRIGHT);
			bestMoveList.add(PolicyMove.LEFT);
			bestMoveList.add(PolicyMove.UP);
			bestMoveList.add(PolicyMove.UPLEFT);
		}
		else if (sourceX < targetX && sourceY > targetY) {
			bestMoveList.add(PolicyMove.UPRIGHT);
			bestMoveList.add(PolicyMove.RIGHT);
			bestMoveList.add(PolicyMove.UP);
			bestMoveList.add(PolicyMove.DOWNRIGHT);
			bestMoveList.add(PolicyMove.UPLEFT);
			bestMoveList.add(PolicyMove.DOWN);
			bestMoveList.add(PolicyMove.LEFT);
			bestMoveList.add(PolicyMove.DOWNLEFT);
		}
		else {
			bestMoveList = getRandomMoveList();
		}
			
		
		return bestMoveList;
	}
	
	
	public PolicyMove populateBestSingleMove(int sourceX, int sourceY, int targetX, int targetY) {
		if (sourceX > targetX && sourceY < targetY)
			return PolicyMove.DOWNLEFT;
		else if (sourceX == targetX && sourceY < targetY) 
			return PolicyMove.DOWN;
		else if (sourceX > targetX && sourceY == targetY)
			return PolicyMove.LEFT;
		else if (sourceX > targetX && sourceY > targetY)
			return PolicyMove.UPLEFT;
		else if (sourceX == targetX && sourceY > targetY)
			return PolicyMove.UP;
		else if (sourceX < targetX && sourceY == targetY) 
			return PolicyMove.RIGHT;
		else if (sourceX < targetX && sourceY < targetY) 
			return PolicyMove.DOWNRIGHT;
		else
			return PolicyMove.UPRIGHT;
		
		/*
		if (sourceX > targetX && sourceY < targetY)
			return PolicyMove.DOWNLEFT;
		else if (sourceX == targetX && sourceY < targetY) 
			return PolicyMove.DOWN;
		else if (sourceX > targetX && sourceY == targetY)
			return PolicyMove.LEFT;
		else if (sourceX > targetX && sourceY > targetY)
			return PolicyMove.UPLEFT;
		else if (sourceX == targetX && sourceY > targetY)
			return PolicyMove.UP;
		else if (sourceX < targetX && sourceY == targetY) 
			return PolicyMove.RIGHT;
		else if (sourceX < targetX && sourceY < targetY) 
			return PolicyMove.DOWNRIGHT;
		else
			return PolicyMove.UPRIGHT;
		*/
	}

}
