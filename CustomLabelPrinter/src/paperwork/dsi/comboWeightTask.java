package paperwork.dsi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;

import config.Config;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class comboWeightTask implements Runnable {
	private String username;
	private String pass;
	private String orderNum;
	private String reworkOrderNum;
	private String printType;
	private String productCode;
	private String comboWeight;

	public comboWeightTask(String username,String pass,String orderNum,String reworkOrderNum,String printType,String productCode,String comboWeight) {
		this.username = username;
		this.pass = pass;
		this.orderNum = orderNum;
		this.reworkOrderNum = reworkOrderNum;
		this.printType = printType;
		this.productCode = productCode;
		this.comboWeight = comboWeight;
	}

	@Override
	public void run() {
		OkHttpClient client = new OkHttpClient();
		FormBody formBody = new FormBody.Builder()
                .add("user", username)
                .add("pass", pass)
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
            	List<String> list = new ArrayList<>();
            	getTrackingNum(client,session,orderNum,list);
            	if(reworkOrderNum.length() > 0)
            	{
                	getTrackingNum(client,session,reworkOrderNum,list);
            	}
            	if(printType.equals("File"))
    			{
    				generatePdf(list,generateWeightList(list,comboWeight,true));
    			} else if (printType.equals("Fixed")) {
    				generatePdf(list,generateWeightList(list,comboWeight,false));
    			} else {
    				generatePdf(list,generateWeightList(list,"",false));
    			}
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	public void getTrackingNum(OkHttpClient client,String sessionid,String orderNum,List<String> list)
	{
		String weightLimit = "2,000.00";
		if(comboWeight.length() > 0)
		{
			if(Integer.parseInt(comboWeight) < 2000)
			{
				weightLimit = "1,800.00";
			}
		}
		
		FormBody formBody = new FormBody.Builder()
                .add("fileName", "reports/SingleOrderProductionViewer.txt")
                .add("Order", orderNum)
                .add("submit1", "Go")
                .add("r", "XMLReport")
                .add("f", "n")
                .add("session", sessionid)
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
            			int count = 0;
            			List<String> splitPalletlist = new ArrayList<>();
            			for(Element tr : table.getElementsByTag("tr"))
            			{
            				String content = tr.html();
            				if(content.contains(productCode) && content.toLowerCase().contains(username.toLowerCase()))
            				{
            					String trackingNum = tr.getElementsByTag("a").get(0).text();
            					if(!content.contains(weightLimit))
            					{
            						splitPalletlist.add(trackingNum);
            					}
            					if(list.contains(trackingNum))
            					{
            						count--;
            						list.remove(trackingNum);
            					} else {
            						count++;
                					list.add(trackingNum);
            					}
            				}
            			}
            			for(String trackingNum : splitPalletlist)
            			{
            				if(list.contains(trackingNum))
            				{
            					int trackingNumIndex = list.indexOf(trackingNum);
            					if(trackingNumIndex > (list.size() - 5))
            					{
            						list.remove(trackingNum);
            					}
            				}
            			}
            			break;
            		}
            	}
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public List<String> generateWeightList(List<String> list, String fixedWeight, boolean randomList) throws IOException
	{
		if(randomList)
		{
			Path filePath = Paths.get("D:\\Users\\pdgwinterm7\\Desktop\\gcweights.txt");
	        List<String> lines = Files.readAllLines(filePath);
	        System.out.println("List size: " + list.size());
	        while(lines.size() < list.size())
	        {
	        	lines.add(randomWeight(fixedWeight));
	        	//lines.add("2146");
	        }
	        
	        String weightExport = "";
			 for(String weight: lines)
			 {
				 weightExport += weight + "\n";
			 }
			 try (BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\Users\\pdgwinterm7\\Desktop\\gcweights.txt", false))) {
		            // Write the string to the file
		            writer.write(weightExport);
		            System.out.println("Data has been written to the file.");
		        } catch (IOException e) {
		            System.err.println("An error occurred while writing to the file.");
		            e.printStackTrace();
		        }
			 
			 return lines;
		} else {
			List<String> lines = new ArrayList<>();
	        System.out.println("List size: " + list.size());
	        while(lines.size() < list.size())
	        {
	        	lines.add(fixedWeight);
	        }
	        
	        return lines;
		}
		
	}
	
	public List<String> generateWeightList1st(List<String> list,int bottom)
	{
		String fileOutput = "";
		List<String> lines = new ArrayList<>();
		for(String trackingNum : list)
		{
			 char lastChar = trackingNum.charAt(trackingNum.length() - 1);
			 // Convert the character to an integer
			 int lastDigit = Character.getNumericValue(lastChar);
			 int weight = bottom - 6 + lastDigit;
			 if(weight % 2 != 0)
			 {
				 weight++;
			 }
			 fileOutput += weight + "\n";
			 //lines.add(String.valueOf(weight));
			 lines.add("21");
		}
		
		File file = new File("D:\\Users\\pdgwinterm7\\Desktop\\gcweights.txt");

        try (FileWriter writer = new FileWriter(file)) {
            // Opening the file in write mode will automatically overwrite the contents, 
            // so the file will be cleared.
            writer.write(fileOutput); // Clear the file by writing an empty string
            System.out.println("File has been overwriten.");
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return lines;
	}
	
	public void printList(List<String> trackingList,List<String> weightList,int listNum)
	{
        String printerIP = "167.110.88.226";  // Replace with your printer's IP
        int port = 9100;  // Default port for network printing
        
        String sbplCommand = "\u001BA";
        if(listNum == 0)
        {
        	sbplCommand = sbplCommand 
        			+ "\u001BH110" 
    				+ "\u001BV25"
        			+ "\u001BXB0" + "Gold creek (20177) weights";
        }
        
        int vertical = 35;
        if(listNum == 0)
        {
        	vertical = 95;
        }
        for(int i = 0;i < trackingList.size();i++)
        {
        	sbplCommand = sbplCommand + "\u001BH20" 
        				+ "\u001BV" + vertical
        				+ "\u001BXB0" + trackingList.get(i) + ": " + weightList.get(i) + " lbs";
        	vertical += 60;
        }
        
        sbplCommand += "\u001BQ1"     // Print one label
                + "\u001BZ";     // End SBPL command
        
        try (Socket socket = new Socket(printerIP, port)) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(sbplCommand.getBytes("UTF-8"));
            outputStream.flush();
            System.out.println("Label sent to the printer.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void generatePdf(List<String> trackingList,List<String> weightList) throws FileNotFoundException
	{
		String dest = "D:/Users/pdgwinterm7/Desktop/GoldCreek_TareWeightLog.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        com.itextpdf.layout.Document document = new  com.itextpdf.layout.Document(pdf);        

        int chunkSize = 35;
		for (int i = 0; i < trackingList.size(); i += chunkSize) {
			Paragraph title = new Paragraph("Gold Creek tare weight recording log")
	                .setFontSize(10)
	                .setMarginLeft(30);
	        document.add(title);
	        
	        // Date
	        document.add(new Paragraph("Date: " + getDate("MM/dd/yyyy")).setFontSize(10).setMarginLeft(30));
			
            int end = Math.min(i + chunkSize, trackingList.size());
            List<String> trackingChunk = trackingList.subList(i, end);
            List<String> weightChunk = weightList.subList(i, end);
            document.add(pdfTableGen(trackingChunk,weightChunk));
            if(end < trackingList.size())
            {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }
        
       // document.add(table);
        document.close();

        System.out.println("PDF created: " + new File(dest).getAbsolutePath());
    	sendtoPrinterJob(Config.officePrinterIP,"All papers have been sent to DEBONE office!");
        //sendEmail();
	}
	
	public Table pdfTableGen(List<String> list,List<String> weightlist)
	{
        float[] columnWidths = {100, 150, 100, 150};  // customize as needed
		Table table = new Table(columnWidths);
		        
		        table.setMarginLeft(30);
		        table.setMarginRight(30);
		        // Add headers
		        table.addHeaderCell(new Cell().add(new Paragraph("Product code").setFontSize(10).setMarginLeft(5)).setPadding(2).setHeight(14));
		        table.addHeaderCell(new Cell().add(new Paragraph("Tracking number").setFontSize(10).setMarginLeft(5)).setPadding(2).setHeight(14));
		        table.addHeaderCell(new Cell().add(new Paragraph("Total weight").setFontSize(10).setMarginLeft(5)).setPadding(2).setHeight(14));
		        table.addHeaderCell(new Cell().add(new Paragraph("Comment").setFontSize(10).setMarginLeft(5)).setPadding(2).setHeight(14));
		
		        // Add empty rows (e.g., 30 rows like the image)
		        int rowCount = 35;
		        for (int i = 0; i < rowCount; i++) {
		        	if(i < list.size())
		        	{
			        	String trackingNum = list.get(i);
			        	String weight = weightlist.get(i);
			            table.addCell(new Cell().add(new Paragraph("20177").setFontSize(10).setMarginLeft(5)));
			            table.addCell(new Cell().add(new Paragraph(trackingNum).setFontSize(10).setMarginLeft(5)));
			            table.addCell(new Cell().add(new Paragraph(weight).setFontSize(10).setMarginLeft(5)));
			            table.addCell(new Cell().add(new Paragraph(" ").setFontSize(10).setMarginLeft(5)));
		        	} else {
		        		table.addCell(new Cell().setHeight(14).add(new Paragraph("").setFontSize(10).setMarginLeft(5)));
			            table.addCell(new Cell().setHeight(14).add(new Paragraph("").setFontSize(10).setMarginLeft(5)));
			            table.addCell(new Cell().setHeight(14).add(new Paragraph("").setFontSize(10).setMarginLeft(5)));
			            table.addCell(new Cell().setHeight(14).add(new Paragraph(" ").setFontSize(10).setMarginLeft(5)));
		        	}
		        }
		        
		return table;
	}
	
	public void sendtoPrinterJob(String printerIp,String notificationText)
	{
        int printerPort = 9100; // Most printers listen on port 9100 for raw jobs
        String filePath = "D:/Users/pdgwinterm7/Desktop/GoldCreek_TareWeightLog.pdf"; // Can be .txt, .pcl, .ps, or supported PDF

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
	   
	   public String getDate(String dateFormat)
		{
			LocalDate today;
			LocalTime currentTime = LocalTime.now();
	        int currentHour = currentTime.getHour();
			if(currentHour < 5)
			{
				today = LocalDate.now().minusDays(1);
			} else {
				today= LocalDate.now();
			}
			
	        // Define the formatter for MMDD
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

	        // Format the date
	        String formattedDate = today.format(formatter);
	        
	        return formattedDate;
		}
	
	public String randomWeight(String weight)
	{
		Random random = new Random();
        int min = Integer.parseInt(weight) - 4;
        int max = Integer.parseInt(weight) + 4;
        int randomEvenNumber;

        do {
            randomEvenNumber = random.nextInt((max - min) + 1) + min;
        } while (randomEvenNumber % 2 != 0);
        
        return String.valueOf(randomEvenNumber);
    }
	
	   public static void showAutoClosingDialog(String message, String title, int timeoutMillis) {
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