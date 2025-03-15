import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import chatSystem.ChatPanel;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
   public static void main(String[] args) throws UnknownHostException, URISyntaxException, InterruptedException {
       // Create the main frame
       JFrame frame = new JFrame("Custom Label Printer");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(550, 400);

       // Create a JTabbedPane
       JTabbedPane tabbedPane = new JTabbedPane();
       
       tabbedPane.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent e) {
               JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
               int selectedIndex = sourceTabbedPane.getSelectedIndex();
               String selectedTabTitle = sourceTabbedPane.getTitleAt(selectedIndex);
               if(selectedTabTitle.equals("Chat"))
               {
            	   tabbedPane.setBackgroundAt(selectedIndex, null);
               }
           }
       });

       // Create panels for each tab
       JPanel tpCountPanel = new JPanel();
       tpCountPanel.add(new TpCountPanel(frame));


       JPanel countComboPanel = new JPanel();
       countComboPanel.add(new countComboPanel(frame));
       
       JPanel freePanel = new JPanel();
       freePanel.add(new FreePanel(frame));
       
       JPanel gcWeightPanel = new JPanel();
       gcWeightPanel.add(new GCweights(frame));

       JPanel reprintPanel = new JPanel();
       reprintPanel.add(new reprintPanel(frame));

       // Add tabs to the tabbedPane
       tabbedPane.addTab("TP count", tpCountPanel);
       tabbedPane.addTab("Combo count", countComboPanel);
       tabbedPane.addTab("GC weights", gcWeightPanel);
       tabbedPane.addTab("Reprint", reprintPanel);
       tabbedPane.addTab("Chat", new ChatPanel(frame,tabbedPane));
       tabbedPane.addTab("no clue", freePanel);

       // Add the tabbedPane to the frame
       frame.setLayout(new BorderLayout());
       frame.add(tabbedPane, BorderLayout.CENTER);
       frame.setLocationRelativeTo(null);

       // Set the frame visibility
       frame.setVisible(true);
       
       
       ExecutorService executor = Executors.newSingleThreadExecutor();
		// Start WebSocket Client in a separate thread with auto-reconnect
       executor.submit(() -> {
    	  SequenceGetter sequenceGetter = new SequenceGetter("pmambo","4292");
           while (true) {
               try {
                   WebSocketClient client = new WebSocketClient(new URI("ws://projectmbymoneymine.com:8082")) {
                       @Override
                       public void onOpen(ServerHandshake handshakedata) {
                           System.out.println("CONNECTED TO WEBSOCKET SERVER");
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
								if (type.equals("whistle_order_request"))
								{
									JSONObject data = (JSONObject) obj.get("data");
									String orderNum = data.get("orderNum").toString();
									String html = sequenceGetter.getOrderHTML(orderNum);
									
									JSONObject responseObj = new JSONObject();
									responseObj.put("type","whistle_data_response");
									responseObj.put("data",html);
									
									send(responseObj.toJSONString());
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
                   while (client.isOpen()) {
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
   }
}