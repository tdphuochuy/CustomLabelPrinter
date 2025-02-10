import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import chatSystem.ChatClient;
import chatSystem.ChatServer;
import config.Config;

public class ChatPanel extends JPanel{
	private JFrame frame;
	private ChatClient clientWS;
    public ChatPanel(JFrame frame,JTabbedPane tabbedPane) throws UnknownHostException, URISyntaxException {
    	this.frame = frame;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setMargin(new Insets(0, 5, 0, 5));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
        	@Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = super.createDecreaseButton(orientation);
                button.setBackground(Color.WHITE);
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
            	JButton button = super.createIncreaseButton(orientation);
                button.setBackground(Color.WHITE);
                return button;
            }

            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
            }
        });
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField();
        textField.setMargin(new Insets(0, 5, 0, 5));
        setPlaceholder(textField,"Message");
       
        inputPanel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        inputPanel.add(sendButton, BorderLayout.EAST);
        sendButton.setBackground(Color.white);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                if (!message.trim().isEmpty() && !message.equals("Message")) {
                	JSONObject obj = new JSONObject();
    				obj.put("type", "message");
    				obj.put("message", message);
    				clientWS.send(obj.toJSONString());
                }
                textField.setText("");
            }
        });
        
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick(); // Simulate clicking the Send button
                }
            }
        });
        
        this.add(scrollPane,BorderLayout.CENTER);
        this.add(inputPanel,BorderLayout.SOUTH);
        
        if(Config.chatServer)
        {
	        ChatServer serverWS = new ChatServer(8887);
	        serverWS.start();
		    clientWS = new ChatClient(new URI("ws://localhost:8887"),textField,textArea,tabbedPane);
		    clientWS.connect();
        } else {
        	clientWS = new ChatClient(new URI("ws://localhost:8887"),textField,textArea,tabbedPane);
		    clientWS.connect();
        }
    }

    
    // Method to set the placeholder text
    public static void setPlaceholder(JTextField textField, String placeholderText) {
        // Set initial placeholder
        textField.setText(placeholderText);
        textField.setForeground(Color.GRAY); // Set the placeholder text color

        // Add FocusListener to manage the prompt behavior
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // When the user clicks on the text field, clear the placeholder if it's still there
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Set the text color to normal when typing
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // When the text field loses focus, reset the placeholder if the field is empty
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholderText);
                    textField.setForeground(Color.GRAY); // Set the placeholder text color back
                }
            }
        });
    }
}