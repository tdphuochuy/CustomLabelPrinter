package chatSystem;



import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.swing.JPanel;
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
  public ChatClient(URI serverUri, Draft draft) {
    super(serverUri, draft);
  }

  public ChatClient(URI serverURI) {
    super(serverURI);
  }
  
  public ChatClient(URI serverURI,JTextField textField,JTextArea textArea) {
    super(serverURI);
    this.textField = textField;
    this.textArea = textArea;
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  public void appendChat(String message)
  {
	  textArea.append(message + "\n"); // Append the message
      textField.setText("");
      //setPlaceholder(textField,"Message");
      textArea.setCaretPosition(textArea.getDocument().getLength());
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
	  
  }

  @Override
  public void onError(Exception ex) {
    ex.printStackTrace();
  }


}