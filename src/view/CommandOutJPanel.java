package view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CommandOutJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPane;
	private JTextArea myCommandWindow;
	private String currentLog;
	private static final String newline = "\n";
	
	public CommandOutJPanel(int newLeft, int newTop, int newWidth, int newHeight) {
		super();
		
		setLayout(new BorderLayout());
		setBounds(newLeft, newTop, newWidth, newHeight);
		
		myCommandWindow = new JTextArea();
		myCommandWindow.setLineWrap(true);
		myCommandWindow.setWrapStyleWord(true);
		myCommandWindow.setEditable(false);
		myCommandWindow.setVisible(true);
		myCommandWindow.setFocusable(false);
	    
	    scrollPane = new JScrollPane(myCommandWindow); 
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    add(BorderLayout.CENTER, scrollPane);
	    
	    
	    
	    currentLog = new String("Initialized Log");
	    myCommandWindow.setText(currentLog);
	}

	public void displayMessage(String message) {
		currentLog += newline + message;
		myCommandWindow.append(newline + message);
		myCommandWindow.setCaretPosition(myCommandWindow.getDocument().getLength());
	}

	public void displaySystemMessage(String message) {
		currentLog += newline + message;
		myCommandWindow.append(newline + message);
		myCommandWindow.setCaretPosition(myCommandWindow.getDocument().getLength());
	}
	
	public void clearLog() {
		myCommandWindow.setText("");
	}
}