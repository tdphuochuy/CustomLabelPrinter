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

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import buttons.ButtonObj;
import buttons.TableEntry;
import buttons.buttonsPanel;
import chatSystem.ChatPanel;
import config.Config;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import noclue.BFlookup;
import noclue.FreePanel;
import noclue.realWeight;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import paperwork.paperworkDSIPanel;
import paperwork.paperworkMarelPanel;
import paperwork.dsi.comboWeightTask;
import paperwork.dsi.paperworkDSIGen;
import raven.emoji.AutoWrapText;
import raven.emoji.EmojiIcon;
import whistle.NeoWhistlePanel;
import whistle.SequenceGetter;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
   private static NeoWhistlePanel neoWhistle;
   private static buttonsPanel buttons;
   private static paperworkDSIPanel ppwDSI;
   private static paperworkMarelPanel ppwMarel;
   public static void main(String[] args) throws URISyntaxException, InterruptedException, ParseException, IOException {
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
       
       JPanel bfLookupPanel = new JPanel();
       bfLookupPanel.add(new BFlookup(frame));
       
       JPanel realWeightPanel = new JPanel();
       realWeightPanel.add(new realWeight(frame));
       
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
       
       JTabbedPane paperworkTabs = new JTabbedPane();
       paperworkTabs.addTab("DSI", ppwDSIPanel);
       paperworkTabs.addTab("Marel", ppwMarelPanel);
       tabbedPane.addTab("Paperwork", paperworkTabs);
       
       tabbedPane.addTab("Reprint", reprintPanel);
       tabbedPane.addTab("Chat", new ChatPanel(frame,tabbedPane));
       
       JTabbedPane noclueTabs = new JTabbedPane();
       noclueTabs.addTab("Printing", freePanel);
       noclueTabs.addTab("Backflush Lookup", bfLookupPanel);
       noclueTabs.addTab("Adage's Real Weight", realWeightPanel);

       tabbedPane.addTab("no clue", noclueTabs);
       
       //change nested tab color to be looking transparent
       UIManager.put("TabbedPane.contentAreaColor", new Color(238,238,238,255));		
       SwingUtilities.updateComponentTreeUI(paperworkTabs);

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

       if (todayStr.equals("01-16") || todayStr.equals("01-15")) {
    	   showMessage(formatter,todayStr);
       } else if (todayStr.equals("03-13"))
       {
    	   LocalTime showTime = LocalTime.of(15, 15);
           LocalTime currentTime = LocalTime.now();
           
           if (currentTime.isBefore(showTime)) {
        	   long delay = java.time.Duration.between(LocalTime.now(), showTime).toMillis();
        	   System.out.println(delay);
               Timer scheduleTimer = new Timer();
               scheduleTimer.schedule(new TimerTask() {
                   @Override
                   public void run() {
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
                	   showMessage(formatter,todayStr);
                   }
               }, delay);
           } else {
        	   showMessage(formatter,todayStr);
           }
       }
   }
   
   private static void showMessage(DateTimeFormatter formatter,String todayStr)
   {
   	   while (true) {
           JPasswordField passwordField = new JPasswordField();
           
           JPanel panel = new JPanel();
           panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
           
           JTextPane messageLabel1 = new JTextPane();
    	   messageLabel1.setEditorKit(new AutoWrapText(messageLabel1));
    	   messageLabel1.setText("Bạn nhận được 1 lời nhắn. Nhập mật khẩu điện thoại (ddmmyy) của người dưng để xem!\n\nNếu bận không đọc được thì tắt app rồi mở lại khi nào rảnh nha");
    	   messageLabel1.setEditable(false);
    	   
    	   panel.add(messageLabel1);
           panel.add(Box.createVerticalStrut(5));
           panel.add(passwordField);
           
    	   int option = JOptionPane.showConfirmDialog(
                   null,
                   panel
                   ,
                   "Một thứ gì đó kì lạ vừa xuất hiện :D",
                   JOptionPane.OK_CANCEL_OPTION,
                   JOptionPane.PLAIN_MESSAGE
           );

    	   if (option != JOptionPane.OK_OPTION) {
    		   System.out.println("hello there");
               break;
           }
    	   
           String enteredPassword = new String(passwordField.getPassword());

           // Correct password
           String correctPassword = "270899";

           if (enteredPassword.equals(correctPassword)) {
        	   String title = "";
        	   
          	   EmojiIcon.getInstance().installEmojiSvg();
        	   
        	   JTextPane messageLabel = new JTextPane();
        	   messageLabel.setEditorKit(new AutoWrapText(messageLabel));
        	   
        	   // install this jtextpane to use emoji
        	   EmojiIcon.getInstance().installTextPane(messageLabel);

               if (todayStr.equals("01-16") || todayStr.equals("01-15")) {
            	   title = "Happy birthday!!!";
            	   messageLabel.setText("Chúc mừng sinh nhật Nhãnnnn!!! 🎉✨😀\nTuổi mới, thêm niềm vui mới!\nKhông còn đau vai, cảm lạnh, nhức đầu về đêm hay bệnh vặt nữa. 💪\nMong Nhãn luôn mỉm cười, dù sau này có ra saooo\nHổng biết có ai nói chưa, Nhãn đẹp lắm khi cười đó! 😳\nNhan sắc chắc khỏi bàn, hổng cần chúc, KIM DA MI VIỆT NAMMMM 😍\nHơi khô khan, lạnh lùng boy nên chỉ có nhiêu đây lời để nói thui 😢\nKhông có tư cách để chúc nhiều hơn nữa, HỨ! 😒 \nMãi mãi tuổi 19 nhoaaa!!! （づ￣3￣）づ 💖\n\n\nLời chúc này được lập trình vào ngày 11/16/2025\nCái tuần Nhãn nghỉ vacation 2 ngày vì bệnh á\nKhông biết lúc Nhãn đọc được những lời này"+
                       " thì mọi thứ ra sao nhỉ? 🙄\nNếu mọi thứ xấu đi hay đại loại vậy thì xin làm lơ mình đi nha\nHông cần cảm ơn hay gì đâu, còn lỡ tệ lắm mà không nhận quà thì sọt rác kế bên 😀😀\nTrời chuyển lạnh đó, phải giữ ấm nha chưa!!!\n\n----2/5/2026----\nLúc đọc được tin nhắn này, mình hy vọng, à không, chắc chắn cả 2 đều đang hạnh phúc\ndù có còn thấy nhau nữa hay không 😁\n--------------------\n\nFrom: Người dưng / hến vương / con 😾 dưới chân Nhãn");
               } else {
            	   messageLabel.setText("Nói nghe nè...\n\nUmmm...\n\nBiết là Nhãn đang có chuyện buồn nên cũng không biết có nên để lại lời nhắn này không.\nNhưng mà mình lại không đủ vô tâm để cứ im lặng mà đi..."
            	   		+ "\n\nHôm nay là ngày cuối mình làm việc ở đây\nBắt đầu từ tuần sau, mình sẽ làm việc trong office.\n\n"
            	   		+ "Tiếc rằng mình không đủ nhẫn tâm để tiếp tục nhìn cả hai làm tổn thương nhau\n"
            	   		+ "Tiếc rằng mình không đủ tàn nhẫn để tiếp tục lờ đi những tin nhắn của Nhãn (mình hiểu mà)\n"
            	   		+ "Tiếc rằng mình thiếu mạnh mẽ để thắng được lí trí, cũng như một danh phận để cho phép bản thân nhìn lại...\n"
            	   		+ "Tiếc rằng chúng ta không thể cho nhau thứ người kia thiếu và muốn được\n"
            	   		+ "Mọi thứ đã sai từ lúc đầu...Ra đi có lẽ là điều tốt nhất và cũng là điều mình nên làm.\n\n"
            	   		+ "Chúng ta có duyên, nhưng chắc không có nợ\n"
            	   		+ "Có tình yêu, nhưng chắc không đủ lớn để vì nhau, chọn nhau và giữ nhau\n"
            	   		+ "Có lẽ đôi ta không thực sự là dành cho nhau\n"
            	   		+ "Gặp nhau, biết nhau và dành tình cảm cho nhau có lẽ là đẹp và đáng quý lắm rồi\n\n"
            	   		+ "Tp cho nguyên tuần mình để trên boxroom á, có gì lên lấy xuống dùng cho hết nha\n"
            	   		+ "Dù gì đống đó cũng tự Nhãn dán mà\n"
            	   		+ "Với có gì giúp đỡ chị da trắng với nha, train mới có 1 tuần thôi nên cũng tội\n"
            	   		+ "À với có đăng hình hay đổi ảnh đại diện gì thì cứ thoải mái đi, "
            	   		+ "mình restrict Nhãn với không còn dõi theo từ lâu rồi\n"
            	   		+ "Lẽ ra nên làm vậy từ trước tết để không phải học bài học về sự cố chấp và buông bỏ...Ừ, đang trách đó!\n"
            	   		+ "Chắc khi nào mình gặp được cô ấy thì mình mới gỡ restrict\n"
            	   		+ "À với đừng mời cưới mình nha, cho bung bét đó không nói chơi đâu!!!\n"
            	   		+ "À đừng có nhắn tin tạm biệt, cảm ơn hay gì gì nha, còn nếu muốn trách móc gì thì chịu khó giữ trong lòng đi\nNhường mình làm người nói những lời cuối nha...\nCũng không còn lý do gì để nhắn nữa rồi nên nhắn nữa là bị ghost hoặc lại cãi lộn đó!\n\n"
            	   		+ "Dù sau này có chuyện gì, mình mong...à không, mình tin chắc Nhãn sẽ mạnh mẽ và vững vàng vượt qua mà\n"
            	   		+ "Tình đầu của mình train có 3 ngày mà còn làm việc ngon ơ thì có gì mà làm khó được!!!\n\n"
            	   		+ "Duyên ta có lẽ đến đây là được rồi...Ở lại mạnh giỏi, chăm sóc bản thân thật tốt và phải thật hạnh phúc nha!\nCảm ơn vì đã xuất hiện và có mặt trong cuộc đời mình\n\nNgười dưng tôi thương nhất 😁");
               }
               
        	   messageLabel.setEditable(false);
        	   
               JOptionPane.showMessageDialog(
                   null,
                   messageLabel,
                   title
                   ,
                   JOptionPane.INFORMATION_MESSAGE
               );
               
               LocalDateTime now = LocalDateTime.now();
               formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
               String content = now.format(formatter);

               // Write to read.txt
               /*try {
                   Files.write(Path.of("read.txt"), content.getBytes());
                   System.out.println("File created and written successfully.");
               } catch (IOException e) {
                   e.printStackTrace();
               }*/
               
               try {
				updateMessage("Đã đọc");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
               
               break;
           } else {
        	   JOptionPane.showMessageDialog(
                       null,
                       "Incorrect password. Try again.",
                       "Error",
                       JOptionPane.ERROR_MESSAGE
               );
        	   
        	   passwordField.setText("");
           }
	   }
   }
   
   private static void updateMessage(String message) throws IOException
   {
       // Define JSON body
	   
	   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       LocalDateTime now = LocalDateTime.now();
       String formattedDateTime = now.format(formatter);
       
	   message = "Last updated: " + formattedDateTime + "\n" + message;
	   	
	   	JSONObject obj = new JSONObject();
	   	obj.put("message", message);

       

       String url = "https://" + Config.serverDomain + "/recap";

       Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Config.webSocketproxyIP, Config.webSocketproxyPort));

		okhttp3.Authenticator proxyAuthenticator = new okhttp3.Authenticator() {
			  @Override public Request authenticate(Route route, Response response) throws IOException {
			       String credential = Credentials.basic(Config.webSocketproxyIP,Config.webSocketproxyPass);
			       return response.request().newBuilder()
			           .header("Proxy-Authorization", credential)
			           .build();
			  }
			};

       OkHttpClient client = new OkHttpClient.Builder()
               //.proxy(proxy)
               //.proxyAuthenticator(proxyAuthenticator)
               .hostnameVerifier((hostname, session) -> true)
               .build();


       RequestBody body = RequestBody.create(
       		obj.toJSONString(), 
               MediaType.get("application/json; charset=utf-8")
       );

       Request request = new Request.Builder()
               .url(url)
               .post(body)
               .build();

       try (Response response = client.newCall(request).execute()) {
           if (response.isSuccessful()) {
               System.out.println("Response: " + response.body().string());
           } else {
               System.err.println("Request failed: " + response.code());
           }
       }
   }
}