import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

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

public class GCweights extends JPanel{
	private JFrame frame;
	private String recipient = "tdphuochuy@gmail.com";
    public GCweights(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        passPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel label = new JLabel("Order #");
        JTextField textField = new JTextField(15);
        setPlaceholder(textField,"Order #");
        inputPanel.add(label);
        inputPanel.add(textField);
        
        JLabel userLabel = new JLabel("username");
        JTextField userField = new JTextField("pmambo",8);
        userPanel.add(userLabel);
        userPanel.add(userField);
        
        JLabel passLabel = new JLabel("pass");
        JPasswordField  passField = new JPasswordField("4292",5);
        passPanel.add(passLabel);
        passPanel.add(passField);
 
        JButton button = new JButton("Print");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                try {
              	   String text = textField.equals("Order #") ? "" : textField.getText();
              	   if(text.length() > 0)
              	   {
              		 if(userField.getText().length() > 0)
              		 {
              			if(passField.getText().length() > 0)
                 		{
	              			 String username = userField.getText();
	              			 String password = passField.getText();
	              			 String orderNum = textField.getText();
	              			 run(username,password,orderNum);
                 		 } else {
  	                       JOptionPane.showMessageDialog(frame, "Missing password", "Error", JOptionPane.ERROR_MESSAGE);
                  		 }
              		 } else {
	                       JOptionPane.showMessageDialog(frame, "Missing username", "Error", JOptionPane.ERROR_MESSAGE);
              		 }
              	   } else {
	                       JOptionPane.showMessageDialog(frame, "Missing order #", "Error", JOptionPane.ERROR_MESSAGE);
              	   }
                } catch (Exception ex)
                {
             	   
                }
                // Create a Timer to re-enable the button after a delay
                Timer timer = new Timer(300, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        button.setEnabled(true); // Re-enable the button
                        userField.setText("pmambo");
                        passField.setText("4292");
                    }
                });
                timer.setRepeats(false); // Make sure the timer only runs once
                timer.start(); // Start the timer
            }
        });
        
        this.add(inputPanel);
        this.add(userPanel);
        this.add(passPanel);
        this.add(button);
        
        File file = new File("D:\\Users\\pdgwinterm7\\Desktop\\gcweights.txt");

        try (FileWriter writer = new FileWriter(file)) {
            // Opening the file in write mode will automatically overwrite the contents, 
            // so the file will be cleared.
            writer.write(""); // Clear the file by writing an empty string
            System.out.println("File has been cleared.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void run(String username,String pass,String orderNum)
	{
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
            	getTrackingNum(client,session,orderNum,username);
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void getTrackingNum(OkHttpClient client,String sessionid,String orderNum,String username)
	{
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
            	List<String> list = new ArrayList<>();
            	Document doc = Jsoup.parse(body);
            	Element bodyElement = doc.body();
            	Element inputElement = bodyElement.select("[name=unnamed]").first();
            	for(Element table : inputElement.getElementsByTag("table"))
            	{
            		if(table.html().toLowerCase().contains(username.toLowerCase()))
            		{
            			int count = 0;
            			for(Element tr : table.getElementsByTag("tr"))
            			{
            				String content = tr.html();
            				if(content.contains("105884") && content.toLowerCase().contains(username.toLowerCase()))
            				{
            					if(!content.contains("2,000.00") && count > 10)
            					{
            						continue;
            					}
            					String trackingNum = tr.getElementsByTag("a").get(0).text();
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
            			if(username.toLowerCase().equals("pmambo"))
            			{
            				generatePdf(list,generateWeightList(list,"2146",true));
            			} else {
            				generatePdf(list,generateWeightList(list,"2140",false));
            			}
        				//splitList(list);
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
	        	lines.add(randomWeight());
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
	
	public void splitList(List<String> list) throws IOException
	{
        Path filePath = Paths.get("D:\\Users\\pdgwinterm7\\Desktop\\gcweights.txt");
        List<String> lines = Files.readAllLines(filePath);
        System.out.println("List size: " + list.size());
        while(lines.size() < list.size())
        {
        	lines.add(randomWeight());
        	//lines.add("2146");
        }
		int chunkSize = 19;
		 for (int i = 0; i < list.size(); i += chunkSize) {
	            int end = Math.min(i + chunkSize, list.size());
	            List<String> trackingChunk = list.subList(i, end);
	            List<String> weightChunk = lines.subList(i, end);
	            // Process the chunk (for example, print it)
	            printList(trackingChunk,weightChunk,i);
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
         JOptionPane.showMessageDialog(frame, "Label printed!", "Alert", JOptionPane.INFORMATION_MESSAGE);
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
	        
	        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	        String formattedDate = dateFormat.format(new Date());
	        // Date
	        document.add(new Paragraph("Date: " + formattedDate).setFontSize(10).setMarginLeft(30));
			
            int end = Math.min(i + chunkSize, trackingList.size());
            List<String> trackingChunk = trackingList.subList(i, end);
            List<String> weightChunk = weightList.subList(i, end);
            document.add(pdfTableGen(trackingChunk,weightList));
            if(end < trackingList.size())
            {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }
        
       // document.add(table);
        document.close();

        System.out.println("PDF created: " + new File(dest).getAbsolutePath());
        sendtoPrinterJob();
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
	
	public void sendtoPrinterJob()
	{
		String printerIp = "167.110.88.204"; // Replace with your Ricoh's IP
        int printerPort = 9100; // Most printers listen on port 9100 for raw jobs
        String filePath = "D:/Users/pdgwinterm7/Desktop/GoldCreek_TareWeightLog.pdf"; // Can be .txt, .pcl, .ps, or supported PDF

        try (Socket socket = new Socket(printerIp, printerPort);
             OutputStream out = socket.getOutputStream();
             FileInputStream fileInput = new FileInputStream(new File(filePath))) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInput.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
            System.out.println("File sent to printer successfully.");
            JOptionPane.showMessageDialog(frame, "File sent to the office!", "Alert", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
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
	            message.setSubject("Gold Creek Tare Weight Log");

	            MimeBodyPart messageBodyPart = new MimeBodyPart();

	            MimeBodyPart attachmentPart = new MimeBodyPart();
	            attachmentPart.attachFile(new File("GoldCreek_TareWeightLog.pdf"));

	            Multipart multipart = new MimeMultipart();
	            //multipart.addBodyPart(messageBodyPart);
	            multipart.addBodyPart(attachmentPart);

	            message.setContent(multipart);

	            Transport.send(message);

                JOptionPane.showMessageDialog(frame, "Email sent!", "Alert", JOptionPane.INFORMATION_MESSAGE);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	
	public String randomWeight()
	{
		Random random = new Random();
        int min = 2146;
        int max = 2154;
        int randomEvenNumber;

        do {
            randomEvenNumber = random.nextInt((max - min) + 1) + min;
        } while (randomEvenNumber % 2 != 0);
        
        return String.valueOf(randomEvenNumber);
    }
	
    
    // Method to set the placeholder text
    public void setPlaceholder(JTextField textField, String placeholderText) {
        // Set initial placeholder
        textField.setText(placeholderText);
        textField.setForeground(Color.GRAY); // Set the placeholder text color

        // Add FocusListener to manage the prompt behavior
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // When the user clicks on the text field, clear the placeholder if it's still there
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Set the text color to normal when typing
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // When the text field loses focus, reset the placeholder if the field is empty
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholderText);
                    textField.setForeground(Color.GRAY); // Set the placeholder text color back
                }
            }
        });
    }
}