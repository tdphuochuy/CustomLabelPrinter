import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import buttons.ButtonObj;
import buttons.TableEntry;
import buttons.buttonsPanel;
import chatSystem.ChatPanel;
import config.Config;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import paperwork.GCweightsTask;
import paperwork.paperworkGen;
import paperwork.paperworkPanel;
import whistle.NeoWhistlePanel;
import whistle.SequenceGetter;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
   private static NeoWhistlePanel neoWhistle;
   private static buttonsPanel buttons;
   private static paperworkPanel ppw;
   public static void main(String[] args) throws UnknownHostException, URISyntaxException, InterruptedException, ParseException {
       // Create the main frame
       JFrame frame = new JFrame("Custom Label Printer");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(600, 450);

       // Create a JTabbedPane
       JTabbedPane tabbedPane = new JTabbedPane();
       tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
       
       tabbedPane.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent e) {
               JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
               int selectedIndex = sourceTabbedPane.getSelectedIndex();
               String selectedTabTitle = sourceTabbedPane.getTitleAt(selectedIndex);
               if(selectedTabTitle.equals("Chat"))
               {
            	   tabbedPane.setBackgroundAt(selectedIndex, null);
               } else if (selectedIndex == 2)
               {
            	   neoWhistle.focusInput();
               }
           }
       });
       
       IconFontSwing.register(FontAwesome.getIconFont());

       // Create panels for each tab
       JPanel tpCountPanel = new JPanel();
       tpCountPanel.add(new TpCountPanel(frame));


       JPanel countComboPanel = new JPanel();
       countComboPanel.add(new countComboPanel(frame));
       
       JPanel freePanel = new JPanel();
       freePanel.add(new FreePanel(frame));
       
       JPanel ppwPanel = new JPanel();
       ppw = new paperworkPanel(frame);
       ppwPanel.add(ppw);

       JPanel reprintPanel = new JPanel();
       reprintPanel.add(new reprintPanel(frame));
       
       JPanel buttonsPanel = new JPanel();
       buttons = new buttonsPanel(frame);
       buttonsPanel.add(buttons);
       
       JPanel neoWhistlePanel = new JPanel();
       neoWhistle = new NeoWhistlePanel(frame,buttons);
       neoWhistlePanel.add(neoWhistle);

       // Add tabs to the tabbedPane
       tabbedPane.addTab("TP count", tpCountPanel);
       tabbedPane.addTab("Combo count", countComboPanel);
       tabbedPane.addTab("", IconFontSwing.buildIcon(FontAwesome.EXCLAMATION_TRIANGLE, 12, Color.red), neoWhistlePanel);
       tabbedPane.addTab("", IconFontSwing.buildIcon(FontAwesome.CIRCLE_O, 12, Color.BLACK), buttonsPanel);
       tabbedPane.addTab("Paperwork", ppwPanel);
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
    	  SequenceGetter sequenceGetter = new SequenceGetter(Config.username,Config.password);
	      	String websocketUrl = "wss://" + Config.serverDomain + ":8082"; // Replace with your WebSocket URL
	
	        // Proxy setup
	        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Config.webSocketproxyIP, Config.webSocketproxyPort));
	
	        // Proxy authenticator
	        Authenticator proxyAuthenticator = (route, response) -> {
	            String credential = Credentials.basic(Config.webSocketproxyUser, Config.webSocketproxyPass);
	            return response.request().newBuilder()
	                    .header("Proxy-Authorization", credential)
	                    .build();
	        };
	        
	        TrustManager[] trustAllCerts = new TrustManager[]{
	        	    new X509TrustManager() {
	        	        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
	        	        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
	        	        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
	        	    }
	        	};
	
	        SSLContext sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(null, trustAllCerts, new SecureRandom());
	        
	        OkHttpClient client = new OkHttpClient.Builder()
	                .proxy(proxy)
	                .proxyAuthenticator(proxyAuthenticator)
	                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
	                .hostnameVerifier((hostname, session) -> true)
	                .readTimeout(0, TimeUnit.MILLISECONDS) // Disable timeout for WebSocket
	                .build();
	
	        Request request = new Request.Builder()
	                .url(websocketUrl)
	                .build();
	        
	        final AtomicBoolean isConnected = new AtomicBoolean(false);
	        final AtomicBoolean isConnecting = new AtomicBoolean(false);

	        WebSocketListener listener = new WebSocketListener() {
	            @Override
	            public void onOpen(WebSocket webSocket, Response response) {
	            	isConnected.set(true);
	            	isConnecting.set(false);
	            	System.out.println("CONNECTED TO WEBSOCKET SERVER!");
	                JSONObject obj = new JSONObject();
	                obj.put("type", "auth");
					obj.put("data", "whistle_server");
					webSocket.send(obj.toJSONString());
	            }
	
	            @Override
	            public void onMessage(WebSocket webSocket, String message) {
	            	try {
	               		JSONParser parser = new JSONParser();
						JSONObject obj = (JSONObject)parser.parse(message);
						System.out.println(obj);
						String type = obj.get("type").toString();
						if (type.equals("whistle_order_request"))
						{
							JSONObject data = (JSONObject) obj.get("data");
							String orderNum = data.get("orderNum").toString();
							String html = sequenceGetter.getOrderHTML(orderNum);
							
							JSONObject responseObj = new JSONObject();
							responseObj.put("type","whistle_data_response");
							responseObj.put("data",html);
							System.out.println(html.substring(0,200));
							webSocket.send(responseObj.toJSONString());
						} else if(type.equals("whistle_command"))
						{
							JSONObject data = (JSONObject) obj.get("data");
							String prodNum = data.get("prodNum").toString();
							String quantity = data.get("quantity").toString();
							neoWhistle.addWhistleCommand(prodNum,quantity);
						} else if (type.equals("whistle_start_request"))
						{
							JSONObject data = (JSONObject) obj.get("data");
							String orderNum = data.get("orderNum").toString();
							neoWhistle.startNeoWhistle(orderNum);
						} else if (type.equals("gc_weights_request"))
						{
							JSONObject data = (JSONObject) obj.get("data");
							String orderNum = data.get("orderNum").toString();
							String username = data.get("username").toString();
							String password = data.get("password").toString();
							String reworkOrderNum = "";
							if(data.get("reworkOrderNum") != null)
							{
								reworkOrderNum = data.get("reworkOrderNum").toString();
							}
							Thread GCthread = new Thread(new GCweightsTask(username,password,orderNum,reworkOrderNum));
							GCthread.start();
						} else if (type.equals("paperwork"))
						{
							JSONObject data = (JSONObject) obj.get("data");
							String orderNum = data.get("orderNum").toString();
							String reworkOrderNum = data.get("reworkOrderNum").toString();
							String username = data.get("username").toString();
							String password = data.get("password").toString();
							String name = data.get("name").toString();
							String firstBreak = data.get("firstBreak").toString();
							String secondBreak = data.get("secondBreak").toString();
							String trimCondemned = data.get("trimCondemned").toString();
							String tenderCondemned = data.get("tenderCondemned").toString();
	
							int break1 = Integer.parseInt(firstBreak);
	              			int break2 = Integer.parseInt(secondBreak);
	              			int[] times = {break1,break2};
	              			List<Integer> comdemnList = new ArrayList<>();
	              			for (String s : trimCondemned.split(",")) {
	              				comdemnList.add(Integer.parseInt(s));
	              			}
							new Thread(() -> {
		             			 paperworkGen ppw = new paperworkGen(frame,username,password,orderNum,reworkOrderNum,name,times,comdemnList,comdemnList,false,true,tenderCondemned);
		             			 try {
									ppw.start();
								} catch (ParseException | InterruptedException | IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
	              	        }).start();
						} else if (type.equals("whistle_button"))
						{
							JSONObject data = (JSONObject) obj.get("data");
							String buttonName = data.get("buttonName").toString();
							ButtonObj button = buttons.getButton(buttonName);
							if(button != null)
							{
								if(button.isEnabled())
								{
									long currentTime = System.currentTimeMillis();
									long buttonLastTimeStampt = button.getLastTimeStamp();
									long buttonDelay = button.getDelay();
									if (currentTime - buttonLastTimeStampt >= buttonDelay) {
										String quantity = button.getQuantity(); 
										if(quantity != null)
										{
											button.setLastTimeStamp(currentTime);
											Map<String, TableEntry> sequenceHourMap = buttons.getHourSequenceMap();
											if(sequenceHourMap.containsKey(button.getProductCode()))
											{
												TableEntry entry = sequenceHourMap.get(button.getProductCode());
												neoWhistle.addWhistleButtonCommand(button.getProductCode(), quantity, entry.getHour(), entry.getSequence());
											} else {
												neoWhistle.addWhistleButtonCommand(button.getProductCode(), quantity);
											}
										}
									}
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	
	            @Override
	            public void onClosing(WebSocket webSocket, int code, String reason) {
	                System.out.println("Closing: " + code + " / " + reason);
	            }
	
	            @Override
	            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
	            	if (response != null) {
	                    System.err.println("Response code: " + response.code());
	                    System.err.println("Response headers: " + response.headers());
	                    try {
							System.err.println("Response body: " + response.body().string());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
	                t.printStackTrace();
	                System.out.println("Failure!!");
	                isConnected.set(false);
	            	isConnecting.set(false);
	            }
	        };
        
	        while(true)
	        {
	        	WebSocket webSocket = client.newWebSocket(request, listener);
	        	System.out.println("Connecting to websocket");
	        	isConnecting.set(true);
	        	while(isConnecting.get())
	        	{
                    Thread.sleep(1000); 
	        	}
	        	System.out.println("Connected!!!!");
	        	while(isConnected.get())
	        	{
	        		try {
	                    Thread.sleep(5000); // Polling every 5 seconds
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	        	}
	        }
       });
   }
}