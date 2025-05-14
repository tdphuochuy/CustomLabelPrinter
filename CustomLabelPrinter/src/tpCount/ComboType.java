package tpCount;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalTime;

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

public class ComboType extends JPanel{
	private JFrame frame;
    public ComboType(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel codePanel = new JPanel();
        codePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel rangePanel = new JPanel();
        rangePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Center the label and text field
        JPanel weightPanel = new JPanel();
        weightPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel customerlabel = new JLabel("Customer");
        JTextField customerField = new JTextField(10);
        customerPanel.add(customerlabel);
        customerPanel.add(customerField);
        
        JLabel codeLabel = new JLabel("Product code");
        JTextField codeField = new JTextField(8);
        setPlaceholder(codeField,"optional");
        codePanel.add(codeLabel);
        codePanel.add(codeField);
        
        JLabel quantityLabel = new JLabel("Qty");
        JTextField quantityField = new JTextField(3);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);
        
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
        rangePanel.add(checkBox);
        rangePanel.add(rangeField1);
        rangePanel.add(label3);
        rangePanel.add(rangeField2);
        
        JLabel weightLabel = new JLabel("Combo's weight");
        JTextField weightField = new JTextField(7);
        setPlaceholder(weightField,"optional");
        weightPanel.add(weightLabel);
        weightPanel.add(weightField);
        weightPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JButton button = new JButton("Print");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                try {
              	   String customer = customerField.getText();
              	   String productCode = codeField.getText().equals("optional") ? "" : codeField.getText();
              	   String weight = weightField.getText().equals("optional") ? "" : weightField.getText();;
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
 			                        String count = weight.length() > 0 ? i + "/" + weight: String.valueOf(i); 
 			                        printLabel(customer,productCode,count);
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
 		                   int amount = Math.min(Integer.parseInt(quantityField.getText()), 50);
 		                    for(int i = 1;i <= amount;i++)
 		                    {
 		                    	String count = weight.length() > 0 ? i + "/" + weight: String.valueOf(i); 
			                    printLabel(customer,productCode,count);
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
        
        this.add(customerPanel);
        this.add(quantityPanel);
        this.add(rangePanel);
        this.add(codePanel);
        this.add(weightPanel);
        this.add(button);
    }
    
    public void printLabel(String customer,String productCode,String count)
    {
        String printerIP = Config.printerIP;  // Replace with your printer's IP
        int port = 9100;  // Default port for network printing
        int quantity = 1;

        //customer
        int[] customerfontSize = calculateFontSize(customer,240);
        String customerfontSizeString = String.valueOf(customerfontSize[0]);
        int customerstartX = 18 + (1200/2 + customerfontSize[1]/2);
        //count
        int countfontSize = 120;
        System.out.println(countfontSize);
        int counttextWidth = countfontSize * count.length() / 2;  // Approximate text width
        String countfontSizeString = String.valueOf(countfontSize);
        int countstartX = 1250 - ((1215 - counttextWidth) / 2);
        
        //productCode
        int productCodefontSize = 80;
        System.out.println(productCodefontSize);
        int productCodetextWidth = productCodefontSize * productCode.length() / 2;  // Approximate text width
        String productCodefontSizeString = String.valueOf(productCodefontSize);
        int productCodestartX = 1250 - ((1215 - productCodetextWidth) / 2);

        // SBPL command to print "G" in the middle of an empty label
        String sbplCommand = "\u001BA"      // Initialize SBPL command
                           + "\u001B%1"
                            + "\u001BH30"  
                            + "\u001BL1010"
                            + "\u001BV" + customerstartX                                                                                      
                           + "\u001BRH0,SATOALPHABC.ttf,0," + customerfontSizeString + "," + customerfontSizeString + "," + customer;
        
        sbplCommand = sbplCommand + "\u001BH450"  
                + "\u001BV" + countstartX                                                                                          
                + "\u001BRH0,SATOCGStream.ttf,0," + countfontSizeString + "," + countfontSizeString + "," + count;
        
        //product code
        if(productCode.length() > 0)
        {
        	sbplCommand = sbplCommand + "\u001BH585"  
                           + "\u001BV" + productCodestartX                                                                                          
                           + "\u001BRH0,SATOCGStream.ttf,0," + productCodefontSizeString + "," + productCodefontSizeString + "," + productCode;
        }
        
        
        sbplCommand = sbplCommand + "\u001BCS10" + "\u001BQ" + quantity + "\u001BZ";     // End SBPL command

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
}