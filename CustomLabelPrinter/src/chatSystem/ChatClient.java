package chatSystem;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import config.Config;

public class ChatClient extends WebSocketClient {
  private JTextField textField;
  private JTextArea textArea;
  private JTabbedPane tabbedPane;
  public ChatClient(URI serverUri, Draft draft) {
    super(serverUri, draft);
  }

  public ChatClient(URI serverURI) {
    super(serverURI);
  }
  
  public ChatClient(URI serverURI,JTextField textField,JTextArea textArea,JTabbedPane tabbedPane) {
    super(serverURI);
    this.textField = textField;
    this.textArea = textArea;
    this.tabbedPane = tabbedPane;
  }

  public ChatClient(URI serverUri, Map<String, String> httpHeaders) {
    super(serverUri, httpHeaders);
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    send("{\"type\":\"init\",\"message\":\"" + Config.chatId + "\"}");
  }

  @Override
  public void onMessage(String msg) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(msg);
			String type = obj.get("type").toString();
			String message = obj.get("message").toString();
			appendChat(message);
			if(type.equals("message"))
			{
			      notifyUser(message);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  public void appendChat(String message)
  {
	  SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
      String currentTime = timeFormat.format(new Date());
	  textArea.append("[" + currentTime + "] " + message + "\n"); // Append the message
      //setPlaceholder(textField,"Message");
      textArea.setCaretPosition(textArea.getDocument().getLength());
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
	      WinDef.HWND hWnd = user32.GetForegroundWindow(); // Get the current window handle
	      
		      if (hWnd != null) {
		          WinUser.FLASHWINFO flashInfo = new WinUser.FLASHWINFO();
		          flashInfo.hWnd = hWnd;
		          flashInfo.uCount = WinUser.FLASHW_TIMERNOFG; // Flash until window is focused
		          flashInfo.dwFlags = WinUser.FLASHW_ALL; // Flash title bar & taskbar button
		          flashInfo.dwTimeout = 0;
		
		          user32.FlashWindowEx(flashInfo);
		          System.out.println("Flashing taskbar icon...");
		      } else {
		          System.out.println("No active window found!");
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

  @Override
  public void onClose(int code, String reason, boolean remote) {
	  new Thread(() -> {
		    try {
		        Thread.sleep(10000); // Optional delay to allow cleanup
		        reconnect();
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		}).start();
  }

  @Override
  public void onError(Exception ex) {
    ex.printStackTrace();
    new Thread(() -> {
        try {
            Thread.sleep(10000); // Optional delay to allow cleanup
            reconnect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
  }


}