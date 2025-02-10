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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

import config.Config;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GCweights extends JPanel{
	private JFrame frame;
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
        JTextField userField = new JTextField(8);
        userPanel.add(userLabel);
        userPanel.add(userField);
        
        JLabel passLabel = new JLabel("pass");
        JPasswordField  passField = new JPasswordField(5);
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
                         JOptionPane.showMessageDialog(frame, "Label printed!", "Alert", JOptionPane.INFORMATION_MESSAGE);
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
            	System.out.println(session);
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
            			for(Element tr : table.getElementsByTag("tr"))
            			{
            				String content = tr.html();
            				if(content.contains("105884") && content.toLowerCase().contains("pmambo"))
            				{
            					String trackingNum = tr.getElementsByTag("a").get(0).text();
            					if(list.contains(trackingNum))
            					{
            						list.remove(trackingNum);
            					} else {
                					list.add(trackingNum);
            					}
            				}
            			}
            			splitList(list);
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
	
	public void splitList(List<String> list) throws IOException
	{
        Path filePath = Paths.get("D:\\Users\\pdgwinterm7\\Desktop\\gcweights.txt");
        List<String> lines = Files.readAllLines(filePath);
        System.out.println("List size: " + list.size());
        while(lines.size() < list.size())
        {
        	//lines.add(randomWeight());
        	lines.add("2146");
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
	
	public String randomWeight()
	{
		Random random = new Random();
        int min = 2150;
        int max = 2158;
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