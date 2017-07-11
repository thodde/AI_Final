package primary;

import view.ApplicationView;

public class GamePlayTimeKeeper {
	public static enum PlayRate { HUMANPLAYER, AIAUTOMATION; }
	private PlayRate myPlayRate;
	private boolean isPaused;
	private boolean gameOver;
	private long lastTurnRun;
	private long minimumTurnLengthinMS;
	public boolean stepOneRound;
	
	public GamePlayTimeKeeper(PlayRate gameRate) {
		myPlayRate = gameRate;
		gameOver = false;
		isPaused = true;
		lastTurnRun = new java.util.Date().getTime();
		minimumTurnLengthinMS = 200;
		stepOneRound = false;
	}
	
	public boolean isTimeForTurn() {
		if (gameOver) 
			return false;
		
		if (stepOneRound) {
			stepOneRound = false;
			return true;
		}

		if (isPaused) 
			return false;
		
		if (myPlayRate == PlayRate.HUMANPLAYER) {
			if (ApplicationModel.getInstance().myPlayer.currentAction == null)
				return false;
			else
				return true;
		}
		
		if (myPlayRate == PlayRate.AIAUTOMATION){
			java.util.Date curDate = new java.util.Date();
			
			if ((curDate.getTime() - lastTurnRun) > minimumTurnLengthinMS) {
				lastTurnRun = curDate.getTime();
				return true;
			}
		}
		
		return false;
	}
	
	public void alterDelayBetweenTurns(long difference) {
		minimumTurnLengthinMS -= difference;
		if (minimumTurnLengthinMS < 0) 
			minimumTurnLengthinMS = 0;
		
		ApplicationView.getInstance().displayMessage("Time between turns set to: " + minimumTurnLengthinMS + " milliseconds");
	}
	
	public void setGameOver() {
		isPaused = true;
		gameOver = true;
	}
	
	public void invertPause() {
		isPaused = !isPaused;
		ApplicationView.getInstance().displayMessage("Game Pause: " + isPaused);
	}
	
	public void setPause(boolean value) {
		isPaused = value;
	}
}
