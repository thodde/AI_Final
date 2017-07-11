package view;

import java.awt.Color;
import java.awt.image.BufferedImage;

import aiModels.AIModel.PolicyMove;

public class PrintListNode {
	public BufferedImage myImage;
	public Color baseColor;
	public boolean overrideColor;
	public int utilityValue;
	public boolean hasUtilityValue;
	public boolean hasPolicyMove;
	public boolean hasInformationZone;
	public PolicyMove myPolicyMove;
	public boolean isBreezy;
	public boolean isPungent;

	public PrintListNode(BufferedImage newImage, boolean overrideBase, Color newColor) {
		myImage = newImage;
		overrideColor = overrideBase;
		baseColor = newColor;
		hasUtilityValue = false;
		utilityValue = 0;
		hasPolicyMove = false;
		hasInformationZone = false;
		isBreezy = false;
		isPungent = false;
	}
	
	public void setInformationZone(boolean breezy, boolean pungent) {
		hasPolicyMove = false;
		hasUtilityValue = false;
		hasInformationZone = true;
		isBreezy = breezy;
		isPungent = pungent;
		
	}
	
	public void setUtilityValue(int newUtility) {
		hasPolicyMove = false;
		hasInformationZone = false;
		hasUtilityValue = true;
		utilityValue = newUtility;
	}
	
	public void setPolicyValue(PolicyMove newPolicy) {
		hasUtilityValue = false;
		hasInformationZone = false;
		hasPolicyMove = true;
		myPolicyMove = newPolicy;
	}
		
}