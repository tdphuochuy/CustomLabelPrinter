package chatSystem;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
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
import java.text.SimpleDateFormat;
import java.util.Date;

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

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import chatSystem.ChatClient;
import chatSystem.ChatServer;
import config.Config;

public class ChatPanel extends JPanel{
	private JFrame frame;
	private ChatClient clientWS;
	private JTextArea textArea;
	private JTabbedPane tabbedPane;
    public ChatPanel(JFrame frame,JTabbedPane tabbedPane) throws UnknownHostException, URISyntaxException {
    	this.frame = frame;
    	this.tabbedPane = tabbedPane;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        textArea = new JTextArea();
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
	        //ws://167.110.212.94:8887
		    clientWS = new ChatClient(new URI("ws://167.110.212.94:8887"),textField,textArea,tabbedPane,this);
		    clientWS.connect();
        } else {
        	clientWS = new ChatClient(new URI("ws://167.110.212.94:8887"),textField,textArea,tabbedPane,this);
		    clientWS.connect();
        }
    }
    
    public void reconnectWS()
    {
    	new Thread(() -> {
		    try {
		        Thread.sleep(10000); // Optional delay to allow cleanup
		        clientWS.reconnect();
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		}).start();
    }
    
    public void appendChat(String type,String message)
    {
  	  SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(new Date());
  	  textArea.append("[" + currentTime + "] " + message + "\n"); // Append the message
        //setPlaceholder(textField,"Message");
        textArea.setCaretPosition(textArea.getDocument().getLength());
        
        if(type.equals("message"))
		{
		      notifyUser(message);
		}
    }
    
    public void notifyUser(String message)
    {
        int selectedIndex = tabbedPane.getSelectedIndex();
        String selectedTabTitle = tabbedPane.getTitleAt(selectedIndex);
        if(!selectedTabTitle.equals("Chat"))
        {
     	   	tabbedPane.setBackgroundAt(tabbedPane.indexOfTab("Chat"), new Color(255, 192, 105));
        }
  	  
  	  if(!isApplicationFocused())
  	  {
  		  User32 user32 = User32.INSTANCE;
  		  WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, "Custom Label Printer");
  	      
  		      if (hWnd != null) {
  		          WinUser.FLASHWINFO flashInfo = new WinUser.FLASHWINFO();
  		          flashInfo.hWnd = hWnd;
  		          flashInfo.uCount = WinUser.FLASHW_TIMERNOFG; // Flash until window is focused
  		          flashInfo.dwFlags = WinUser.FLASHW_ALL; // Flash title bar & taskbar button
  		          flashInfo.dwTimeout = 0;
  		
  		          user32.FlashWindowEx(flashInfo);
  		      }
  		      
  			  SystemTray tray = SystemTray.getSystemTray();
  		      Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
  		
  		      // Create tray icon
  		      TrayIcon trayIcon = new TrayIcon(image, "New message");
  		      trayIcon.setImageAutoSize(true);
  		      
  		      try {
  		          tray.add(trayIcon);
  		          trayIcon.displayMessage("New Message",message, MessageType.INFO);
  		      } catch (AWTException e) {
  		          e.printStackTrace();
  		      }
  	  }
    }

    public static boolean isApplicationFocused() {
        Frame focusedFrame = (Frame) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        return focusedFrame != null;
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