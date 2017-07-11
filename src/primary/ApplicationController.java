package primary;

import gameObjects.GameObjectPlayer;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import actions.ActionMove;
import actions.Event;
import aiModels.*;
import aiModels.AIModel.PolicyMove;
import primary.GamePlayTimeKeeper.PlayRate;
import primary.Point;

import view.ApplicationView;
import xml.Message;

/**
 * Negotiates all interactions between the User, View and Model
 *
 */
public class ApplicationController {
	public static enum GameView { STANDARD, UTILITY, POLICY, INFORMATIONZONE; }
	
	private static Random generator = null;
	static ApplicationController thisController = null;
	private Timer renderTimer;	// our time keeper
	private TimerTask renderTask; // the main render and update task.
	public Stack<Event> currentEvents;
	private volatile GamePlayTimeKeeper myTimeKeeper;
	public ArrayList<String> loggedEvents;
	public GameConfiguration myLoadConfiguration;
	public GameView myGameView;
	private AtomicBoolean resettingGame;
	public static final int VISIBILITYRANGE = 5;
	private AtomicBoolean threadInProcess;
	private String terminateReason;
	private boolean terminate;
	private boolean showScreenInValidationMode;
	private int turnCounter;
	
	public ApplicationController() {
		currentEvents = new Stack<Event>();
		myTimeKeeper = new GamePlayTimeKeeper(PlayRate.AIAUTOMATION);
		threadInProcess = new AtomicBoolean(false);
		resettingGame = new AtomicBoolean(false);
		showScreenInValidationMode = false;
	}
	
	public static Random getGenerator() {
		if (generator == null)
			generator = new Random();
		
		return generator;
	}
	
	public static ApplicationController getInstance() {
		if (thisController == null)
			thisController = new ApplicationController();
		
		return thisController;
	}
	
	private boolean loadFromXMLFile(String xmlFile) {
		if (!Message.configure("logs" + Constants.fileDelimiter + "Board.xsd")) { 
			System.err.println("Error, cannot load Board XSD file");
			System.exit(-1);
		}
				
		Message inMessage = null;
		
		try {
			String inFile = "";
			BufferedReader inBR = new BufferedReader(new FileReader("logs" + Constants.fileDelimiter + xmlFile));

			String inSegment = inBR.readLine();
			while (inSegment != null) {
				inFile += inSegment;
				inSegment = inBR.readLine();
			}
			inBR.close();
	
			inMessage = new Message(inFile);
		} catch (Exception e) {
			System.err.println("Error while reading in XML file");
			return false;
		}

		myLoadConfiguration.setInitialBoard(xmlFile);
		
		return ApplicationModel.getInstance().initialize(inMessage.contents.getFirstChild());
	}
	
	public boolean initialize(GameConfiguration newLoadConfiguration) {
		terminate = false;
		myLoadConfiguration = newLoadConfiguration;
		turnCounter = 0;
		loggedEvents = new ArrayList<String>();
		if (myLoadConfiguration.randomlyGenerateWorld) {
			AIModel playerAI;
			AIModel redAI;
			AIModel blueAI;
			int boardWidth = (int) (getScreenWorkingWidth() * 0.8 / Constants.baseImageSize);
			int boardHeight = (int) (getScreenWorkingHeight() * 0.8 / Constants.baseImageSize);
			
			playerAI = GameConfiguration.getAIModel(myLoadConfiguration.playerAIModel);
			
			if (myLoadConfiguration.hasRedGhost) 
				redAI =  GameConfiguration.getAIModel(myLoadConfiguration.redGhostAIModel);
			else
				redAI = null;
			
			if (myLoadConfiguration.hasBlueGhost) 
				blueAI =  GameConfiguration.getAIModel(myLoadConfiguration.blueGhostAIModel);
			else
				blueAI = null;
			
			if(!ApplicationModel.getInstance().initialize(boardWidth, boardHeight, playerAI, redAI, blueAI)) {
				JOptionPane.showMessageDialog(null, "Error while initializating the base Application Model.  Exiting.");
				System.exit(0);		
			}

			if (myLoadConfiguration.hasInternalWalls) 
				ApplicationModel.getInstance().myBoard.generateInternalWalls();
			
			writeInitialLog();
		}
		else {
			if (!loadFromXMLFile(myLoadConfiguration.preexistingBoard))
				return false;
		}
		
		if (myLoadConfiguration.isHumanControlled)
			myTimeKeeper = new GamePlayTimeKeeper(PlayRate.HUMANPLAYER);
		else {
			myTimeKeeper = new GamePlayTimeKeeper(PlayRate.AIAUTOMATION);
			
			if (myLoadConfiguration.onAutoRepeat) {
				myTimeKeeper.alterDelayBetweenTurns(2000);
				myTimeKeeper.setPause(false);
			}
		}
		
		if (myLoadConfiguration.deterministicWorld) {
			PhysicsEngine.deterministicMovement = true;
			PhysicsEngine.movementModifier = new int[] { 100, 0, 0, 0, 0, 0, 0, 0, 0 };
		}
		else {
			PhysicsEngine.deterministicMovement = false;
			PhysicsEngine.movementModifier = myLoadConfiguration.nonDeterministicMovement;			
		}
		

		GameObjectPlayer myPlayer = ApplicationModel.getInstance().myPlayer;
		myPlayer.currentAction = new ActionMove(myPlayer.myLocation, myPlayer, PolicyMove.NOWHERE);
		
		myGameView = GameView.STANDARD;
		if (renderTimer == null)
			renderTimer = new Timer();
		startGraphicTimer();
		
		ApplicationView.getInstance().displayMessage("Starting Game.  Current State - PAUSED");

		resettingGame.set(false);
		return true;
	}
	
	public void renderGraphics() {
		
		if (myLoadConfiguration.fullValidationRun && !showScreenInValidationMode)
			ApplicationView.getInstance().renderGraphics(null);
		else
			ApplicationView.getInstance().renderGraphics(ApplicationModel.getInstance().buildPrintList());
	}
	
	public void receiveKeyInput(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_NUMPAD1:
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_NUMPAD3:
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_NUMPAD7:
			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_NUMPAD9:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_LEFT:
				moveCursor(e);
				break;
				
			case KeyEvent.VK_P:
				myTimeKeeper.invertPause();
				break;
				
			case KeyEvent.VK_SPACE:
				myTimeKeeper.stepOneRound = true;
				ApplicationView.getInstance().displayMessage("---Stepping forward one round---");
				break;
				
			case KeyEvent.VK_ADD:
			case KeyEvent.VK_PLUS:
			case '+':
				myTimeKeeper.alterDelayBetweenTurns(20);
				break;
				
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_SUBTRACT:
				myTimeKeeper.alterDelayBetweenTurns(-20);
				break;
				
			case KeyEvent.VK_H:
				String tmpStr = "(H) help, (P) pause, (V) view detailed AI, (B) more detailed AI, (N) view information zones, " +
						" (+) increase game speed, (-) decrease game speed, (space) step forward one round, (arrow keys) move character, " +
						"(A) abort game with unspecified error, (S) abort game due to unwinnable, (D) abort game due to error in game";
				ApplicationView.getInstance().displayMessage(tmpStr);
				break;
				
			case KeyEvent.VK_N:
				myTimeKeeper.setPause(true);
				ApplicationView.getInstance().displayMessage("Rotating Information Zones");
				if (myGameView == GameView.INFORMATIONZONE)
					myGameView = GameView.STANDARD;
				else
					myGameView = GameView.INFORMATIONZONE;
				break;
				
			case KeyEvent.VK_R:
				resettingGame.set(true);
				myTimeKeeper.setPause(true);
				ApplicationModel.getInstance().resetModel();
				ApplicationView.getInstance().resetView();
				ApplicationView.getInstance().displayMessage("---Game Reset Command Received---");
				if (!initialize(myLoadConfiguration)) {
					System.err.println("Error while reseting environment");
					System.exit(-1);
				}
				break;
				
			case KeyEvent.VK_A:
				ApplicationView.getInstance().displayMessage("Manually aborting game for undefined reason");
				finishGame("unknown reason");
				break;
			
			case KeyEvent.VK_S:
				finishGame("unwinnable");
				ApplicationView.getInstance().displayMessage("Manually aborting game because it has been deemed unwinnable");
				break;
				
			case KeyEvent.VK_D:
				ApplicationView.getInstance().displayMessage("Manually aborting game due to an error in the game");
				finishGame("error in game");
				break;
				
			case KeyEvent.VK_V:
				myTimeKeeper.setPause(true);
				ApplicationView.getInstance().displayMessage("Rotating Detailed AI View");
				if (myGameView == GameView.UTILITY)
					myGameView = GameView.STANDARD;
				else
					myGameView = GameView.UTILITY;
				break;
				
			case KeyEvent.VK_C:
				showScreenInValidationMode = !showScreenInValidationMode;
				break;
				
			case KeyEvent.VK_B:
				myTimeKeeper.setPause(true);
				ApplicationView.getInstance().displayMessage("Rotating Policy AI View");
				if (myGameView == GameView.POLICY)
					myGameView = GameView.STANDARD;
				else
					myGameView = GameView.POLICY;
				break;
		
			default: 
				String newMessage = new String("Error, unrecognized command: " + e.getKeyChar());
				ApplicationView.getInstance().displayMessage(newMessage);
				break;
		}
	}
	
	private void writeInitialLog() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
		Date date = new Date();
		myLoadConfiguration.setInitialBoard("Board_" + dateFormat.format(date) + ".xml");
		
		try {
			BufferedWriter myOut = new BufferedWriter(new FileWriter("logs" + Constants.fileDelimiter + myLoadConfiguration.preexistingBoard));
			myOut.write("<Game automatePlayer='" + (!myLoadConfiguration.isHumanControlled) + "' " +
					"deterministic='" + myLoadConfiguration.deterministicWorld + "' " +
					"visible='" + myLoadConfiguration.visibleWorld + "' " +
					"informationZones='" + myLoadConfiguration.informativeZones + "' " +
					"ghostKills='" + myLoadConfiguration.killOnGhostTouch + "'>" + Constants.newline);
			ApplicationModel.getInstance().writeToXMLFile(myOut);
			myOut.write("</Game>");
			myOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while writing to log file: " + myLoadConfiguration.preexistingBoard);
			System.exit(-1);
		}
	}

	public void finishGame(String terminateReason) {
		this.terminateReason = terminateReason;
		terminate = true;
	}
	
	private void writeOutRunLog() {
		renderTask.cancel();
		myTimeKeeper.setGameOver();
		terminate = false;
		
		if (ApplicationModel.getInstance().myPlayer.myAIModel instanceof AIModelLearning) {
			AIModelLearning tempAI = (AIModelLearning) ApplicationModel.getInstance().myPlayer.myAIModel;
			myLoadConfiguration.myLearningObject = tempAI.createLearningObject();
		}

		String runDir = myLoadConfiguration.preexistingBoard.replace(".xml", "");
		String runLog = "Run_";
		
		int counter = 0;
	    final File folder = new File("logs" + Constants.fileDelimiter + runDir + Constants.fileDelimiter);
	    folder.mkdir();
	    for (final File fileEntry : folder.listFiles())
	        if (!(fileEntry.isDirectory()))
	        	if (fileEntry.getName().contains(runLog))
	        		counter++;
		
	    counter++;
		runLog += counter + ".xml";
		GameObjectPlayer myPlayer = ApplicationModel.getInstance().myPlayer;
		try {
			BufferedWriter myOut = new BufferedWriter(new FileWriter("logs" + Constants.fileDelimiter + runDir + Constants.fileDelimiter + runLog));
			myOut.write("<GameRun Board='" + myLoadConfiguration.preexistingBoard + "' run='" + counter + "' ");
			if (terminateReason.isEmpty())
				myOut.write("finish='success' ");
			else
				myOut.write("finish='abort' reason='" + terminateReason + "' ");

			myOut.write("availableBerries='" + ApplicationModel.getInstance().myBoard.startingBerries + "' " +
				"acquiredBerries='" + myPlayer.berriesPickedUp + "' " +
				"finalScore='" + myPlayer.getPointsGained() + "' " +
				"ghostTouches='" + myPlayer.touchedByGhost  + "' " +
				"pitFalls='" + myPlayer.pitFalls + "' " + 
				"playerAI='" + myPlayer.myAIModel.getClass() + "' ");
			
			if (ApplicationModel.getInstance().redGhost != null)
				myOut.write("redGhostAI='" + ApplicationModel.getInstance().redGhost.myAIModel.getClass() + "' ");
			if (ApplicationModel.getInstance().blueGhost != null)
				myOut.write("blueGhostAI='" + ApplicationModel.getInstance().blueGhost.myAIModel.getClass() + "' ");

			myOut.write("automatePlayer='" + (!myLoadConfiguration.isHumanControlled) + "' " +
					"deterministic='" + myLoadConfiguration.deterministicWorld + "' " +
					"visible='" + myLoadConfiguration.visibleWorld + "' " +
					"informationZones='" + myLoadConfiguration.informativeZones + "' " +
					"ghostKills='" + myLoadConfiguration.killOnGhostTouch + "' " +
					"stepsTaken='" + myPlayer.stepsTaken + "' ");
			
			if (myLoadConfiguration.myLearningObject != null)
				myOut.write("learningRate='" + myLoadConfiguration.myLearningObject.learningRate + "' explorationRate='" + myLoadConfiguration.myLearningObject.explorationRate + "' ");
			
			if (myLoadConfiguration.fullValidationRun)
				myOut.write("Set='" + myLoadConfiguration.getSetCardinal() + "' Repetitions='" + myLoadConfiguration.getRepetitionCardinal() + "' ");
					
			myOut.write("wallCollisions='" + myPlayer.wallCollisions + "'>" + Constants.newline);
			
			for (int i = 0; i < loggedEvents.size(); i++)
				myOut.write(loggedEvents.get(i) + Constants.newline);
			myOut.write("</GameRun>");
			myOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while writing to log file: " + myLoadConfiguration.preexistingBoard);
			System.exit(-1);
		}
		loggedEvents.clear();
		
		myLoadConfiguration.rotateNextSetting();
			
		if (!myLoadConfiguration.getIsDone()) {
			resettingGame.set(true);
			ApplicationModel.getInstance().resetModel();
			ApplicationView.getInstance().resetView();
			ApplicationView.getInstance().displayMessage("---Automatic Reset---");
			
			if (myLoadConfiguration.fullValidationRun)
				ApplicationView.getInstance().displaySystemMessage(myLoadConfiguration.describeState());
				
			if (!initialize(myLoadConfiguration)) {
				System.err.println("Error while reseting environment");
				System.exit(-1);
			}
			if (ApplicationModel.getInstance().myPlayer.myAIModel instanceof AIModelLearning && myLoadConfiguration.myLearningObject != null) {
				AIModelLearning tempAI = (AIModelLearning) ApplicationModel.getInstance().myPlayer.myAIModel;
				tempAI.setLearningObject(myLoadConfiguration.myLearningObject);
			}
		}
	}

	private void moveCursor(KeyEvent e) {
		ApplicationModel myModel = ApplicationModel.getInstance();
		Point myLocation = new Point(myModel.myPlayer.myLocation);
		PolicyMove myPolicy = PolicyMove.UNKNOWN;
		
		if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
			myLocation.x--;
			myLocation.y++;
			myPolicy = PolicyMove.DOWNLEFT;
		}
		else if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
			myLocation.x++;
			myLocation.y++;
			myPolicy = PolicyMove.DOWNRIGHT;
		}
		else if (e.getKeyCode() == KeyEvent.VK_NUMPAD9) {
			myLocation.x++;
			myLocation.y--;
			myPolicy = PolicyMove.UPRIGHT;
		}
		else if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
			myLocation.x--;
			myLocation.y--;
			myPolicy = PolicyMove.UPLEFT;
		}
		else if (e.getKeyCode() == KeyEvent.VK_NUMPAD4 || e.getKeyCode() == KeyEvent.VK_LEFT) { //left
			myLocation.x--;
			myPolicy = PolicyMove.LEFT;
		}
		else if (e.getKeyCode() == KeyEvent.VK_NUMPAD6 || e.getKeyCode() == KeyEvent.VK_RIGHT) { //right
			myLocation.x++;
			myPolicy = PolicyMove.RIGHT;
		}
		else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8 || e.getKeyCode() == KeyEvent.VK_UP) { //up
			myLocation.y--;
			myPolicy = PolicyMove.UP;
		}
		else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2 || e.getKeyCode() == KeyEvent.VK_DOWN) { //down
			myLocation.y++;
			myPolicy = PolicyMove.DOWN;
		}
		
		ActionMove newAction = new ActionMove(myLocation, myModel.myPlayer, myPolicy);
		myModel.myPlayer.currentAction = newAction;
	}

	public synchronized void processAIPhase() {
		if (ApplicationModel.getInstance().myPlayer != null) {
			if (ApplicationModel.getInstance().myPlayer.currentAction == null) {
				if (ApplicationModel.getInstance().myPlayer.stepsTaken > 1000) {
					ApplicationView.getInstance().displayMessage("Maximum number of moves exceeded, game exiting with failure");
					finishGame("error, exceeded maximum number of moves");
				}
				ApplicationModel.getInstance().myPlayer.planNextMove();
			}
		}
		
		if (ApplicationModel.getInstance().redGhost != null) {
			if (ApplicationModel.getInstance().redGhost.currentAction == null) {
				ApplicationModel.getInstance().redGhost.planNextMove();
			}
		}

		if (ApplicationModel.getInstance().blueGhost != null) {
			if (ApplicationModel.getInstance().blueGhost.currentAction == null) {
				ApplicationModel.getInstance().blueGhost.planNextMove();
			}
		}
	}
	
	public static int getScreenWorkingWidth() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	public static int getScreenWorkingHeight() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}
	
	/*
	 * Actions are intended before they are carried out.  This step processes all intended actions
	 */
	public synchronized void processActions() {
		ApplicationModel myModel = ApplicationModel.getInstance();
		
		turnCounter++;
		
		if (turnCounter > 1000) {
			ApplicationView.getInstance().displayMessage("Maximum number of moves exceeded, game exiting with failure");
			finishGame("error, exceeded maximum number of moves");
			return;
		}
		
		if (myModel.myPlayer != null) {
			if (myModel.myPlayer.currentAction != null) {
				myModel.myPlayer.currentAction.processAction();
				
				if (myModel.myPlayer.currentAction.getIsDone()) 
					myModel.myPlayer.currentAction = null;
			}
		}		
		
		if (myModel.redGhost != null) {
			if (myModel.redGhost.currentAction != null) {
				myModel.redGhost.currentAction.processAction();
				
				if (myModel.redGhost.currentAction.getIsDone()) 
					myModel.redGhost.currentAction = null;
			}
		}
		
		
		if (myModel.blueGhost != null) {
			if (myModel.blueGhost.currentAction != null) {
				myModel.blueGhost.currentAction.processAction();
				
				if (myModel.blueGhost.currentAction.getIsDone()) 
					myModel.blueGhost.currentAction = null;
			}
		}
	}
	
	public synchronized void processEvents() {
		while (!currentEvents.empty()) {
			currentEvents.pop().processEvent();
		}
		
		return;
	}
	
	public void startGraphicTimer() {
		if (renderTask != null) {
			renderTask.cancel();
		}

		threadInProcess.set(false);
		renderTask = new TimerTask() {
			@Override
			public void run() {
				if (threadInProcess.get())
					return;
				
				threadInProcess.set(true);
				if (!resettingGame.get()) {
					renderGraphics();
					if (myTimeKeeper.isTimeForTurn()) {
						processActions();
						processEvents();
						processAIPhase();
					}
				}
				threadInProcess.set(false);
				
				if (terminate)
					writeOutRunLog();
			}
		};
		renderTimer.schedule(renderTask, 0, 16);
	}

	/**
	 * Stops the rendering cycle so that the application can close gracefully.
	 */
	public void stop() {
		renderTask.cancel();
	}
}
