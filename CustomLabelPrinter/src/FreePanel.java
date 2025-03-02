import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.awt.font.TextAttribute;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import config.Config;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class FreePanel extends JPanel{
	private JFrame frame;
	private boolean multipleLines;
    public FreePanel(JFrame frame) {
    	this.frame = frame;
    	this.multipleLines = false;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); 
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JLabel label = new JLabel("Text");
        JTextField textField = new JTextField(15);
        setPlaceholder(textField,"Any text");
        IconFontSwing.register(FontAwesome.getIconFont());

        Icon icon = IconFontSwing.buildIcon(FontAwesome.REFRESH, 10);
        JButton switchBtn = new JButton(icon);
        switchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        switchBtn.setBackground(getBackground());
        switchBtn.setOpaque(false); // Makes background transparent
        switchBtn.setContentAreaFilled(false); // No default fill
        //switchBtn.setBorderPainted(false); // Hides border
        inputPanel.add(label);
        inputPanel.add(textField);
        
        JLabel quantityLabel = new JLabel("Qty");
        JTextField quantityField = new JTextField(3);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);
 
        JTextArea textArea = new JTextArea(12,22);
        setPlaceholderArea(textArea,"Separated by new lines, # for quantity");
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);        
        
        JButton button = new JButton("Print");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                try {
                	System.out.println(multipleLines);
                	if(multipleLines)
                	{
 	              	   String text = textArea.getText().equals("Separated by new lines, # for quantity") ? "" : textArea.getText();
	              	   if(text.length() > 0)
	              	   {
	              		   String[] lines = text.split("\n");
	              		   for (String line : lines) {
	              			   System.out.println(line);
	              			   int quantity = 1;
	              			   String[] lineText = line.split("#");
	              			   if(lineText.length > 1)
	              			   {
	              				 quantity = Integer.parseInt(lineText[1]);
	              			   }
	              			   for(int i = 0; i < quantity;i++)
	              			   {
	  	              			 printLabel(lineText[0]);
	              			   }
	              		   }
	              	   }else 
	              	   {
	                       JOptionPane.showMessageDialog(frame, "Missing text", "Error", JOptionPane.ERROR_MESSAGE);
	              	   }
                	} else {
	              	   String text = textField.getText().equals("Any text") ? "" : textField.getText();
	              	   if(text.length() > 0)
	              	   {
	              		 int quantity = quantityField.getText().length() > 0 ? Integer.valueOf(quantityField.getText()) : 1;
	              		 for(int i = 0; i < quantity;i++)
	              		 {
	              			 printLabel(text);
	              		 }
	                     JOptionPane.showMessageDialog(frame, "Label printed!", "Alert", JOptionPane.INFORMATION_MESSAGE);
	              	   } else {
		                       JOptionPane.showMessageDialog(frame, "Missing text", "Error", JOptionPane.ERROR_MESSAGE);
	              	   }
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
        
        switchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	contentPanel.removeAll();
               if(multipleLines)
               {
            	   multipleLines = false;
            	   contentPanel.add(inputPanel);
            	   contentPanel.add(quantityPanel);
               } else {
            	   multipleLines = true;
            	   contentPanel.add(scrollPane);
               }
               contentPanel.revalidate();
               contentPanel.repaint();
            }
        });
        
        JButton buttonNhan = new JButton("Click this :)");
        buttonNhan.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonNhan.setBackground(Color.white);
        buttonNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
     // Make the button transparent
        buttonNhan.setContentAreaFilled(false);
        buttonNhan.setBorder(null);

        // Set an underlined font
        Font font = new Font("Arial", Font.PLAIN, 12);
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        Font underlinedFont = font.deriveFont(attributes);
        //buttonNhan.setFont(underlinedFont);
        
        buttonNhan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int quantity = quantityField.getText().length() > 0 ? Integer.valueOf(quantityField.getText()) : 1;
         		 for(int i = 0; i < quantity;i++)
         		 {
         			 printNhan();
         		 }
            }
        });
        
        contentPanel.add(inputPanel);
        contentPanel.add(quantityPanel);
        
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(switchBtn);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(button);
        buttonPanel.add(Box.createVerticalStrut(150));
        buttonPanel.add(buttonNhan);
        
        this.add(contentPanel);
        this.add(buttonPanel);
    }
    
    public void printLabel(String text)
    {
        String printerIP = Config.printerIP;  // Replace with your printer's IP
        int port = 9100;  // Default port for network printing
        int quantity = 1;

        //text
        int[] textfontSize = calculateFontSize(text,240);
        System.out.println(textfontSize);
        int texttextWidth = textfontSize[1];  // Approximate text width
        String textfontSizeString = String.valueOf(textfontSize[0]);
        int textstartX = 18 + (1200/2 + textfontSize[1]/2);
     

        // SBPL command to print "G" in the middle of an empty label
        String sbplCommand = "\u001BA"      // Initialize SBPL command
                           + "\u001B%1"
                            + "\u001BH50"
                            + "\u001BL1010"
                            + "\u001BV" + textstartX                                                                                      
                           + "\u001BRH0,SATOALPHABC.ttf,0," + textfontSizeString + "," + textfontSizeString + "," + text;
        
        
        sbplCommand = sbplCommand + "\u001BQ" + quantity + "\u001BZ";     // End SBPL command

        try (Socket socket = new Socket(printerIP, port)) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(sbplCommand.getBytes("UTF-8"));
            outputStream.flush();
            System.out.println("Label sent to the printer.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void printNhan()
    {
        String printerIP = Config.printerIP;  // Replace with your printer's IP
        int port = 9100;  // Default port for network printing
        int quantity = 1;
        
    	 String text1 = "1st shift - ERP";
         String text2 = "Please don't throw";
         String text3 = "it away :)";
         
         int[] arr = calculateFontSize(text1,240);
         int fontSize = arr[0];
         int[] arr2 = calculateFontSize(text2,240);
         int fontSize2 = arr2[0];
         int[] arr3 = calculateFontSize(text3,240);
         int fontSize3 = arr2[0];

         String fontSizeString = String.valueOf(fontSize);
         String fontSizeString2 = String.valueOf(fontSize2);
         String fontSizeString3 = String.valueOf(fontSize3);

         int textWidth = arr[1];
         int startX = 18 + (1200/2 + textWidth/2);
         
         int textWidth2 = arr2[1];
         int startX2 = 18 + (1200/2 + textWidth2/2);

         int textWidth3 = arr3[1];
         int startX3 = 18 + (1200/2 + textWidth3/2) - 250;
         
         System.out.println(startX);
         // SBPL command to print "G" in the middle of an empty label
         String sbplCommand = "\u001BA"      // Initialize SBPL command
                            + "\u001B%1"
                            + "\u001BH100"  // Set horizontal position (H)
                            + "\u001BV" + startX                                                                                           // Set vertical position (V)        // Print "G"
                            + "\u001BL1010"
                            + "\u001BRH0,SATO0.ttf,0," + fontSizeString + "," + fontSizeString + "," + text1
                            
                            + "\u001BH350"  // Set horizontal position (H)
                            + "\u001BV" + startX2                                                                                           // Set vertical position (V)        // Print "G"
                            + "\u001BL1010"
                            + "\u001BRH0,SATO0.ttf,0," + fontSizeString2 + "," + fontSizeString2 + "," + text2
                            
                            + "\u001BH550"  // Set horizontal position (H)
                            + "\u001BV" + startX3                                                                                           // Set vertical position (V)        // Print "G"
                            + "\u001BL1010"
                            + "\u001BRH0,SATO0.ttf,0," + fontSizeString3 + "," + fontSizeString3 + "," + text3
                            
                           + "\u001BQ" + quantity     // Print one label
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
    
    public static int[] calculateFontSize(String text, int maxWidth) {
    	int [] arr=new int [2];
    	double textRatio = 2.47422;
    	double spaceRatio = 13.3333;
    	if(isAllCapital(text))
    	{
    		textRatio = 2.12389;
    		spaceRatio = 8.57142;
    	}
    	int textLength = text.length();
    	int singleTextRealLength = (int) Math.floor(maxWidth / textRatio); 
    	int spaceLength = textLength > 1 ? (int) Math.floor(maxWidth / spaceRatio) * (textLength - 1) : 0;
    	int fullTextLength = (singleTextRealLength * textLength) + spaceLength;
       	System.out.println("Old text font " + maxWidth);
    	System.out.println("Old text length " + fullTextLength);
    	if(fullTextLength < 1200)
    	{
    		arr[0] = maxWidth;
    		arr[1] = fullTextLength;
    		return arr;
    	}
    	
    	double adjustedFontSizedouble =maxWidth* ((double)1200/fullTextLength);
    	int adjustedFontSize = (int) Math.floor(adjustedFontSizedouble);
    	int newSingleTextRealLength = (int) Math.floor(adjustedFontSize / textRatio); 
    	int newSpaceLength = textLength > 1 ? (int) Math.floor(adjustedFontSize / spaceRatio) * (textLength - 1) : 0;
    	int newFullTextLength = (newSingleTextRealLength * textLength) + newSpaceLength;
    	
    	System.out.println("New text font " + adjustedFontSize);
    	System.out.println("New text length " + newFullTextLength);
    	arr[0] = adjustedFontSize;
		arr[1] = newFullTextLength;
		return arr;
    }
    
    public static boolean isAllCapital(String text) {
        // Check if the text is not null and equals its uppercase version
        return text != null && text.equals(text.toUpperCase());
    }
    
    // Method to set the placeholder text
    public static void setPlaceholder(JTextField textField, String placeholderText) {
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
    
    public static void setPlaceholderArea(JTextArea textField, String placeholderText) {
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