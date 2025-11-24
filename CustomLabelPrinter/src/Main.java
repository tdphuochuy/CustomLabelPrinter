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
import paperwork.paperworkDSIPanel;
import paperwork.paperworkMarelPanel;
import paperwork.dsi.comboWeightTask;
import paperwork.dsi.paperworkDSIGen;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
   private static paperworkDSIPanel ppwDSI;
   private static paperworkMarelPanel ppwMarel;
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
       
       JPanel ppwDSIPanel = new JPanel();
       ppwDSI = new paperworkDSIPanel(frame);
       ppwDSIPanel.add(ppwDSI);
       
       JPanel ppwMarelPanel = new JPanel();
       ppwMarel = new paperworkMarelPanel(frame);
       ppwMarelPanel.add(ppwMarel);

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
       
       JTabbedPane nestedTabs = new JTabbedPane();
       nestedTabs.addTab("DSI", ppwDSIPanel);
       nestedTabs.addTab("Marel", ppwMarelPanel);
       tabbedPane.addTab("Paperwork", nestedTabs);
       tabbedPane.addTab("Reprint", reprintPanel);
       tabbedPane.addTab("Chat", new ChatPanel(frame,tabbedPane));
       tabbedPane.addTab("no clue", freePanel);
       
       //change nested tab color to be looking transparent
       UIManager.put("TabbedPane.contentAreaColor", new Color(238,238,238,255));		
       SwingUtilities.updateComponentTreeUI(nestedTabs);

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
           while (true) {
               try {
                   WebSocketClient client = new WebSocketClient(new URI("wss://" + Config.serverDomain)) {
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
								String type = obj.get("type").toString();
								if(type.equals("ping"))
								{
									ppwDSI.setVerified(true);
									neoWhistle.setVerified(true);
								} else if (type.equals("pong"))
								{
									ppwDSI.setVerified(false);
									neoWhistle.setVerified(false);
								}
								if (type.equals("whistle_order_request"))
								{
									JSONObject data = (JSONObject) obj.get("data");
									String orderNum = data.get("orderNum").toString();
									String html = sequenceGetter.getOrderHTML(orderNum);
									
									JSONObject responseObj = new JSONObject();
									responseObj.put("type","whistle_data_response");
									responseObj.put("data",html);
									System.out.println(html.substring(0,200));
									send(responseObj.toJSONString());
								}else if(type.equals("whistle_command"))
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
									Thread GCthread = new Thread(new comboWeightTask(username,password,orderNum,reworkOrderNum,"File","105884","2124"));
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
				             			 paperworkDSIGen ppw = new paperworkDSIGen(frame,username,password,orderNum,reworkOrderNum,name,times,comdemnList,comdemnList,false,true,tenderCondemned);
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
													neoWhistle.addWhistleButtonCommand(button.getProductCode(), quantity);
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
       
       checkBirthday();
   }
   
   private static void checkBirthday() {
       LocalDate today = LocalDate.now();
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
       String todayStr = today.format(formatter);

       if (todayStr.equals("01-16")) {
           JOptionPane.showMessageDialog(
               null,
               "Chúc mừng tuổi 19 của Nhãnnnn!!! 😁\nTuổi mới, thêm niềm vui mới!\nKhông còn đau vai, cảm lạnh, nhức đầu về đêm hay bệnh vặt nữa\nMong Nhãn luôn mỉm cười, như món quà, dù sau này có ra sao\nNhãn luôn xinh đẹp trong mắt mình, nhưng đẹp nhất khi cười đó :D\nHơi khô khan nên chỉ chúc được nhiêu đây thui (┬﹏┬)\nMãi mãi tuổi 19 nhoaaa!!! （づ￣3￣）づ❤️\n\nLời chúc này được lập trình vào ngày 11/16/2025\nkhông biết lúc Nhãn đọc"+
               " được những lời này thì mọi thứ sẽ thay đổi ra sao nhỉ?\n\n\nFrom: Người dưng / hến vương",
               "Happy birthday!!!",
               JOptionPane.INFORMATION_MESSAGE
           );
       }
   }
}