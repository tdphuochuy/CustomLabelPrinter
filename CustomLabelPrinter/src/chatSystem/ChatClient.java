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
  private ChatPanel cp;
  public ChatClient(URI serverUri, Draft draft) {
    super(serverUri, draft);
  }

  public ChatClient(URI serverURI) {
    super(serverURI);
  }
  
  public ChatClient(URI serverURI,JTextField textField,JTextArea textArea,JTabbedPane tabbedPane,ChatPanel cp) {
    super(serverURI);
    this.cp = cp;
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
			cp.appendChat(type, message);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
  	//System.out.println("Closed Reconnecting chat server...");
	//cp.reconnectWS();
  }

  @Override
  public void onError(Exception ex) {
	 System.out.println("Reconnecting chat server...");
	 ex.printStackTrace();
	 cp.reconnectWS();
  }


}