package whistle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.stream.Collectors;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import config.Config;
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
    private JButton startButton;
    private String orderNum;
    public NeoWhistlePanel(JFrame frame) throws ParseException {
        this.frame = frame;
        this.running = false;
        setLayout(new BorderLayout());

        // User Input Console
        userConsole = new JTextArea(16, 22);
        userConsole.setEditable(false);
        JScrollPane userScrollPane = new JScrollPane(userConsole);
        userConsole.setLineWrap(true);
        userConsole.setWrapStyleWord(true);
        userConsole.setMargin(new Insets(5, 5, 5, 5));
        
        // System Message Console
        systemConsole = new JTextArea(16, 22);
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
        
        startButton = new JButton("");
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
	             			 if(orderNum != null)
	             			 {
	             				 whistleTask = new NeoWhistleTask(username,password,autoSequencecb.isSelected(),userConsole,systemConsole,orderNum);
	             			 } else {
	             				 whistleTask = new NeoWhistleTask(username,password,autoSequencecb.isSelected(),userConsole,systemConsole);
	             			 }
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
            	 userInput.requestFocusInWindow();
            }
        });
        
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config/commands.json");
        String content = new BufferedReader(new InputStreamReader(inputStream))
                            .lines()
                            .collect(Collectors.joining("\n"));
    	JSONParser jsonParser = new JSONParser();
	    JSONArray commandsArray = (JSONArray) jsonParser.parse(content);
	    for(Object obj : commandsArray)
	    {
	    	JSONObject jsonObject = (JSONObject) obj;
	    	String key = jsonObject.get("product").toString();
	    	String quantity = jsonObject.get("quantity").toString();
	    	JButton commandbtn = new JButton(key + " (" + quantity + ")");
	    	commandbtn.setBackground(Color.white);
	    	commandPanel.add(commandbtn);
	    	
	    	commandbtn.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                if(whistleTask != null)
	                {
	                	if(whistleTask.isRunning())
	                	{
	                		commandbtn.setEnabled(false);
	                		whistleTask.addCommand(key.toString(), quantity, "1");
	                		
	                		 Timer timer = new Timer(300, new ActionListener() {
	                             @Override
	                             public void actionPerformed(ActionEvent evt) {
	                            	 commandbtn.setEnabled(true); // Re-enable the button
	                             	 userInput.requestFocusInWindow();
	                             }
	                         });
	                         timer.setRepeats(false); // Make sure the timer only runs once
	                         timer.start(); // Start the timer
	                	}
	                }
	            }
	        });
	    }
        
        loginPanel.add(usernamelbl);
        loginPanel.add(usernameField);
        loginPanel.add(passwordlbl);
        loginPanel.add(passwordField);
        loginPanel.add(autoSequencecb);
        loginPanel.add(startButton);
        
        add(consolePanel, BorderLayout.SOUTH);
        add(commandPanel, BorderLayout.CENTER);
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
    
    public void startNeoWhistle(String orderNum)
    {
    	if(!running)
    	{
    		this.orderNum = orderNum;
    		startButton.doClick();
    	}
    }
    
    public void focusInput()
    {
    	SwingUtilities.invokeLater(() -> {
    	    userInput.requestFocusInWindow();
    	});
    }
    
    public void addWhistleButtonCommand(String productCode, String quantity)
    {
    	if(whistleTask != null)
        {
        	if(whistleTask.isRunning())
        	{
        		whistleTask.addCommand(productCode, quantity, "1");
        	} else {
                JOptionPane.showMessageDialog(frame, "Failed executing button command!\nNeo Whistle is not running.", "Alert", JOptionPane.INFORMATION_MESSAGE);
        	}
        } else {
            JOptionPane.showMessageDialog(frame, "Failed executing button command!\nNeo Whistle is not running.", "Alert", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}