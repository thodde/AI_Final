package aiModels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import gameObjects.Board;
import gameObjects.GameObject;
import gameObjects.GameObjectCreature;
import primary.ApplicationController;
import primary.ApplicationModel;
import primary.Constants;
import primary.Point;
import view.PrintListNode;
import actions.ActionMove;

public class AIModelLearning extends AIModelSelfAware {
	private boolean visitedSquares[][];
    public ActionMove action;
    public PolicyNode myPolicies[][] = null;
	private int maxIterations = 10;
    public int exploration;
    public double learningRate;
    public String currentActivity;
    
    //set default values for different types of rewards
    private HashMap<String, Double> rewardGuesses = null;
    
	public AIModelLearning() {
		currentActivity = "Undefined";
		//exploration = 150;
		exploration = 10;
		learningRate = 0.1;
	}
	
	@Override
	public void assignToCreature(GameObjectCreature newSelf) {
		mySelf = newSelf;
		mySelf.myAIModel = this;
	}
	
	@Override
	public String describeActionPlan() { 
		return "Reinforcement Learning AI.  Explore rate:" + (exploration / 10) + "% Learning Rate: " + (int)(learningRate*100) + "%  Current Mode:" + currentActivity; 
	}
	
	private HashMap<String, Double> getRewardList() {
		if (rewardGuesses == null) {
			rewardGuesses = new HashMap<String, Double>();
			Board tempBoard = ApplicationModel.getInstance().myBoard;
			if (tempBoard != null) {
				//populate initial values
				for (int y = 0; y < tempBoard.height; y++)
					for (int x = 0; x < tempBoard.width; x++) {
						GameObject tmpGO = ApplicationModel.getInstance().findGOByLocation(new Point(x, y));
						if (!rewardGuesses.containsKey(tmpGO.name))
							rewardGuesses.put(tmpGO.name, new Double(-1.0));
					}
			}
		}
		return rewardGuesses;
	}

	@Override
	public ActionMove planNextMove() {
		if (visitedSquares == null) 
			defaultVisitedSquares();
		
		if(exploration >= ApplicationController.getGenerator().nextInt(1000)) {
			return planNextMoveExploration();
		}
		else {
			return planNextMoveExploitation();
		}
	}
	
	private void defaultVisitedSquares() {
		visitedSquares = new boolean[ApplicationModel.getInstance().myBoard.height][ApplicationModel.getInstance().myBoard.width];
		for (int y = 0; y < visitedSquares.length; y++)
			for (int x = 0; x < visitedSquares[y].length; x++) 
				visitedSquares[y][x] = ApplicationModel.getInstance().findGOByLocation(new Point(x, y)).canBlockMovement;
	}
	
	@Override
	public void setVisibleSquares(Point centerPoint) {
		if (visitedSquares == null) 
			defaultVisitedSquares();

		visitedSquares[centerPoint.y][centerPoint.x] = true;
		super.setVisibleSquares(centerPoint);
	}
	
	/**
	 * Explore if the player doesn't really know what to do or where to go
	 * @return ActionMove
	 */
	public ActionMove planNextMoveExploration() {
		//exploration -= 0.01;
		currentActivity = "Explore";
		visitedSquares[mySelf.myLocation.y][mySelf.myLocation.x]= true; 
		
		int distance = visitedSquares.length * visitedSquares[0].length;
		ArrayList<PolicyMove> bestMoveList = null;
		for (int y = 0; y < visitedSquares.length; y++)
			for (int x = 0; x < visitedSquares[y].length; x++)
				if (!visitedSquares[y][x])
					if (Math.max(Math.abs(mySelf.myLocation.x - x), Math.abs(mySelf.myLocation.y - y)) < distance) {
						bestMoveList = new ArrayList<PolicyMove>();
						bestMoveList.add(populateBestSingleMove(mySelf.myLocation.x, mySelf.myLocation.y, x, y));
						distance = Math.max(Math.abs(mySelf.myLocation.x - x), Math.abs(mySelf.myLocation.y - y));
					} else if (Math.max(Math.abs(mySelf.myLocation.x - x), Math.abs(mySelf.myLocation.y - y)) == distance) {
						bestMoveList.add(populateBestSingleMove(mySelf.myLocation.x, mySelf.myLocation.y, x, y));
					}
		
		if (bestMoveList == null)
			bestMoveList  = getRandomMoveList();
		else 
			Collections.shuffle(bestMoveList);
		
		PolicyMove moveTarget = determineBestMove(bestMoveList);
		if (moveTarget == null) {
			bestMoveList  = getRandomMoveList();
			Collections.shuffle(bestMoveList);
			moveTarget = determineBestMove(bestMoveList);
			Point targetP = Constants.outcomeOfMove(moveTarget, mySelf.myLocation);
			return new ActionMove(targetP, mySelf, moveTarget);
		}
		
		Point targetP = Constants.outcomeOfMove(moveTarget, mySelf.myLocation);
		return new ActionMove(targetP, mySelf, moveTarget);
	}
	
	/**
	 * Use learned knowledge to go for positive rewards
	 * return ActionMove
	 */
	public ActionMove planNextMoveExploitation() {
		currentActivity = "Exploit";
		
		Board myBoard = ApplicationModel.getInstance().myBoard;
		if (myPolicies == null) {
			myPolicies = new PolicyNode[myBoard.height][myBoard.width];
			maxIterations = Math.max(myBoard.height, myBoard.width);
			
			for(int y = 0; y < myBoard.height; y++)
				for (int x = 0; x < myBoard.width; x++) {
					myPolicies[y][x] = new PolicyNode();
				}
			
			populateUtilities();
			determinePolicies();
		}
		
		if (mySelf == null)
			return null;
		
		if (myPolicies[mySelf.myLocation.y][mySelf.myLocation.x] == null)
			return null;
		
		boolean allSameValues = true;
		double policyValue = 100;
		ArrayList<PolicyMove> myBestMoves = new ArrayList<PolicyMove>();
		for (int y = mySelf.myLocation.y - 1; y <= (mySelf.myLocation.y + 1); y++)
			for (int x = mySelf.myLocation.x - 1; x <= (mySelf.myLocation.x + 1); x++) {
				if (x >= 0 && y >= 0 && x < myBoard.width && y < myBoard.height && !mySelf.myLocation.equals(new Point(x, y)))
					if (!myPolicies[y][x].unreachableSquare) {
						if (policyValue == 100) {
							policyValue = myPolicies[y][x].utility;
							myBestMoves = new ArrayList<PolicyMove>();
							myBestMoves.add(populateBestSingleMove(mySelf.myLocation.x, mySelf.myLocation.y, x, y));
						}
						else if (policyValue != myPolicies[y][x].utility && myPolicies[y][x].utility >= 0)
							allSameValues = false;
						else if (policyValue == myPolicies[y][x].utility)
							myBestMoves.add(populateBestSingleMove(mySelf.myLocation.x, mySelf.myLocation.y, x, y));
					}
			}
		
		PolicyMove myPolicy;
		if (allSameValues) {
			Collections.shuffle(myBestMoves);
			myPolicy = determineBestMove(myBestMoves);
		}
		else
			myPolicy = myPolicies[mySelf.myLocation.y][mySelf.myLocation.x].myPolicy;
		Point targetP = Constants.outcomeOfMove(myPolicy, mySelf.myLocation);
		
		return new ActionMove(targetP, mySelf, myPolicy);
	}
	
	@Override
	public void clearTarget(GameObject oldTarget) {
		myPolicies = null;
		mySelf.currentAction = null;
	}	
	
	private void determinePolicies() {
		for (int y = 0; y < myPolicies.length; y++) 
			for (int x = 0; x < myPolicies[y].length; x++) {
				myPolicies[y][x].myPolicy = PolicyMove.UNKNOWN;
				double bestValue = -1;
				
				if (y > 0)
					if (myPolicies[y-1][x].utility > bestValue && !myPolicies[y-1][x].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.UP;
						bestValue = myPolicies[y-1][x].utility;
					}
				if (y > 0 && x > 0)
					if (myPolicies[y-1][x-1].utility > bestValue && !myPolicies[y-1][x-1].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.UPLEFT;
						bestValue = myPolicies[y-1][x-1].utility;
					}
				if (x > 0)
					if (myPolicies[y][x-1].utility > bestValue && !myPolicies[y][x-1].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.LEFT;
						bestValue = myPolicies[y][x-1].utility;
					}
				if (x > 0 && y < (myPolicies.length - 1))
					if (myPolicies[y+1][x-1].utility > bestValue && !myPolicies[y+1][x-1].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.DOWNLEFT;
						bestValue = myPolicies[y+1][x-1].utility;
					}
				if (y < (myPolicies.length - 1))
					if (myPolicies[y+1][x].utility > bestValue && !myPolicies[y+1][x].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.DOWN;
						bestValue = myPolicies[y+1][x].utility;
					}
				if (y < (myPolicies.length - 1) && x < (myPolicies[0].length - 1))
					if (myPolicies[y+1][x+1].utility > bestValue && !myPolicies[y+1][x+1].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.DOWNRIGHT;
						bestValue = myPolicies[y+1][x+1].utility;
					}
				if (x < (myPolicies[0].length - 1))
					if (myPolicies[y][x+1].utility > bestValue && !myPolicies[y][x+1].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.RIGHT;
						bestValue = myPolicies[y][x+1].utility;
					}
				if (x < (myPolicies[0].length - 1) && y > 0)
					if (myPolicies[y-1][x+1].utility > bestValue && !myPolicies[y-1][x+1].unreachableSquare) {
						myPolicies[y][x].myPolicy = PolicyMove.UPRIGHT;
						bestValue = myPolicies[y-1][x+1].utility;
					}
			}
	}
	
	private void populateUtilities() {
		// populate the initial values
		for (int y = 0; y < myPolicies.length; y++) 
			for (int x = 0; x < myPolicies[y].length; x++) {
				GameObject localGO = ApplicationModel.getInstance().findGOByLocation(new Point(x, y));
				myPolicies[y][x].utility = getRewardList().get(localGO.name);
				
				if (localGO.name.equals("Strawberry")) {
					myPolicies[y][x].utilityFixed = true;
				}
				else if (localGO.name.equals("Pit")) {
					myPolicies[y][x].utilityFixed = true;
				}
				else if (localGO.name.equals("Wall")) {
					myPolicies[y][x].utilityFixed = true;
					myPolicies[y][x].unreachableSquare = true;
				}
				else 
					myPolicies[y][x].utilityFixed = false;
			}
		
		//how many times should I do this?.  Right now it is set to the maximum of the board size
		for (int i = 0; i < maxIterations; i++)
			iteratePolicies();
	}
	
	private void iteratePolicies() {
		// average out everything in between
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
					myPI.utility = 0;
					myPI.count = 0;
					tmpPN[y][x] = new PolicyNode();
					
					//set the utility based off the neighboring utilities
					addPolicyInterim(myPI, myPolicies[y][x]); //center square
					if (y > 0)
						addPolicyInterim(myPI, myPolicies[y-1][x]); //up square
					if (y > 0 && x > 0)
						addPolicyInterim(myPI, myPolicies[y-1][x-1]); //up left square
					if (x > 0)
						addPolicyInterim(myPI, myPolicies[y][x-1]); //left square
					if (x > 0 && y < (myPolicies.length - 1))
						addPolicyInterim(myPI, myPolicies[y+1][x-1]); //down left square
					if (y < (myPolicies.length - 1))
						addPolicyInterim(myPI, myPolicies[y+1][x]); //down square
					if (y < (myPolicies.length - 1) && x < (myPolicies[0].length - 1))
						addPolicyInterim(myPI, myPolicies[y+1][x+1]); //down right square
					if (x < (myPolicies[0].length - 1))
						addPolicyInterim(myPI, myPolicies[y][x+1]); //right square
					if (x < (myPolicies[0].length - 1) && y > 0)
						addPolicyInterim(myPI, myPolicies[y-1][x+1]); //up right square
					
					if (myPI.count == 0)
						tmpPN[y][x].utility = myPolicies[y][x].utility;
					else
						tmpPN[y][x].utility = myPI.utility / myPI.count;
				}
			}
		myPolicies = tmpPN;
	}
	
	private void addPolicyInterim(PolicyInterim localPI, PolicyNode testPN) {
		if (testPN.unreachableSquare)
			return;
		
		if ((testPN.utility * 0.9) > localPI.utility)
			localPI.utility = (testPN.utility * 0.9);
		
		localPI.count = 1;
	}
	
	@Override
	public void setAdvancedView(PrintListNode[][] myPL) {
		if (currentActivity.equals("Explore")) {
			if (visitedSquares == null)
				defaultVisitedSquares();
			
			for (int y = 0; y < visitedSquares.length; y++) 
				for (int x = 0; x < visitedSquares[y].length; x++)
					if (visibleSquares[y][x]) {
						if (visitedSquares[y][x])
							myPL[y][x].setUtilityValue(0);
						else
							myPL[y][x].setUtilityValue(1);
					}
		}
		else {
			if (myPolicies == null) 
				return;
			
			for (int y = 0; y < myPolicies.length; y++) 
				for (int x = 0; x < myPolicies[y].length; x++)
					if (visibleSquares[y][x])
						if (myPolicies[y][x] != null)
							if (!myPolicies[y][x].unreachableSquare)
								myPL[y][x].setUtilityValue((int)myPolicies[y][x].utility);
		}
	}

	@Override
	public void setPolicyView(PrintListNode[][] myPL) { 
		if (myPolicies == null) 
			return;
		
		for (int y = 0; y < myPolicies.length; y++) 
			for (int x = 0; x < myPolicies[y].length; x++)
				if (visibleSquares[y][x])
					if (myPolicies[y][x] != null)
						if (!myPolicies[y][x].unreachableSquare)
							myPL[y][x].setPolicyValue(myPolicies[y][x].myPolicy);
	} 
	
	/**
	 * This function allows the player to receive a reward from the environment
	 * when it explores so it can determine what is good and what is bad. If the feedback
	 * is greater than 0, it should update the utility of the strawberries to be higher. If
	 * the feedback is less than 0, it should update the other utilities to be lower.
	 */
	@Override
	public void receiveFeedbackFromEnvironment(double feedback, String itemName) {
		Double curValue = getRewardList().get(itemName);
		
		Double newReward = curValue + (feedback - curValue) * learningRate;
		getRewardList().put(itemName,  newReward);
	}
	
	public LearningObject createLearningObject() {
		return new LearningObject(learningRate, exploration,rewardGuesses);
	}
	
	public void setLearningObject(LearningObject newLO) {
		exploration = newLO.explorationRate;
		learningRate = newLO.learningRate;
		rewardGuesses = newLO.rewardList;
	}
}
