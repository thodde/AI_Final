package view;

import gameObjects.GameObjectCreature;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import primary.ApplicationModel;

public class VariableDisplayJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	JTextArea primaryCreatureLabel;
	JTextArea blueGhostLabel;
	JTextArea redGhostLabel;
	private static final String newline = "\n";
	
	public VariableDisplayJPanel(int newLeft, int newTop, int newWidth, int newHeight) {
		super();
		setBounds(newLeft, newTop, newWidth, newHeight);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		primaryCreatureLabel = new JTextArea();
		primaryCreatureLabel.setLineWrap(true);
		primaryCreatureLabel.setWrapStyleWord(true);
		primaryCreatureLabel.setEditable(false);
		primaryCreatureLabel.setVisible(true);
		primaryCreatureLabel.setFocusable(false);
		primaryCreatureLabel.setText("Primary Creature: ");
		add(primaryCreatureLabel);
		
		redGhostLabel = new JTextArea();
		redGhostLabel.setLineWrap(true);
		redGhostLabel.setWrapStyleWord(true);
		redGhostLabel.setEditable(false);
		redGhostLabel.setVisible(true);
		redGhostLabel.setFocusable(false);
		redGhostLabel.setText("Red Ghost");
		add(redGhostLabel);
		
		blueGhostLabel = new JTextArea();
		blueGhostLabel.setLineWrap(true);
		blueGhostLabel.setWrapStyleWord(true);
		blueGhostLabel.setEditable(false);
		blueGhostLabel.setVisible(true);
		blueGhostLabel.setFocusable(false);
		blueGhostLabel.setText("Blue Ghost");
		add(blueGhostLabel);
		
	}
	
	public void updateDisplay() {
		ApplicationModel myModel = ApplicationModel.getInstance();
		GameObjectCreature tmpC = myModel.myPlayer;
		String outLine;
		if(tmpC != null) {
			outLine = "Name: " + tmpC.name + newline + "AI Model: " + tmpC.describeAIState();
			
			if (tmpC.currentAction != null) {
				outLine += newline + "Current Action: " + tmpC.currentAction.describeAction();
			}
			
			primaryCreatureLabel.setText(outLine);
		}
		else
			primaryCreatureLabel.setText("");

		
		if (myModel.redGhost != null) {
			tmpC = myModel.redGhost;
			if(tmpC != null) {
				outLine = "Name: " + tmpC.name + newline + "AI Model: " + tmpC.describeAIState();
				
				if (tmpC.currentAction != null) {
					outLine += newline + "Current Action: " + tmpC.currentAction.describeAction();
				}
				
				redGhostLabel.setText(outLine);
			}
			else
				redGhostLabel.setText("");
		}
		else
			redGhostLabel.setText("");

		
		if (myModel.blueGhost != null) {
			tmpC = myModel.blueGhost;
			if(tmpC != null) {
				outLine = "Name: " + tmpC.name + newline + "AI Model: " + tmpC.describeAIState();
				
				if (tmpC.currentAction != null) {
					outLine += newline + "Current Action: " + tmpC.currentAction.describeAction();
				}
				
				blueGhostLabel.setText(outLine);
			}
			else
				blueGhostLabel.setText("");
		}
		else
			blueGhostLabel.setText("");
	}
}
