package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import primary.Constants;
import primary.GameConfiguration;
import primary.MainApplication;
import xml.Message;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.w3c.dom.Node;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JTextField;

public class ShowStartupScreen extends JFrame {
	static final long serialVersionUID = 1L;
	JPanel contentPane;
	JButton btnStartContoller;
	JButton btnExit;
	ImageIcon strawberry;
	ImageIcon blueGuy;
	ImageIcon orangeGuy;
	ImageIcon logo;
	JLabel pastRunsLabel;
	PastRunsJPanel myPastRunsJPanel;
	JCheckBox chckbxInteriorWalls, chckbxFullyVisibleWorld, chckbxRedGhost, chckbxBlueGhost, chckbxDeterministicMove, chckbxGhostKills, chckbxCompleteValidtionRun;
	JComboBox redGhostAICombo, blueGhostAICombo, playerAICombo;
	private JTextField repeatCounterTextField;

	public ShowStartupScreen() {
		//creates the JFrame and contentPane to hold everything
		setTitle("AI Final Project");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(800, 500);
		//the sizes are all relative, so don't resize it
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//the green matches the logo nicely
		contentPane.setBackground(Color.GREEN);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		//this centers the frame on the screen
		setLocationRelativeTo(null);
	    
		//displays the logo
	    logo = new ImageIcon("images" + Constants.fileDelimiter + "AI_Logo.png");
	    JLabel imageContainer = new JLabel(logo);
	    imageContainer.setBounds(0, 20, 800, 200);
	    getContentPane().add(imageContainer);
	    
	    //displays a few berries to look cute
	    strawberry = new ImageIcon("images" + Constants.fileDelimiter + "berry.png");
	    
	    JLabel berry3 = new JLabel(strawberry);
	    berry3.setBounds(500, 313, 50, 50);
	    getContentPane().add(berry3);
	    
	    //displays the blue ghost for fun
	    blueGuy = new ImageIcon("images" + Constants.fileDelimiter + "blue.png");
	    JLabel blue = new JLabel(blueGuy);
	    blue.setBounds(30, 50, 50, 50);
	    getContentPane().add(blue);
	    
	    //displays the orange ghost for fun
	    orangeGuy = new ImageIcon("images" + Constants.fileDelimiter + "orange.png");
	    JLabel orange = new JLabel(orangeGuy);
	    orange.setBounds(700, 50, 50, 50);
	    getContentPane().add(orange);
	    
	    //adds a button to use the AI model for the player
	    btnStartContoller = new JButton("Start");
	    btnStartContoller.setBounds(335, 289, 130, 50);
	    getContentPane().add(btnStartContoller);
	    
	    //add a button so the user can exit
	    btnExit = new JButton("Exit");
	    btnExit.setBounds(335, 350, 130, 50);
	    getContentPane().add(btnExit);
	    
	    
	    //The past runs document
	    pastRunsLabel = new JLabel("Prior Runs:");
	    pastRunsLabel.setBounds(25, 205, 400, 25);
	    getContentPane().add(pastRunsLabel);
	    myPastRunsJPanel = new PastRunsJPanel(25, 225, 200, 400, this);
	    getContentPane().add(myPastRunsJPanel);
	    	    
	    chckbxInteriorWalls = new JCheckBox("Interior Walls");
	    chckbxInteriorWalls.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    chckbxInteriorWalls.setBounds(584, 341, 116, 23);
	    chckbxInteriorWalls.setSelected(true);
	    contentPane.add(chckbxInteriorWalls);
	    
	    chckbxFullyVisibleWorld = new JCheckBox("Visible World");
	    chckbxFullyVisibleWorld.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    chckbxFullyVisibleWorld.setBounds(584, 315, 116, 23);
	    chckbxFullyVisibleWorld.setSelected(true);
	    contentPane.add(chckbxFullyVisibleWorld);
	    
	    chckbxRedGhost = new JCheckBox("Red Ghost");
	    chckbxRedGhost.setBounds(520, 227, 97, 23);
	    chckbxRedGhost.setSelected(true);
	    contentPane.add(chckbxRedGhost);
	    
	    chckbxBlueGhost = new JCheckBox("Blue Ghost");
	    chckbxBlueGhost.setBounds(520, 257, 97, 23);
	    chckbxBlueGhost.setSelected(true);
	    contentPane.add(chckbxBlueGhost);
	    
	    chckbxDeterministicMove = new JCheckBox("Deterministic Move");
	    chckbxDeterministicMove.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    chckbxDeterministicMove.setBounds(584, 289, 116, 23);
	    chckbxDeterministicMove.setSelected(true);
	    contentPane.add(chckbxDeterministicMove);
	    
	    chckbxGhostKills = new JCheckBox("Ghost Kills");
	    chckbxGhostKills.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    chckbxGhostKills.setBounds(584, 367, 116, 23);
	    contentPane.add(chckbxGhostKills);
	    
	    redGhostAICombo = new JComboBox();
	    redGhostAICombo.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    redGhostAICombo.setBounds(623, 228, 145, 20);
	    redGhostAICombo.addItem("AIModelPlayer");
	    redGhostAICombo.addItem("AIModelBlindlyForward");
	    redGhostAICombo.addItem("AIModelDirectMove");
	    redGhostAICombo.addItem("AIModelClosestMove");
	    redGhostAICombo.addItem("AIModelHillClimb");
	    redGhostAICombo.addItem("AIModelBasicUtility");
	    redGhostAICombo.addItem("AIModelLearning");
	    redGhostAICombo.setSelectedIndex(2);
	    contentPane.add(redGhostAICombo);
	    
	    blueGhostAICombo = new JComboBox();
	    blueGhostAICombo.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    blueGhostAICombo.setBounds(623, 258, 145, 20);
	    blueGhostAICombo.addItem("AIModelPlayer");
	    blueGhostAICombo.addItem("AIModelBlindlyForward");
	    blueGhostAICombo.addItem("AIModelDirectMove");
	    blueGhostAICombo.addItem("AIModelClosestMove");
	    blueGhostAICombo.addItem("AIModelHillClimb");
	    blueGhostAICombo.addItem("AIModelBasicUtility");
	    blueGhostAICombo.addItem("AIModelLearning");
	    blueGhostAICombo.setSelectedIndex(1);
	    contentPane.add(blueGhostAICombo);
	    
	    JLabel lblPlayersAi = new JLabel("Players AI Model");
	    lblPlayersAi.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    lblPlayersAi.setBounds(491, 404, 86, 14);
	    contentPane.add(lblPlayersAi);
	    
	    playerAICombo = new JComboBox();
	    playerAICombo.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    playerAICombo.setBounds(584, 401, 145, 20);
	    playerAICombo.addItem("AIModelPlayer");
	    playerAICombo.addItem("AIModelBlindlyForward");
	    playerAICombo.addItem("AIModelDirectMove");
	    playerAICombo.addItem("AIModelClosestMove");
	    playerAICombo.addItem("AIModelHillClimb");
	    playerAICombo.addItem("AIModelBasicUtility");
	    playerAICombo.addItem("AIModelLearning");
	    playerAICombo.setSelectedIndex(6);
	    contentPane.add(playerAICombo);
	    
	    JLabel lblRepeatCounter = new JLabel("Repeat Counter");
	    lblRepeatCounter.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    lblRepeatCounter.setBounds(491, 431, 82, 14);
	    contentPane.add(lblRepeatCounter);
	    
	    repeatCounterTextField = new JTextField();
	    repeatCounterTextField.setText("1");
	    repeatCounterTextField.setBounds(581, 428, 50, 20);
	    contentPane.add(repeatCounterTextField);
	    repeatCounterTextField.setColumns(10);
	    
	    chckbxCompleteValidtionRun = new JCheckBox("Complete Validation Run");
	    chckbxCompleteValidtionRun.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    chckbxCompleteValidtionRun.setBounds(290, 407, 175, 23);
	    contentPane.add(chckbxCompleteValidtionRun);
	    
	    //This starts the player AI model 
	    btnStartContoller.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				dispose();
								
				MainApplication.startGame(getConfiguration());
			}
	    });
	    
	    //exits the application
	    btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				dispose();
				System.exit(0);
			}
	    });
	}
	
	public void resetSettings() {
		if (!Message.configure("logs" + Constants.fileDelimiter + "Board.xsd")) { 
			System.err.println("Error, cannot load Board XSD file");
			System.exit(-1);
		}
				
		Message inMessage = null;
		
		try {
			String inFile = "";
			BufferedReader inBR = new BufferedReader(new FileReader("logs" + Constants.fileDelimiter + myPastRunsJPanel.localList.getSelectedItem()));

			String inSegment = inBR.readLine();
			while (inSegment != null) {
				inFile += inSegment;
				inSegment = inBR.readLine();
			}
			inBR.close();
	
			inMessage = new Message(inFile);
		} catch (Exception e) {
			System.err.println("Error while reading in XML file");
			return;
		}
		Node localNode = inMessage.contents;
		Node cursorNode;
		String readAI;
		chckbxFullyVisibleWorld.setSelected(Boolean.parseBoolean(localNode.getAttributes().getNamedItem("visible").getNodeValue()));
		chckbxDeterministicMove.setSelected(Boolean.parseBoolean(localNode.getAttributes().getNamedItem("deterministic").getNodeValue()));
		chckbxInteriorWalls.setSelected(false);
		chckbxGhostKills.setSelected(Boolean.parseBoolean(localNode.getAttributes().getNamedItem("ghostKills").getNodeValue()));
		// information zones is always true, interior walls in this case is always false because the map is built
		localNode = localNode.getFirstChild();
		chckbxRedGhost.setSelected(false);
		chckbxBlueGhost.setSelected(false);
		for (int i = 0; i < localNode.getChildNodes().getLength(); i++) {
			cursorNode = localNode.getChildNodes().item(i);
			if (cursorNode.getLocalName().equals("Player")) {
				readAI = cursorNode.getAttributes().getNamedItem("AIModel").getNodeValue();
				readAI = readAI.replace("class aiModels.",  "");
				playerAICombo.setSelectedItem(readAI);
			}
			else if (cursorNode.getLocalName().equals("RedGhost")) {
				readAI = cursorNode.getAttributes().getNamedItem("AIModel").getNodeValue();
				readAI = readAI.replace("class aiModels.",  "");
				chckbxRedGhost.setSelected(true);
				redGhostAICombo.setSelectedItem(readAI);
			}
			else if (cursorNode.getLocalName().equals("BlueGhost")) {
				readAI = cursorNode.getAttributes().getNamedItem("AIModel").getNodeValue();
				readAI = readAI.replace("class aiModels.",  "");
				chckbxBlueGhost.setSelected(true);
				blueGhostAICombo.setSelectedItem(readAI);
			}
		}
	}
	
	private GameConfiguration getConfiguration() {
		boolean hasVisibleWorld = chckbxFullyVisibleWorld.isSelected();
		boolean hasDeterministicWorld = chckbxDeterministicMove.isSelected();
		boolean hasInformativeZones = true;
		boolean hasInternalWalls = chckbxInteriorWalls.isSelected();
		boolean hasKills = chckbxGhostKills.isSelected();
		
		GameConfiguration retVal = new GameConfiguration(hasVisibleWorld, hasDeterministicWorld, hasInformativeZones, hasInternalWalls, hasKills);
		
		if (chckbxRedGhost.isSelected())
			retVal.setRedGhostAI("class aiModels." +  (String)redGhostAICombo.getSelectedItem());
		
		if (chckbxBlueGhost.isSelected())
			retVal.setBlueGhostAI("class aiModels." +  (String)blueGhostAICombo.getSelectedItem());
		
		retVal.setPlayerAI("class aiModels." +  (String)playerAICombo.getSelectedItem());
		
		retVal.setInitialBoard(myPastRunsJPanel.localList.getSelectedItem());
		
		retVal.setAutoRepeatCounter(Integer.parseInt(repeatCounterTextField.getText()));
		
		//set this to True for Learning Only validation runs, false for all other validation runs
		if (chckbxCompleteValidtionRun.isSelected())
			retVal.setFullValidationRun(true);
		
		return retVal;
	}
}
