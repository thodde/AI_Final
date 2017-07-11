package aiModels;

import java.util.ArrayList;

import primary.Constants;
import aiModels.AIModel.PolicyMove;
import primary.PhysicsEngine;
import primary.Point;

public class PotentialMove {
	public Point sourcePoint;
	public PolicyMove moveDirection;
	
	public PotentialMove(){
	}
	
	public PotentialMove(PolicyMove direction, int newSourceX, int newSourceY) {
		sourcePoint = new Point(newSourceX, newSourceY);
		moveDirection = direction;
	}
	
	public ArrayList<PotentialDestination> getResults() {
		PolicyMove localDir = moveDirection;
		ArrayList<PotentialDestination> localResults = new ArrayList<PotentialDestination>();
		int pathChance[] = PhysicsEngine.movementModifier;
		for (int counter = 0; counter < 8; counter++) {
			if (pathChance[counter] > 0) {
				Point targetP = Constants.outcomeOfMove(localDir, sourcePoint);
				localResults.add(new PotentialDestination(pathChance[counter], targetP));
			}
			
			localDir = Constants.getMoveDirectionToRight(localDir);				
		}
		
		if (pathChance[8] > 0) {
			localResults.add(new PotentialDestination(pathChance[8], sourcePoint));
		}
		
		return localResults;
	}
}