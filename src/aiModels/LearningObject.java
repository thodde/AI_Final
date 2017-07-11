package aiModels;

import java.util.HashMap;

public class LearningObject {
//current learning rate, current exploration rate, reward list
	public double learningRate;
	public int explorationRate;
	public HashMap<String, Double> rewardList;
	
	public LearningObject(double newLearningRate, int newExplorationRate, HashMap<String, Double> newRewardList) {
		rewardList = newRewardList;
		learningRate = newLearningRate;
		explorationRate = newExplorationRate;
	}
}
