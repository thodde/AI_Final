package aiModels;

import primary.Point;

public class PotentialDestination {
	public int expectedArrivalRate;
	public int targetX;
	public int targetY;
	
	public PotentialDestination(int newRate, int newX, int newY) {
		expectedArrivalRate = newRate;
		targetX = newX;
		targetY = newY;
	}
	
	public PotentialDestination(int newRate, Point targetP) {
		expectedArrivalRate = newRate;
		targetX = targetP.x;
		targetY = targetP.y;
	}
	
}
