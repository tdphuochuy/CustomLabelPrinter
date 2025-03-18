package whistle;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NeoWhistleTask implements Runnable {
	private String username;
    private String password;
    private boolean autoSequence;
    private volatile boolean running = true;
    private JTextArea systemConsole;
    private JTextArea userConsole;
    private TelnetManager manager;
	public NeoWhistleTask(String username, String password, boolean autoSequence,JTextArea userConsole,JTextArea systemConsole) {
        this.username = username;
        this.password = password;
        this.autoSequence = autoSequence;
        this.systemConsole = systemConsole;
        this.userConsole = userConsole;
    }
	
	@Override
    public void run()
	{
		
		try {
			userConsole.append("Enter order number:\n");
			Scanner scanner = new Scanner(System.in);
			String orderNum = scanner.nextLine();
			System.out.println(orderNum);
			manager = new TelnetManager(orderNum, username, password, autoSequence,systemConsole);
			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			// Start WebSocket Client in a separate thread with auto-reconnect
	        executor.submit(() -> {
	            while (running) {
	                try {
	                    WebSocketClient client = new WebSocketClient(new URI("ws://projectmbymoneymine.com:8082")) {
	                        @Override
	                        public void onOpen(ServerHandshake handshakedata) {
	                            System.out.println("CONNECTED TO WEBSOCKET SERVER!");
	                            JSONObject obj = new JSONObject();
	                            obj.put("type", "auth");
								obj.put("data", "whistle_server");
	                            send(obj.toJSONString());
	                        }
	
	                        @Override
	                        public void onMessage(String message) {
	                			try {
	                            	JSONParser parser = new JSONParser();
									JSONObject obj = (JSONObject)parser.parse(message);
									System.out.println(obj);								String type = obj.get("type").toString();
									if(type.equals("whistle_command"))
									{
										JSONObject data = (JSONObject) obj.get("data");
										String prodNum = data.get("prodNum").toString();
										String quantity = data.get("quantity").toString();
										manager.addCommand(new Command(prodNum,quantity,"1"));
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                			
	                        }
	
	                        @Override
	                        public void onClose(int code, String reason, boolean remote) {
	                            System.out.println("WebSocket connection closed: " + reason);
	                        }
	
	                        @Override
	                        public void onError(Exception ex) {
	                            System.err.println("WebSocket Error: " + ex.getMessage());
	                        }
	                    };
	                    client.connectBlocking(); // Block until connected
	                    while (client.isOpen() && running) {
	                        Thread.sleep(5000); // Keep the connection alive
	                    }
	                } catch (URISyntaxException | InterruptedException e) {
	                    System.err.println("WebSocket client error: " + e.getMessage());
	                }
	                System.out.println("Reconnecting WebSocket in 5 seconds...");
	                try {
	                    Thread.sleep(5000);
	                } catch (InterruptedException ignored) {}
	            }
	        });
			
			while(running)
			{
				userConsole.append("Enter product code\n");
				String prodNum = scanner.nextLine();
				userConsole.append("Enter quantity\n");
				String quantity = scanner.nextLine();
				String sequence = "1";
				if(!autoSequence)
				{
					userConsole.append("Enter sequence\n");
					sequence = scanner.nextLine();
				}
				manager.addCommand(new Command(prodNum,quantity,sequence));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() throws IOException {
        running = false;
        if(manager != null)
        {
        	manager.stop();
        }
    }
}