package view;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import primary.ApplicationController;

/**
 * Manages all aspects of the View
 * @author Andrew and Trevor
 */
public class ApplicationView extends JFrame implements KeyListener, WindowListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private GraphicsCanvas myGraphicCanvas;
	private CommandOutJPanel commandOutArea;
	private VariableDisplayJPanel myVariableDisplay;
	static ApplicationView thisView = null;
	
	ApplicationView() {
		super("FullScreen");
		getContentPane().setPreferredSize( Toolkit.getDefaultToolkit().getScreenSize());
	    setResizable(false);
	    
	    JMenuBar jmb = new JMenuBar();

	    JMenu jmFile = new JMenu("File");
	    JMenuItem jmiReset = new JMenuItem("New Game");
	    JMenuItem jmiExit = new JMenuItem("Exit");
	    jmFile.add(jmiReset);
	    jmFile.addSeparator();
	    jmFile.add(jmiExit);
	    jmb.add(jmFile);

	    JMenu jmHelp = new JMenu("Help");
	    JMenuItem jmiAbout = new JMenuItem("About");
	    jmHelp.add(jmiAbout);
	    jmb.add(jmHelp);

	    //reset the game
	    jmiReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetGame();
			}
	    });
	    
	    //Exit the game
	    jmiExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
	    });
	    
	    //Show info about the game
	    jmiAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "This game was created by:\nAndrew Zafft\nTrevor Hodde\nJames Forkey");
			}
	    });

	    this.setJMenuBar(jmb);
	}
	
	public void resetView() {
		// only clear during regular runs
		if (!ApplicationController.getInstance().myLoadConfiguration.fullValidationRun)
			commandOutArea.clearLog();
	}
	
	//We can reset the game here
	public void resetGame() {
		this.dispose();
		ShowStartupScreen startup = new ShowStartupScreen();
		startup.setVisible(true);
	}
	
	public static ApplicationView getInstance() {
		if (thisView == null)
			thisView = new ApplicationView();
		
		return thisView;
	}
	
	public boolean initializeScreen() {
		addKeyListener(this);
		addWindowListener(this);
		
		setLayout(null);
	    setVisible(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    int width = getScreenWorkingWidth();
		int height = getScreenWorkingHeight();

	    myGraphicCanvas = new GraphicsCanvas(0, 0, (int)(width*0.8), (int)(height*0.8));
	    myGraphicCanvas.setVisible(true);
	    add(myGraphicCanvas);

	    commandOutArea = new CommandOutJPanel(0, (int)(height*0.8), width, height - (int)(height*0.81));
	    add(commandOutArea);
	    
		myVariableDisplay = new VariableDisplayJPanel((int)(width*0.8), 0, width - (int)(width*0.81), (int)(height*0.8));
		add(myVariableDisplay);
		
		
	    
	    pack();
	    repaint();
	    myGraphicCanvas.initialize();

	    return true;
	}

	public void displayMessage(String newMessage){
		// Only display during a regular run, ignore otherwise
		if (!ApplicationController.getInstance().myLoadConfiguration.fullValidationRun)
			commandOutArea.displayMessage(newMessage);
	}

	public void displaySystemMessage(String newMessage){
		commandOutArea.displaySystemMessage(newMessage);
	}
	
	/**
	 * Graphics are defined outside of the drawing cycle.  Instead, the drawing application just writes whatever the current graphic list is at the time
	 *   of calling.
	 * @param printList
	 */
	public void renderGraphics(PrintListNode[][] printList) {
		myVariableDisplay.updateDisplay();
		if (printList == null) 
			return;
		
		myGraphicCanvas.render(printList);
	}	
	
	public static BufferedImage convertImageToLocalSettings(BufferedImage input) {
		GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); 

		// if image is already compatible and optimized for current system settings, simply return it 
		if (input.getColorModel().equals(gfx_config.getColorModel())) 
			return input; 
		
		// image is not optimized, so create a new image that is 
		BufferedImage new_image = gfx_config.createCompatibleImage(input.getWidth(), input.getHeight(), input.getTransparency()); 

		// get the graphics context of the new image to draw the old image on 
		Graphics2D g2d = (Graphics2D) new_image.getGraphics(); 

		// actually draw the image and dispose of context no longer needed 
		g2d.drawImage(input, 0, 0, null); 
		g2d.dispose(); 

		// return the new optimized image 
		return new_image; 
	}

	public void keyPressed(KeyEvent arg0) {
		ApplicationController.getInstance().receiveKeyInput(arg0);
	}

	public void windowClosing(WindowEvent arg0) {
		ApplicationController.getInstance().stop();
		System.exit(0);
	}

	public void windowActivated(WindowEvent arg0) { ; }
	public void windowClosed(WindowEvent arg0) { ; }
	public void windowDeactivated(WindowEvent arg0) { ; }
	public void windowDeiconified(WindowEvent arg0) { ; }
	public void windowIconified(WindowEvent arg0) { ; }
	public void windowOpened(WindowEvent arg0) { ; }
	public void keyReleased(KeyEvent arg0) { ; }
	public void keyTyped(KeyEvent arg0) { ; }
	
	public static int getScreenWorkingWidth() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	public static int getScreenWorkingHeight() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
