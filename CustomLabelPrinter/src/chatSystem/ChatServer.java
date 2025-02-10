package chatSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChatServer extends WebSocketServer {
	  private Map<WebSocket,String> nameMap;
	
	  public ChatServer(int port) throws UnknownHostException {
	    super(new InetSocketAddress(port));
	    nameMap = new HashMap<>();
	  }
	
	  public ChatServer(InetSocketAddress address) {
	    super(address);
	  }
	
	  public ChatServer(int port, Draft_6455 draft) {
	    super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
	  }
	
	  @Override
	  public void onOpen(WebSocket conn, ClientHandshake handshake) {
		
	  }
	
	  @Override
	  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		    JSONObject obj = new JSONObject();
			obj.put("type", "system");
			obj.put("message", nameMap.get(conn) + " has left the room!");
		    broadcast(obj.toJSONString());
		    nameMap.remove(conn);
	  }
	
	  @Override
	  public void onMessage(WebSocket conn, String msg) {
		  try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(msg);
			String type = obj.get("type").toString();
			String message = obj.get("message").toString();
			if(type.equals("init"))
			{
				for(WebSocket connection: nameMap.keySet())
				{
					obj = new JSONObject();
					obj.put("type", "system");
					obj.put("message", nameMap.get(connection) + " has joined the room!");
				    conn.send(obj.toJSONString());
				}
				
				nameMap.put(conn,message);
				obj = new JSONObject();
				obj.put("type", "system");
				obj.put("message", nameMap.get(conn) + " has joined the room!");
			} else if (type.equals("message")) {
				obj = new JSONObject();
				obj.put("type", "message");
				obj.put("message", nameMap.get(conn) + ": " + message);
			}
		    broadcast(obj.toJSONString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	
	  @Override
	  public void onError(WebSocket conn, Exception ex) {
	    ex.printStackTrace();
	    if (conn != null) {
	      // some errors like port binding failed may not be assignable to a specific websocket
	    }
	  }
	
	  @Override
	  public void onStart() {
	    System.out.println("Server started!");
	    setConnectionLostTimeout(0);
	    setConnectionLostTimeout(100);
	  }

}