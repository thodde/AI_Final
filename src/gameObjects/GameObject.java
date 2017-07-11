package gameObjects;

import java.awt.Color;
import java.awt.image.BufferedImage;

import primary.ApplicationController;
import primary.Point;

import view.PrintListNode;

/**
 *  GameObject is a very generic class to hold anything that the game interacts with.  They are broken down into GameObjectCreature (those capable of
 * 	independent action, GameObjectBackground (objects which cannot move or act) and GameObject.... (something here that indicates a goal)
 */
public class GameObject {
	public static enum GameObjectType { GENERAL, BACKGROUND, CREATURE, PLAYER, TOKEN; }
	
	protected GameObjectType myType;
	private BufferedImage myGraphics; //the actual display object.  Current a 32X32 pixel bitmap image
	public Color baseColor;
	public boolean overrideColor;
	public boolean canBlockMovement;
	public Point myLocation;
	public String name;
	public boolean isBreezy;
	public boolean isPungent;
	
	public GameObject() {
		myGraphics = null;
		overrideColor = false;
		myLocation = new Point(0, 0);
		name = new String("Undefined");
		myType = GameObjectType.GENERAL;
		isBreezy = false;
		isPungent = false;
	}
	
	public GameObjectType getType() { return myType; }
	
	public PrintListNode generateDisplayNode() {
		if (ApplicationController.getInstance().myGameView == ApplicationController.GameView.INFORMATIONZONE) {
			PrintListNode retVal = new PrintListNode(myGraphics, overrideColor, baseColor);
			
			if (isBreezy || isPungent) 
				retVal.setInformationZone(isBreezy,  isPungent);
			return retVal;
		}
		else
			return new PrintListNode(myGraphics, overrideColor, baseColor);
	}
	
	public void setXY(int newX, int newY) {
		myLocation.x = newX;
		myLocation.y = newY;
	}
	
	public void setGraphics(BufferedImage newGraphics) {
		myGraphics = newGraphics;
	}
	
	public GameObject generateClone(GameObject newObject) {
		GameObject tempObject;
		if (newObject == null)
			tempObject = new GameObject();
		else
			tempObject = newObject;
		
		tempObject.canBlockMovement = canBlockMovement;
		tempObject.name = new String(name);
		tempObject.setGraphics(myGraphics);
		tempObject.baseColor = baseColor;
		tempObject.overrideColor = overrideColor;
		tempObject.setXY(myLocation.x, myLocation.y);
		
		return tempObject;
	}
	
}
