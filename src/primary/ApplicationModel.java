package primary;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.w3c.dom.Node;

import aiModels.*;

import view.ApplicationView;
import view.PrintListNode;

import gameObjects.*;

/**
 * Controls all aspects of the Model objects
 *
 */
public class ApplicationModel {
	public Board myBoard;
	public GameObjectPlayer myPlayer;
	public GameObjectCreature redGhost;
	public GameObjectCreature blueGhost;
	static ApplicationModel thisModel = null;
	
	private ApplicationModel() {
		
	}
	
	public boolean initialize(int width, int height, AIModel playerAI, AIModel redAI, AIModel blueAI) {
		myPlayer = new GameObjectPlayer(playerAI);
		myPlayer.name = "Pac-man";
		
		if (redAI != null) {
			redGhost = new GameObjectCreature(redAI);
			redGhost.name = "Red-Ghost";
			redGhost.setXY(10, 10);
		}
		if (blueAI != null) {
			blueGhost = new GameObjectCreature(blueAI);
			blueGhost.name = "Blue-Ghost";
			blueGhost.setXY(15, 15);
		}

		loadTemplates();

		myBoard = new Board(width, height);
		myPlayer.setXY(5, 5);
		myBoard.generateRandomMap();
		
		if (redGhost != null)
			redGhost.myAIModel.setInitialValues(true);
		if (blueGhost != null)
			blueGhost.myAIModel.setInitialValues(true);
		myPlayer.myAIModel.setInitialValues(ApplicationController.getInstance().myLoadConfiguration.visibleWorld);
		return true;
	}
	
	public void resetModel() {
		myPlayer.myAIModel = null;
		myPlayer = null;
		if (redGhost != null) {
			redGhost.myAIModel = null;
			redGhost = null;
		}
		if (blueGhost != null) {
			blueGhost.myAIModel = null;
			blueGhost = null;
		}
		myBoard.clearBoard();
		myBoard = null;
		thisModel = null;
	}

	public boolean initialize(Node inMessage) {
		Node localNode = inMessage.getFirstChild();
		AIModel readModel;
		GameConfiguration myConfig = ApplicationController.getInstance().myLoadConfiguration;
		
		readModel = GameConfiguration.getAIModel(myConfig.playerAIModel);
		myPlayer = new GameObjectPlayer(readModel);
		myPlayer.name = "Pac-man";
		myPlayer.setXY(5, 5);
		
		if (myConfig.hasRedGhost) {
			readModel = GameConfiguration.getAIModel(myConfig.redGhostAIModel);
			redGhost = new GameObjectCreature(readModel);
			redGhost.name = "Red-Ghost";
			redGhost.setXY(10, 10);
		}
		
		if (myConfig.hasBlueGhost) {
			readModel = GameConfiguration.getAIModel(myConfig.blueGhostAIModel);
			blueGhost = new GameObjectCreature(readModel);
			blueGhost.name = "Blue-Ghost";
			blueGhost.setXY(15, 15);
		}
		
		
		for (int i = 0; i < inMessage.getChildNodes().getLength(); i++) {
			localNode = inMessage.getChildNodes().item(i);
			if (inMessage.getChildNodes().item(i).getLocalName().equals("Board")) {
				loadTemplates();
				myBoard = new Board(localNode);
			}
		}
		
		if (redGhost != null)
			redGhost.myAIModel.setInitialValues(true);
		if (blueGhost != null)
			blueGhost.myAIModel.setInitialValues(true);
		myPlayer.myAIModel.setInitialValues(ApplicationController.getInstance().myLoadConfiguration.visibleWorld);
		return true;
	}

	private void loadTemplates() {
		try {
			BufferedImage imgBasePlayer = ImageIO.read(new File("images" + Constants.fileDelimiter + "Pacman.bmp"));
			myPlayer.setGraphics(ApplicationView.convertImageToLocalSettings(imgBasePlayer));
			
			if (redGhost != null) {
				BufferedImage imgBaseRedGhost = ImageIO.read(new File("images" + Constants.fileDelimiter + "RedGhost.bmp"));
				redGhost.setGraphics(ApplicationView.convertImageToLocalSettings(imgBaseRedGhost));
			}
			
			if (blueGhost != null) {
				BufferedImage imgBaseBlueGhost = ImageIO.read(new File("images" + Constants.fileDelimiter + "BlueGhost.bmp"));
				blueGhost.setGraphics(ApplicationView.convertImageToLocalSettings(imgBaseBlueGhost));
			}
		}
		catch (Exception e) {
			System.err.println("Error while creating a character");
			System.exit(-1);
		}
	}
	
	public static ApplicationModel getInstance() {
		if (thisModel == null)
			thisModel = new ApplicationModel();
		
		return thisModel;
	}
	
	public PrintListNode[][] buildPrintList() {
		if (myBoard == null) 
			return null;
		
		int height = myBoard.height;
		int width = myBoard.width;

		PrintListNode[][] printList = new PrintListNode[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (myPlayer.myAIModel.visibleSquares[y][x]) {
					if ((x == myPlayer.myLocation.x) && (y == myPlayer.myLocation.y))
						printList[y][x] = myPlayer.generateDisplayNode();
					else if(redGhost != null && (x == redGhost.myLocation.x && y == redGhost.myLocation.y))
						printList[y][x] = redGhost.generateDisplayNode();
					else if(blueGhost != null && (x == blueGhost.myLocation.x && y == blueGhost.myLocation.y))
						printList[y][x] = blueGhost.generateDisplayNode();
					else {
						printList[y][x] = myBoard.myGO[y][x].generateDisplayNode();
	
						ArrayList<GameObjectToken> tempList = myBoard.myTokens;
						for (int i = 0; i < tempList.size(); i++) {
							if (tempList.get(i).myLocation.equals(new Point(x, y)))
								printList[y][x] = tempList.get(i).generateDisplayNode();
						}
					}
				}
				else
					printList[y][x] = myBoard.templateBoundaryHidden.generateDisplayNode();
			}
		}
		
		if (myPlayer != null && ApplicationController.getInstance().myGameView == ApplicationController.GameView.UTILITY) {
			if (myPlayer.myAIModel != null) 
				myPlayer.myAIModel.setAdvancedView(printList);
		}
		
		if (myPlayer != null && ApplicationController.getInstance().myGameView == ApplicationController.GameView.POLICY) {
			if (myPlayer.myAIModel != null) 
				myPlayer.myAIModel.setPolicyView(printList);
		}

		return printList;
	}
	
	/*
	 * TODO repair this hack
	 * This is a hack for several reasons.  First, there should be a way to get the underlying object, regardless of whether a token, 
	 * player or ghost is on top of it.  Next, there should be a function that gets the topmost object, which would grab the
	 * creature/token at that time.  Finally, it should incorporate hidden maps.  
	 * 
	 */
	public GameObject findGOByLocation(Point targetLocation) {
		if (myBoard == null)
			return null;
		
		if ((targetLocation.x >= 0) && (targetLocation.x < myBoard.width) && (targetLocation.y >= 0) && (targetLocation.y < myBoard.height)) {
			if (myPlayer.myLocation.x == targetLocation.x && myPlayer.myLocation.y == targetLocation.y)
				return myPlayer;
			else {
				ArrayList<GameObjectToken> tempList = myBoard.myTokens;
				for (int i = 0; i < tempList.size(); i++) {
					if (tempList.get(i).myLocation.equals(targetLocation))
						return tempList.get(i);
				}
				
				return myBoard.myGO[targetLocation.y][targetLocation.x];
			}
		}
		
		return null;
	}
	
	public void writeToXMLFile(BufferedWriter outWR) {
		try {
			outWR.write("<Model>" + Constants.newline);
			outWR.write("<Player name='" + myPlayer.name + "' x='" + myPlayer.myLocation.x + "' y='" + myPlayer.myLocation.y + 
					"' AIModel='" + myPlayer.myAIModel.getClass().toString() + "'/>" + Constants.newline);
			if (redGhost != null)
				outWR.write("<RedGhost name='" + redGhost.name + "' x='" + redGhost.myLocation.x + "' y='" + redGhost.myLocation.y + 
						"' AIModel='" + redGhost.myAIModel.getClass().toString() + "'/>" + Constants.newline);

				if (blueGhost != null) 
				outWR.write("<BlueGhost name='" + blueGhost.name + "' x='" + blueGhost.myLocation.x + "' y='" + blueGhost.myLocation.y + 
						"' AIModel='" + blueGhost.myAIModel.getClass().toString() + "'/>" + Constants.newline);

			myBoard.writeToXMLFile(outWR);
			outWR.write("</Model>" + Constants.newline);
		} catch (IOException e) {
			System.out.println("Error, cannot write to log file");
			System.exit(0);
		}
	}
	
}
