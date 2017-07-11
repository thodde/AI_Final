package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JPanel;

import primary.Constants;

public class PastRunsJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public List localList; 
	private ShowStartupScreen localStartupScreen;

	public PastRunsJPanel(int xlocation, int ylocation, int width, int height, ShowStartupScreen myStart) {
		localStartupScreen = myStart;
		setBounds(xlocation, ylocation, width, height);
	    setLayout(new FlowLayout());
	    setBackground(Color.GREEN);
	    setFont(new Font("Helvetica", Font.PLAIN, 12));
	    localList = new List(10, false);
	    localList.setBounds(xlocation,  ylocation,  width,  height);
	    
	    localList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				localStartupScreen.resetSettings();
			}

			public void mouseEntered(MouseEvent arg0) { ; }
			public void mouseExited(MouseEvent arg0) { ; }
			public void mousePressed(MouseEvent arg0) { ; }
			public void mouseReleased(MouseEvent arg0) { ; }
	    });
				
	    
	    int counter = 0;
	    final File folder = new File("logs" + Constants.fileDelimiter);
	    for (final File fileEntry : folder.listFiles()) {
	        if (!(fileEntry.isDirectory()) && counter < 30) {
	        	if (!(fileEntry.getName().contains(".xsd")) && !(fileEntry.getName().contains("Run")) && fileEntry.getName().contains(".xml")) {
	        		localList.add(fileEntry.getName());
	            counter++;
	        	}
	        }
	    }
	    add(localList);

    	setVisible(true);
	}
}
