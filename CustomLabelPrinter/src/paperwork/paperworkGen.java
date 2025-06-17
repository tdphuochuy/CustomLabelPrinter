package paperwork;

import java.awt.Desktop;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.stream.Collectors;

import java.net.Socket;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import config.Config;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import paperwork.gen.breastGen;
import paperwork.gen.carcassGen;
import paperwork.gen.recapGen;
import paperwork.gen.tenderGen;

public class paperworkGen{
	private String username,password,orderNum,reworkOrderNum,name;
	private int[] times;
	private List<Integer> bloodcondemnList,greencondemnList;
	private String sessionId = "";
	private String tenderCondemnTotal;
	private String recipient = "tdphuochuy@gmail.com";
	private Frame frame;
	private List<Double> issuedList1 = new ArrayList<>();
	private List<Double> issuedList2 = new ArrayList<>();
	private boolean pdfOnly,sendEmail;
	public paperworkGen(Frame frame,String username,String password,String orderNum,String reworkOrderNum,String name,int[] times,List<Integer> bloodcondemnList,List<Integer> greencondemnList,boolean pdfOnly,boolean sendEmail,String tenderCondemnTotal)
	{
		this.frame = frame;
		this.username = username;
		this.password = password;
		this.orderNum = orderNum;
		this.reworkOrderNum = reworkOrderNum;
		this.name = name;
		this.times = times;
		this.bloodcondemnList = bloodcondemnList;
		this.greencondemnList = greencondemnList;
		this.pdfOnly = pdfOnly;
		this.sendEmail = sendEmail;
		this.tenderCondemnTotal = tenderCondemnTotal;
		sessionId = getSessionId();
	}
	
	public void start() throws ParseException, InterruptedException, IOException
	{
		deleteOldRecap();
		Map<String,Product> productMap = new TreeMap<>();

		Element dataTable = getData(orderNum);
		extractData(productMap,dataTable);
		if(reworkOrderNum.length() > 0)
		{
			System.out.println("Found rework!!");
			Element dataTableRework = getData(reworkOrderNum);
			if(dataTableRework != null)
			{
				extractData(productMap,dataTableRework);
			}
			getIssuedData(reworkOrderNum);
		}
		
		evaluateData(productMap);
	}
	
	public void getIssuedData(String orderNum)
	{		
		OkHttpClient client = new OkHttpClient();

		FormBody formBody = new FormBody.Builder()
                .add("fileName", "reports/issue_detail_for_production_order.txt")
                .add("prodOrder2", orderNum)
                .add("submit1", "Go")
                .add("r", "XMLReport")
                .add("f", "n")
                .add("session", sessionId)
                .build();
		
		Request request = new Request.Builder()
                .url("http://whistleclient/cgi-bin/yield/") // Example URL
                .post(formBody)
                .build();
		
		try (
				Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
            	String body = response.body().string();
            	Document doc = Jsoup.parse(body);
            	Element bodyElement = doc.body();
            	Element inputElement = bodyElement.select("[name=unnamed]").first();
            	for(Element table : inputElement.getElementsByTag("table"))
            	{
            		if(table.html().toLowerCase().contains(username.toLowerCase()))
            		{
            			for(Element tr : table.getElementsByTag("tr"))
            			{
            				String content = tr.html();
            				if(content.toLowerCase().contains(username.toLowerCase()))
            				{
            					Elements td = tr.getElementsByTag("td");
            					double quantity = Double.parseDouble(td.get(5).text().replace(",", ""));
            					String lotNum = td.get(4).text();
        						String hourSequenceText = lotNum.substring(lotNum.length() - 6);
        						int lotNumHour = Integer.valueOf(hourSequenceText.substring(0,2));
        						if(lotNumHour < 17)
        						{
                					issuedList1.add(quantity);
        						} else {
                					issuedList2.add(quantity);
        						}
            					System.out.println(quantity);
            				}
            			}
            		}
            	}
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	public void deleteOldRecap()
	{
        File file = new File("D:\\Users\\pdgwinterm7\\Desktop\\recap_output\\recap.pdf");

        if (!file.exists()) {
            System.out.println("File does not exist.");
        } else {
            if (file.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        }
	}
	
	public String getSessionId()
	{
		OkHttpClient client = new OkHttpClient();
		FormBody formBody = new FormBody.Builder()
                .add("user", username)
                .add("pass", password)
                .add("submit1", "Go")
                .add("r", "login")
                .add("f", "n")
                .build();
		
		Request request = new Request.Builder()
                .url("http://whistleclient/cgi-bin/yield/") // Example URL
                .post(formBody)
                .build();
		
		try (
				Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
            	String body = response.body().string();
            	String session = body.substring(body.indexOf("?session=")+9,body.indexOf("&cmp="));
            	return session;
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	public Element getData(String orderNum)
	{
		OkHttpClient client = new OkHttpClient();

		FormBody formBody = new FormBody.Builder()
                .add("fileName", "reports/SingleOrderProductionViewer.txt")
                .add("Order", orderNum)
                .add("submit1", "Go")
                .add("r", "XMLReport")
                .add("f", "n")
                .add("session", sessionId)
                .build();
		
		Request request = new Request.Builder()
                .url("http://whistleclient/cgi-bin/yield/") // Example URL
                .post(formBody)
                .build();
		
		try (
				Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
            	String body = response.body().string();
            	List<String> list = new ArrayList<>();
            	Document doc = Jsoup.parse(body);
            	Element bodyElement = doc.body();
            	Element inputElement = bodyElement.select("[name=unnamed]").first();
            	for(Element table : inputElement.getElementsByTag("table"))
            	{
            		if(table.html().toLowerCase().contains(username.toLowerCase()))
            		{
            			return table;
            		}
            	}
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	public void extractData(Map<String,Product> map,Element table) throws ParseException
	{
		InputStream inputStream = paperworkGen.class.getClassLoader().getResourceAsStream("paperwork/product_data.json");
        String inputData = new BufferedReader(new InputStreamReader(inputStream))
                            .lines()
                            .collect(Collectors.joining("\n"));
    	JSONParser jsonParser = new JSONParser();
    	JSONObject productObj = (JSONObject) jsonParser.parse(inputData);
		for(Element tr : table.getElementsByTag("tr"))
		{
			String content = tr.html();
			if(content.toLowerCase().contains(username.toLowerCase()))
			{
				String trackingNum = tr.getElementsByTag("a").get(0).text();
				if(content.contains("#ff0000"))
				{
					map.remove(trackingNum);
					continue;
				}
				Elements td = tr.getElementsByTag("td");
				String itemPack = td.get(2).text() + td.get(3).text();
				String productCode = ((JSONObject)productObj.get(itemPack)).get("Product").toString();
				String description =  td.get(4).text();
				String lotNumber =  td.get(5).text();
				int hour = Integer.parseInt(lotNumber.substring(lotNumber.length() - 6,lotNumber.length() - 4));
				if(hour > 35 && hour < 54)
				{
					hour = hour - 30;
				} else if (hour > 29 && hour < 35) {
					hour = hour - 6;
				} else if (hour > 26 && hour < 29)
				{
					hour = 26;
				}
				int quantity = (int) Double.parseDouble(td.get(8).text().replace(",", ""));
				double weight = Double.parseDouble(td.get(10).text().replace(",", ""));
				boolean isCombo = (((JSONObject)productObj.get(itemPack)).get("Container Type").toString().toLowerCase()).contains("combo");
				String type = getType(description);
				map.put(trackingNum,new Product(productCode,trackingNum,hour,type,quantity,weight,isCombo));
			}
		}
	}
	
	public void evaluateData(Map<String,Product> map) throws InterruptedException, IOException
	{
		breastGen breastExcel = new breastGen(times);
		tenderGen tenderExcel = new tenderGen(times);
		carcassGen carcassExcel = new carcassGen(times);
		
		for(String key: map.keySet())
		{
			Product product = map.get(key);
			if(product.getType().equals("breast"))
			{
				breastExcel.addProduct(product);
			} else if (product.getType().equals("tender"))
			{
				tenderExcel.addProduct(product);
			} else if (product.getType().equals("carcass"))
			{
				carcassExcel.addProduct(product);
			} else if (product.getType().equals("trim"))
			{
				tenderExcel.addTrim(product);
			} else if (product.getType().equals("keelbone"))
			{
				tenderExcel.addKeelBone(product);
			} else if (product.getType().equals("kneebone"))
			{
				tenderExcel.addKneeBone(product);
			} else if (product.getType().equals("skin"))
			{
				tenderExcel.addSkin(product);
			}
		}
		
		breastExcel.generateExcel();
		tenderExcel.generateExcel();
		carcassExcel.generateExcel();
		
		recapGen recapExcel = new recapGen(name,breastExcel,tenderExcel,carcassExcel,bloodcondemnList,greencondemnList,issuedList1,issuedList2,tenderCondemnTotal);
		recapExcel.generateExcel();
		
        File file = new File("D:\\Users\\pdgwinterm7\\Desktop\\recap_output\\recap.xlsx");
        exportExceltoPDF(file.getAbsolutePath());
        
        if(pdfOnly)
        {
        	openPDFfile();
        } else {
        	sendtoPrinterJob(Config.officePrinterIP,"All papers have been sent to DEBONE office!");
        }
        
        if(sendEmail)
        {
        	sendEmail();
        }
        
        try {
			postRecap(breastExcel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openPDFfile()
	{
		try {
	        String filePath = "D:\\Users\\pdgwinterm7\\Desktop\\recap_output\\recap.pdf"; // Can be .txt, .pcl, .ps, or supported PDF
            
	        File file = new File(filePath);

	        int timeoutSeconds = 30;
	        int waited = 0;

	        while (!file.exists()) {
	            System.out.println("Waiting for file to appear...");
	            Thread.sleep(1000); // Wait 1 second
	            waited++;

	            if (waited >= timeoutSeconds) {
	                System.out.println("Timeout reached. File not found.");
	                return;
	            }
	        }
	        
	        File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().browse(pdfFile.toURI()); // or use .open() for system default PDF viewer
            } else {
                System.out.println("File does not exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public String getType(String description)
	{
		String type = "";
		if(description.contains("TENDER"))
		{
			type = "tender";
		} else if (description.contains("CARC"))
		{
			type = "carcass";
		} else if (description.contains("TRIMGS")) {
			type = "trim";
		} else if (description.contains("BRST")) {
			type = "breast";
		} else if (description.contains("KNEE SOFT BONE")) {
			type = "kneebone";
		} else if (description.contains("KEELBONES")) {
			type = "keelbone";
		} else if (description.contains("SKN")) {
			type = "skin";
		}

		return type;
	}
	
	public static void exportExceltoPDF(String excelFilePath)
	{
        String outputDir = "D:\\Users\\pdgwinterm7\\Desktop\\recap_output\\";

        // Full path to LibreOffice Portable or installed soffice.exe
        String libreOfficePath = "D:\\Users\\pdgwinterm7\\Downloads\\LibreOfficePortable\\App\\libreoffice\\program\\soffice.exe"; // Adjust path if needed

        // Construct the command to run LibreOffice headless
        String command = libreOfficePath + " --headless --convert-to pdf --outdir " + outputDir + " " + excelFilePath;
        System.out.println(command);
        try {
            // Run the process
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Conversion successful!");
            } else {
                System.out.println("Error during conversion. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	public void sendtoPrinterJob(String printerIp,String notificationText) throws InterruptedException
	{
        String filePath = "D:\\Users\\pdgwinterm7\\Desktop\\recap_output\\recap.pdf"; // Can be .txt, .pcl, .ps, or supported PDF
		File file = new File(filePath);

        int timeoutSeconds = 30;
        int waited = 0;

        while (!file.exists()) {
            System.out.println("Waiting for file to appear...");
            Thread.sleep(1000); // Wait 1 second
            waited++;

            if (waited >= timeoutSeconds) {
                System.out.println("Timeout reached. File not found.");
                return;
            }
        }
        		
        int printerPort = 9100; // Most printers listen on port 9100 for raw jobs

        try (Socket socket = new Socket()) {
            SocketAddress address = new InetSocketAddress(printerIp, printerPort);
            socket.connect(address, 5000); // 5000 milliseconds = 5 seconds

            try (OutputStream out = socket.getOutputStream();
                 FileInputStream fileInput = new FileInputStream(new File(filePath))) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                out.flush();
                showAutoClosingDialog(notificationText, "Alert", 3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        	sendtoPrinterJob(Config.officePrinter2IP,"All papers have been sent to LINE2/24 office!");
        }
	}
	
	   public void sendEmail()
	   {
	    	Properties props = new Properties();
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.host", "smtp.gmail.com");
	        props.put("mail.smtp.port", "587");

	        Session session = Session.getInstance(props,
	                new Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication("letitburn0001@gmail.com", "qybb syeg yief bphz");
	                    }
	                });

	        try {
	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress("letitburn0001@gmail.com"));
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
	            message.setSubject("Debone recap");

	            MimeBodyPart messageBodyPart = new MimeBodyPart();

	            MimeBodyPart attachmentPart = new MimeBodyPart();
	            attachmentPart.attachFile(new File("D:\\Users\\pdgwinterm7\\Desktop\\recap_output\\recap.pdf"));

	            Multipart multipart = new MimeMultipart();
	            //multipart.addBodyPart(messageBodyPart);
	            multipart.addBodyPart(attachmentPart);

	            message.setContent(multipart);

	            Transport.send(message);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	   
	   public void postRecap(breastGen breastExcel) throws Exception
	   {
	        LocalDateTime now = LocalDateTime.now();
	        
	        if(Config.dayTimeSaving == 1)
	        {
	        	now = LocalDateTime.now().plusHours(1);
	        }

	        // Format it as a string
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String formattedDateTime = now.format(formatter);
		   
		   	String message = "Last updated: " + formattedDateTime + "\n";
		   	message = message + "Total breast cases: " + breastExcel.getTotalCase();
	        // Define JSON body
		   	
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
	                .sslSocketFactory(getUnsafeSSLSocketFactory(), getTrustAllCertsManager())
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
	   
	   private static SSLSocketFactory getUnsafeSSLSocketFactory() throws Exception {
		    TrustManager[] trustAllCerts = new TrustManager[]{
		        new X509TrustManager() {
		            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
		            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
		        }
		    };

		    SSLContext sslContext = SSLContext.getInstance("SSL");
		    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		    return sslContext.getSocketFactory();
		}

		private static X509TrustManager getTrustAllCertsManager() {
		    return new X509TrustManager() {
		        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
		        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
		    };
		}

	   
	   public void showAutoClosingDialog(String message, String title, int timeoutMillis) {
	        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
	        JDialog dialog = pane.createDialog(null, title);

	        // Set dialog to not block the EDT
	        dialog.setModal(false);
	        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        dialog.setVisible(true);

	        // Create a timer to close the dialog
	        new Timer().schedule(new TimerTask() {
	            @Override
	            public void run() {
	                dialog.dispose();
	            }
	        }, timeoutMillis);
	    }
}