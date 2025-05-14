import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import config.Config;

public class countComboPanel extends JPanel{
	private JFrame frame;
    public countComboPanel(JFrame frame) {
    	this.frame = frame;
       this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Set the main layout to BoxLayout (vertical)
        // Create a panel for the label and text field with FlowLayout (they will be on the same line)
       JPanel inputPanel = new JPanel();
       inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Center the label and text field
       JPanel optionalPanel = new JPanel();
       optionalPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Center
       JPanel quantityPanel = new JPanel();
       quantityPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Center the label and text field
       JPanel rangePanel = new JPanel();
       rangePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Center the label and text field
       rangePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

       // Create a label
       JLabel label = new JLabel("Name");

       // Create a text field (textbox) with 20 columns
       JTextField textField = new JTextField(15);

       // Add the label and text field to the input panel
       inputPanel.add(label);
       inputPanel.add(textField);
       
       JLabel prodNumlabel = new JLabel("Product #");
       // Create a text field (textbox) with 20 columns
       JTextField prodNumField = new JTextField(5);
       setPlaceholder(prodNumField,"optional");

       optionalPanel.add(prodNumlabel);
       optionalPanel.add(prodNumField);

       JLabel quantityLabel = new JLabel("Qty");
       // Create a text field (textbox) with 20 columns
       JTextField quantityField = new JTextField(3);
       // Add the label and text field to the input panel
       
       JLabel label3 = new JLabel("to");
       JTextField rangeField1 = new JTextField(2);
       rangeField1.setEnabled(false);
       JTextField rangeField2 = new JTextField(2);
       rangeField2.setEnabled(false);
       JCheckBox checkBox = new JCheckBox("Range");
       
       checkBox.addItemListener(new ItemListener() {
           @Override
           public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.SELECTED) {
                   rangeField1.setEnabled(true);
                   rangeField2.setEnabled(true);
                   quantityField.setEnabled(false);
               } else {
                   rangeField1.setEnabled(false);
                   rangeField2.setEnabled(false);
                   quantityField.setEnabled(true);
               }
           }
       });
       
       quantityPanel.add(quantityLabel);
       quantityPanel.add(quantityField);
       
       
       
       rangePanel.add(checkBox);
       rangePanel.add(rangeField1);
       rangePanel.add(label3);
       rangePanel.add(rangeField2);
       // Create a button
       JButton button = new JButton("Print");

       // Add action listener to the button
       button.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               button.setEnabled(false);
           	   String productCode = prodNumField.getText().equals("optional") ? "" : prodNumField.getText();
               try {
	               if(checkBox.isSelected())
	               {
	            	   if(rangeField1.getText().length() > 0 && rangeField2.getText().length() > 0)
	            	   {
		                   int range1 = Integer.parseInt(rangeField1.getText());
		                   int range2 = Integer.parseInt(rangeField2.getText());
		                   if(range2 - range1 > 0)
		                   {
			                   for(int i = range1; i <=  range2;i++)
			                   {
			                        printLabel(textField.getText(),String.valueOf(i),productCode);
			                   }
			                   JOptionPane.showMessageDialog(frame, "Label printed!", "Alert", JOptionPane.INFORMATION_MESSAGE);
		                   } else {
		                       JOptionPane.showMessageDialog(frame, "Invalid range", "Error", JOptionPane.ERROR_MESSAGE);
		            	   }
	            	   } else {
	                       JOptionPane.showMessageDialog(frame, "Missing range value", "Error", JOptionPane.ERROR_MESSAGE);
	            	   }
	               } else {
	            	   if(quantityField.getText().length() > 0)
	            	   {
		                   int amount = Math.min(Integer.parseInt(quantityField.getText()), 45);
		                    for(int i = 1;i <= amount;i++)
		                    {
		                        printLabel(textField.getText(),String.valueOf(i),productCode);
		                    }
		                    JOptionPane.showMessageDialog(frame, "Label printed!", "Alert", JOptionPane.INFORMATION_MESSAGE);
	            	   } else {
	                       JOptionPane.showMessageDialog(frame, "Missing quantity value", "Error", JOptionPane.ERROR_MESSAGE);
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

       // Center align the button
       button.setAlignmentX(Component.CENTER_ALIGNMENT);
       button.setBackground(Color.white);
       
       this.add(inputPanel);
       this.add(Box.createRigidArea(new Dimension(0, 2)));
       this.add(optionalPanel);
       this.add(quantityPanel);// Adds the panel containing label and text field
       this.add(rangePanel);
       this.add(button);      // Adds the button below
    }
    
       public void printLabel(String text1,String text2,String productCode)
        {
            String printerIP = Config.printerIP;  // Replace with your printer's IP
            int port = 9100;  // Default port for network printing
            int quantity = 1;

            int[] fontSize = calculateFontSize(text1,240);
            int textWidth = fontSize[1];  // Approximate text width
            String fontSizeString = String.valueOf(fontSize[0]);
            int startX = 18 + (1200/2 + fontSize[1]/2);
            if(text1.length() < 4)
            {
            	startX += 100;
            }
            
            int fontSize2 = calculateFontSize(text2,800,400);
            int textWidth2 = fontSize2 * text2.length() / 2;  // Approximate text width
            String fontSizeString2 = String.valueOf(fontSize2);
            int startX2 = 1215 - ((1215 - textWidth2) / 2);
            System.out.println(startX2);

            // SBPL command to print "G" in the middle of an empty label
            String sbplCommand = "\u001BA"      // Initialize SBPL command
                               + "\u001B%1"
                                + "\u001BH1"  // Set horizontal position (H)
                                + "\u001BL1010"
                                + "\u001BV" + startX                                                                                           // Set vertical position (V)        // Print "G"
                               + "\u001BRH0,SATOALPHABC.ttf,0," + fontSizeString + "," + fontSizeString + "," + text1;
            if(productCode.length() > 0)
            {
            	int productCodefontSize = 120;
                System.out.println(productCodefontSize);
                int productCodetextWidth = productCodefontSize * productCode.length() / 2;  // Approximate text width
                String productCodefontSizeString = String.valueOf(productCodefontSize);
                int productCodestartX = 1250 - ((1215 - productCodetextWidth) / 2);
            
                sbplCommand = sbplCommand + "\u001BH285"  
                        + "\u001BV" + productCodestartX
                        + "\u001BL1010"
                        + "\u001BRH0,SATOALPHABC.ttf,0," + productCodefontSizeString + "," + productCodefontSizeString + "," + productCode;
             
            }
            
            sbplCommand = sbplCommand + (productCode.length() > 0? "\u001BH410":"\u001BH360")  // Set horizontal position (H)
                               + "\u001BV" + startX2                                                                                           // Set vertical position (V)        // Print "G"
                               + "\u001BRH0,SATO0.ttf,0," + fontSizeString2 + "," + fontSizeString2 + "," + text2
                               + "\u001BCS10"
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
       
       public int calculateFontSize(String text, int maxWidth, int maxHeight) {
           // Approximate width per character (in dots)
           int charWidth = 100;
           int charHeight = maxHeight;  // This is an example; you can adjust based on your printer's settings

           // Calculate the required width and height of the text block
           int requiredWidth = text.length() * charWidth;
           int requiredHeight = charHeight;

           // Calculate the scaling factor to fit within the 300x300 area
           double widthScale = (double) maxWidth / requiredWidth;
           double heightScale = (double) maxHeight / requiredHeight;

           // Choose the smaller scale factor to ensure the text fits both horizontally and vertically
           double scale = Math.min(widthScale, heightScale);

           // Calculate the adjusted font size
           int adjustedFontSize = (int) (charHeight * scale);

           // Return the calculated font size
           return adjustedFontSize;
       }
       
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

       
       public static boolean isAllCapital(String text) {
           // Check if the text is not null and equals its uppercase version
           return text != null && text.equals(text.toUpperCase());
       }

}