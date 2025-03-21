package whistle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class NeoWhistlePanel extends JPanel {
    private JFrame frame;
    private JTextArea userConsole;
    private JTextArea systemConsole;
    private JTextField userInput;
    private PipedInputStream pipedIn;
    private PipedOutputStream pipedOut;
    private boolean running;
    private NeoWhistleTask whistleTask;
    private Thread thread;
    public NeoWhistlePanel(JFrame frame) {
        this.frame = frame;
        this.running = false;
        setLayout(new BorderLayout());

        // User Input Console
        userConsole = new JTextArea(15, 20);
        userConsole.setEditable(false);
        JScrollPane userScrollPane = new JScrollPane(userConsole);
        userConsole.setLineWrap(true);
        userConsole.setWrapStyleWord(true);
        userConsole.setMargin(new Insets(5, 5, 5, 5));
        
        // System Message Console
        systemConsole = new JTextArea(15, 20);
        systemConsole.setEditable(false);
        JScrollPane systemScrollPane = new JScrollPane(systemConsole);
        systemConsole.setLineWrap(true);
        systemConsole.setWrapStyleWord(true);
        systemConsole.setMargin(new Insets(5, 5, 5, 5));
        
        try {
            pipedIn = new PipedInputStream();
            pipedOut = new PipedOutputStream(pipedIn);
            System.setIn(pipedIn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // User Input Panel
        userInput = new JTextField();
        userInput.setEnabled(running);
        
        JPanel userInputPanel = new JPanel(new BorderLayout());
        
        userInputPanel.add(userScrollPane,BorderLayout.CENTER);
        userInputPanel.add(userInput,BorderLayout.SOUTH);
        
        // Side-by-side console panel
        JPanel consolePanel = new JPanel(new GridLayout(1, 2, 5, 0));
        consolePanel.add(userInputPanel);
        consolePanel.add(systemScrollPane);
        
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernamelbl = new JLabel("username");
        JTextField usernameField = new JTextField("pmambo",7);
        JLabel passwordlbl = new JLabel("password");
        JPasswordField passwordField = new JPasswordField("4292",5);
        
        JCheckBox autoSequencecb = new JCheckBox("Auto sequence",true);
        
        JButton startButton = new JButton("");
        startButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PLAY,12,Color.GREEN));
        startButton.setBackground(Color.white);
        startButton.setPreferredSize(new Dimension(35, 20));
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
            	if(!running)
            	{
	            	if(usernameField.getText().length() > 0)
	         		 {
	         			 if(passwordField.getText().length() > 0)
	     				 {
	             			 String username = usernameField.getText();
	             			 String password = passwordField.getText();
	             			 whistleTask = new NeoWhistleTask(username,password,autoSequencecb.isSelected(),userConsole,systemConsole);
	             			 thread = new Thread(whistleTask);
	             	         thread.start();
	             	         running = true;
	                         autoSequencecb.setEnabled(false);
	                         startButton.setIcon(IconFontSwing.buildIcon(FontAwesome.SQUARE,12,Color.RED));
		        		 } else {
		                       JOptionPane.showMessageDialog(frame, "Missing password", "Error", JOptionPane.ERROR_MESSAGE);
		         		 }
	         		 } else {
	                      JOptionPane.showMessageDialog(frame, "Missing username", "Error", JOptionPane.ERROR_MESSAGE);
	         		 }
            	} else {
                    startButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PLAY,12,Color.GREEN));
                    autoSequencecb.setEnabled(true);
        	        running = false;
            		try {
						whistleTask.stop();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            	 Timer timer = new Timer(300, new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent evt) {
                         startButton.setEnabled(true); // Re-enable the button
                     }
                 });
                 timer.setRepeats(false); // Make sure the timer only runs once
                 timer.start(); // Start the timer
                userInput.setEnabled(running);
            }
        });
        
        loginPanel.add(usernamelbl);
        loginPanel.add(usernameField);
        loginPanel.add(passwordlbl);
        loginPanel.add(passwordField);
        loginPanel.add(autoSequencecb);
        loginPanel.add(startButton);
        
        add(consolePanel, BorderLayout.SOUTH);
        add(loginPanel, BorderLayout.NORTH);
        
        // Press Enter to execute command
        userInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendUserInput();
            }
        });
    }

    private void sendUserInput() {
        String input = userInput.getText().trim();
        if (!input.isEmpty()) {
            try {
                pipedOut.write((input + "\n").getBytes());
                pipedOut.flush();
                userConsole.append(input + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            userInput.setText("");
        }
    }
}