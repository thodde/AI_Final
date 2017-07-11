package aiModels;

import java.util.ArrayList;
import java.util.Collections;

import gameObjects.Board;
import gameObjects.GameObject;
import gameObjects.GameObjectCreature;
import gameObjects.GameObjectPlayer;
import gameObjects.GameObjectCreature.CreatureAlliance;

import primary.ApplicationModel;
import primary.Constants;
import primary.Point;
import view.ApplicationView;
import view.PrintListNode;

import actions.ActionMove;
import aiModels.AIModel.PolicyInterim;
import aiModels.AIModel.PolicyMove;
import aiModels.AIModel.PolicyNode;

/**
 * Base Heuristic: Distance to strawberry
 */
public class AIModelHillClimb extends AIModelSelfAware {
	PolicyNode myPolicies[][] = null;
	
	public AIModelHillClimb() {
	}

	@Override
	public ActionMove planNextMove() {
		if (myPolicies == null || mySelf.myAlliance == CreatureAlliance.GHOST)
			populateUtilities();
		
		ArrayList<PolicyMove> bestMoveList = getBestMoveList();
		
		PolicyMove moveTarget = determineBestMove(bestMoveList);
		if (moveTarget == null) {
			bestMoveList = getRandomMoveList();
			Collections.shuffle(bestMoveList);
			
			moveTarget = determineBestMove(bestMoveList);
			if (moveTarget == null)
				return null;
		}

		Point targetP = Constants.outcomeOfMove(moveTarget, mySelf.myLocation);
		return new ActionMove(targetP, mySelf, moveTarget);
	}
	
	private ArrayList<PolicyMove> getBestMoveList() {
		Point local = mySelf.myLocation;
		ArrayList<PolicyMove> myMoves = new ArrayList<PolicyMove>();
		double bestValue = myPolicies[local.y][local.x].utility + 1;
		
		for (int y = local.y - 1; y <= (local.y+1); y++)
			for (int x = local.x - 1; x <= (local.x+1); x++) {
				if (x > 0 && y > 0 && x < myPolicies[y].length && y < myPolicies.length)
					if (!myPolicies[y][x].unreachableSquare) {
						if (myPolicies[y][x].utility < bestValue && !local.equals(new Point(x, y))) {
							bestValue = myPolicies[y][x].utility;
							myMoves = new ArrayList<PolicyMove>();
							myMoves.add(populateBestSingleMove(local.x, local.y, x, y));
						}
						else if (myPolicies[y][x].utility == bestValue)
							myMoves.add(populateBestSingleMove(local.x, local.y, x, y));
					}
			}
		
		return myMoves;
	}
	
	@Override
	public void clearTarget(GameObject oldTarget) {
		if (mySelf.myAlliance == CreatureAlliance.PLAYER)
			myPolicies = null; 
	}	

	@Override
	public String describeActionPlan() {
		String retVal = "Hill Climb - Heuristic: Distance to ";
		if (mySelf.myAlliance == CreatureAlliance.PLAYER)
			retVal += "Strawberry";
		else
			retVal += "Pac-man";
		
		retVal += " with lookahead of 1 square for blocking. ";
		
		return retVal;
	}

	private void populateUtilities() {
		myPolicies = new PolicyNode[ApplicationModel.getInstance().myBoard.height][ApplicationModel.getInstance().myBoard.width];
		for (int y = 0; y < myPolicies.length; y++) 
			for (int x = 0; x < myPolicies[y].length; x++)
				myPolicies[y][x] = new PolicyNode();
		
		if (mySelf.myAlliance == CreatureAlliance.PLAYER) { 
			// populate the initial values
			for (int y = 0; y < myPolicies.length; y++) 
				for (int x = 0; x < myPolicies[y].length; x++) {
					GameObject localGO = ApplicationModel.getInstance().findGOByLocation(new Point(x, y));
					if (localGO.name.equals("Strawberry")) {
						myPolicies[y][x].utilityFixed = true;
						myPolicies[y][x].utility = 0;
					}
					else if (localGO.name.equals("Wall")) {
						myPolicies[y][x].utilityFixed = true;
						myPolicies[y][x].unreachableSquare = true;
						myPolicies[y][x].utility = 0;
					}
				}
		}
		else { //This is a ghost
			GameObjectPlayer myPlayer = ApplicationModel.getInstance().myPlayer;
			for (int y = 0; y < myPolicies.length; y++) 
				for (int x = 0; x < myPolicies[y].length; x++) {
					if (myPlayer.myLocation.equals(new Point(x, y))) {
						myPolicies[y][x].utilityFixed = true;
						myPolicies[y][x].utility = 0;
					}
					else {
						GameObject localGO = ApplicationModel.getInstance().findGOByLocation(new Point(x, y));
						if (localGO.name.equals("Wall")) {
							myPolicies[y][x].utilityFixed = true;
							myPolicies[y][x].unreachableSquare = true;
							myPolicies[y][x].utility = 0;
						}
					}
				}			
		}
		
		//how many times should I do this?.  Right now it is set to the maximum of the board size
		int maxIterations = Math.max(ApplicationModel.getInstance().myBoard.width, ApplicationModel.getInstance().myBoard.height);
		for (int i = 0; i < maxIterations; i++)
			iterateUtilityValues();
	}
	
	private void iterateUtilityValues() {
		PolicyNode tmpPN[][] = new PolicyNode[myPolicies.length][myPolicies[0].length];
		for (int y = 0; y < myPolicies.length; y++) 
			for (int x = 0; x < myPolicies[y].length; x++) {
				tmpPN[y][x] = new PolicyNode();
				if (myPolicies[y][x].utilityFixed) {
					tmpPN[y][x] = new PolicyNode(myPolicies[y][x]);
				} else if (myPolicies[y][x].unreachableSquare) {
					tmpPN[y][x] = new PolicyNode(myPolicies[y][x]);
				}
				else {
					PolicyInterim myPI = new PolicyInterim();
					myPI.utility = 100;
					myPI.count = 0;
					tmpPN[y][x] = new PolicyNode();
					
					//set the utility based off the neighboring utilities
					minimizePolicyInterim(myPI, myPolicies[y][x]); //center square
					if (y > 0)
						minimizePolicyInterim(myPI, myPolicies[y-1][x]); //up square
					if (y > 0 && x > 0)
						minimizePolicyInterim(myPI, myPolicies[y-1][x-1]); //up left square
					if (x > 0)
						minimizePolicyInterim(myPI, myPolicies[y][x-1]); //left square
					if (x > 0 && y < (myPolicies.length - 1))
						minimizePolicyInterim(myPI, myPolicies[y+1][x-1]); //down left square
					if (y < (myPolicies.length - 1))
						minimizePolicyInterim(myPI, myPolicies[y+1][x]); //down square
					if (y < (myPolicies.length - 1) && x < (myPolicies[0].length - 1))
						minimizePolicyInterim(myPI, myPolicies[y+1][x+1]); //down right square
					if (x < (myPolicies[0].length - 1))
						minimizePolicyInterim(myPI, myPolicies[y][x+1]); //right square
					if (x < (myPolicies[0].length - 1) && y > 0)
						minimizePolicyInterim(myPI, myPolicies[y-1][x+1]); //up right square
					
					if (myPI.count == 0)
						tmpPN[y][x].utility = myPolicies[y][x].utility;
					else
						tmpPN[y][x].utility = myPI.utility / myPI.count;
				}
			}
		myPolicies = tmpPN;
	}

	private void minimizePolicyInterim(PolicyInterim localPI, PolicyNode testPN) {
		if (testPN.unreachableSquare)
			return;
		
		if ((testPN.utility + 1) < localPI.utility)
			localPI.utility = testPN.utility+1;
		
		localPI.count = 1;
	}
	
	@Override
	public void setAdvancedView(PrintListNode[][] myPL) {
		if (myPolicies == null) 
			return;
		
		for (int y = 0; y < myPolicies.length; y++) 
			for (int x = 0; x < myPolicies[y].length; x++)
				if (myPolicies[y][x] != null)
					if (!myPolicies[y][x].unreachableSquare)
						myPL[y][x].setUtilityValue((int)myPolicies[y][x].utility);
	}
}
