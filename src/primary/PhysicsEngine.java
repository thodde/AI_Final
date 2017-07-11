package primary;

import java.util.ArrayList;

import view.ApplicationView;
import gameObjects.*;
import gameObjects.GameObject.GameObjectType;
import actions.*;
import aiModels.PotentialDestination;
import aiModels.PotentialMove;

public class PhysicsEngine {
	//change this for non-deterministic worlds
	public static boolean deterministicMovement = true;
	
	//the overall multiplier for non-deterministic worlds.  the first square is straight, with each next number being the chance to the right
	// the last square is the chance to stay still.  these are normalized when being used
	public static int movementModifier[] = { 100, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public static void moveCreature(ActionMove myAM) {
		int targetX = myAM.initiator.myLocation.x;
		int targetY = myAM.initiator.myLocation.y;
		
		if (deterministicMovement) {
			targetX = myAM.initiator.myLocation.x;
			targetY = myAM.initiator.myLocation.y;

			if (targetX < myAM.targetX) {
				targetX++;
			}
			if (targetX > myAM.targetX) {
				targetX--;
			}
			if (targetY < myAM.targetY) {
				targetY++;
			}		
			if (targetY > myAM.targetY) {
				targetY--;
			}
		}
		else { // non deterministic movement
			PotentialMove testMove = new PotentialMove(myAM.moveDirection, myAM.initiator.myLocation.x, myAM.initiator.myLocation.y);
			
			ArrayList<PotentialDestination> myTestDest = testMove.getResults();
			if (myTestDest == null) 
				return;
			int totalPool = 0;
			for (int i = 0; i < myTestDest.size(); i++)
				totalPool += myTestDest.get(i).expectedArrivalRate;
			int randomValue = ApplicationController.getGenerator().nextInt(totalPool);

			for (int i = 0; i < myTestDest.size(); i++) {
				randomValue -= myTestDest.get(i).expectedArrivalRate;
				if (randomValue <= 0) { // choose this movement
					targetX = myTestDest.get(i).targetX;
					targetY = myTestDest.get(i).targetY;
					break;
				}
			}
			
		}
		
		ApplicationModel myModel = ApplicationModel.getInstance();
		GameObject tmp = myModel.findGOByLocation(new Point(targetX, targetY));
		
		ApplicationController.getInstance().loggedEvents.add(new EventMove(myAM.initiator, new Point(targetX, targetY)).writeLogString());
		
		ApplicationView myView = ApplicationView.getInstance();
		
		if (tmp == null) {
			myAM.setIsDone(true);
			myView.displayMessage("Error, attempting to move '" + myAM.initiator.name + "' to a location that does not have a background object.");
		}
		else if (tmp.canBlockMovement) {
			myAM.setIsDone(true);

			myView.displayMessage("Error, while moving '" + myAM.initiator.name + "' encountered a square blocked by '" + tmp.name + "'");
			
			if (tmp.name.equals(myModel.myBoard.templateBoundaryWall.name))
				myAM.initiator.wallCollisions++;
		}
		else {
			if ((tmp.getType() == GameObjectType.TOKEN) && (myAM.initiator.getType() == GameObjectType.PLAYER)) {
				GameObjectPlayer tmpPlayer = (GameObjectPlayer) myAM.initiator;
				GameObjectToken tmpToken = (GameObjectToken) tmp;
				ApplicationController.getInstance().currentEvents.push(new EventPickupToken(tmpPlayer, tmpToken));
			}
			
			myAM.initiator.myLocation.x = targetX;
			myAM.initiator.myLocation.y = targetY;
			myAM.initiator.stepsTaken++;
			
			// make the square and all around it visible
			if (myAM.initiator.getType() == GameObjectType.PLAYER)
				myAM.initiator.myAIModel.setVisibleSquares(myAM.initiator.myLocation);
			
			if (myModel.myBoard.myGO[targetY][targetX].name.equals("Pit"))
				ApplicationController.getInstance().currentEvents.push(new EventFallenInPit(myAM.initiator));
			
			GameObjectCreature redGhost = myModel.redGhost;
			if (redGhost != null)
				if ((myAM.initiator.myLocation.equals(redGhost.myLocation)) && (myAM.initiator.myAlliance != redGhost.myAlliance)) {
					GameObjectPlayer tmpPlayer = (GameObjectPlayer) myAM.initiator;
					ApplicationController.getInstance().currentEvents.push(new EventGhostTouchPlayer(myModel.redGhost, tmpPlayer));
				}
			
			GameObjectCreature blueGhost = myModel.blueGhost;
			if (blueGhost != null)
		if ((myAM.initiator.myLocation.equals(blueGhost.myLocation)) && (myAM.initiator.myAlliance != blueGhost.myAlliance)) {
			GameObjectPlayer tmpPlayer = (GameObjectPlayer) myAM.initiator;
			ApplicationController.getInstance().currentEvents.push(new EventGhostTouchPlayer(myModel.blueGhost, tmpPlayer));
		}
			
			GameObjectPlayer myPlayer = myModel.myPlayer;
			if ((myAM.initiator.myLocation.equals(myPlayer.myLocation)) && (myAM.initiator.myAlliance != myPlayer.myAlliance)) {
				ApplicationController.getInstance().currentEvents.push(new EventGhostTouchPlayer(myAM.initiator, myPlayer));
			}
		}

		if ((myAM.initiator.myLocation.x == myAM.targetX) && (myAM.initiator.myLocation.y == myAM.targetY))
			myAM.setIsDone(true);
	}
}