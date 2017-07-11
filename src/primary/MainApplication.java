package primary;

import javax.swing.JOptionPane;

import view.ApplicationView;
import view.ShowStartupScreen;

public class MainApplication {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ShowStartupScreen start = new ShowStartupScreen();
		start.setVisible(true);
	}
	
	public static void startGame(GameConfiguration loadConfiguration) {
		
		if (!ApplicationView.getInstance().initializeScreen()) {
			JOptionPane.showMessageDialog(null, "Error while initializating the base Application View.  Exiting.");
			System.exit(0);
		}
				
		if(!ApplicationController.getInstance().initialize(loadConfiguration)) {
			JOptionPane.showMessageDialog(null, "Error while initializating the base Application Controller.  Exiting.");
			System.exit(0);				
		}
		
		//ApplicationController.getInstance().startGraphicTimer();
		ApplicationView.getInstance().displayMessage(new String("Load complete"));
	}
}
