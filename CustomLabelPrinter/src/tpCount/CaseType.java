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
import java.util.HashMap;
import java.util.Map;

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

public class CaseType extends JPanel{
	private JFrame frame;
    public CaseType(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel autoPanel = new JPanel();
        autoPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel codePanel = new JPanel();
        codePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        
        JLabel label = new JLabel("Customer");
        JTextField customerField = new JTextField(10);
        inputPanel.add(label);
        inputPanel.add(customerField);
        
        
        JCheckBox checkBox = new JCheckBox("Auto split");
        JTextField caseQtyField = new JTextField(7);
        caseQtyField.setEnabled(false);
        setPlaceholder(caseQtyField,"# of cases");
        
        JLabel quantityLabel = new JLabel("Qty");
        JTextField quantityField = new JTextField(3);
        JLabel casePerPalletLabel = new JLabel("# cases");
        JTextField casePerPalletField = new JTextField(5);
        setPlaceholder(casePerPalletField,"optional");
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);
        quantityPanel.add(casePerPalletLabel);
        quantityPanel.add(casePerPalletField);
        
        JLabel codeLabel = new JLabel("Product code");
        JTextField codeField = new JTextField(8);
        setPlaceholder(codeField,"optional");
        codePanel.add(codeLabel);
        codePanel.add(codeField);
        codePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	caseQtyField.setEnabled(true);
                	quantityField.setEnabled(false);
                	casePerPalletField.setEnabled(false);
                } else {
                	caseQtyField.setEnabled(false);
                	quantityField.setEnabled(true);
                	casePerPalletField.setEnabled(true);
                }
            }
        });
        autoPanel.add(checkBox);
        autoPanel.add(caseQtyField);
        
        JButton button = new JButton("Print");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        // Add action listener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	button.setEnabled(false);
            	try {
            		String customer = customerField.getText();
               	   String productCode = codeField.getText().equals("optional") ? "" : codeField.getText();
            		if(checkBox.isSelected()) {
		                   String casesNumber = caseQtyField.getText().equals("# cases") ? "" : caseQtyField.getText(); 
		                   if(casesNumber.length() > 0)
	  	            	   {
		                	   Map<Integer,Integer> map = getMultiplesOf7(new HashMap<>(),Integer.valueOf(casesNumber));
		                	   String splitResult = "";
		                	   for(int caseQuantity : map.keySet())
		                	   {
		                		   splitResult += map.get(caseQuantity) + " pallets of " + caseQuantity + "\n";
		                		   for(int i = 1; i <= map.get(caseQuantity); i++)
		                		   {
		  		                    	String count = i + "/" + caseQuantity;
		 			                    printLabel(customer,productCode,count);
		                		   }
		                	   }
		                       JOptionPane.showMessageDialog(frame, "Label printed!\n" + splitResult, "Alert", JOptionPane.INFORMATION_MESSAGE);
	  	            	   } else {
	  	                       JOptionPane.showMessageDialog(frame, "Missing number of cases", "Error", JOptionPane.ERROR_MESSAGE);
	  	            	   }
            		} else {
            			if(quantityField.getText().length() > 0)
  	            	   {
  		                   int amount = Math.min(Integer.parseInt(quantityField.getText()), 30);
		                   String casesNumber = casePerPalletField.getText().equals("optional") ? "" : casePerPalletField.getText(); 
  		                   for(int i = 1;i <= amount;i++)
  		                    {
  		                    	String count = casesNumber.length() > 0 ? i + "/" + casesNumber: String.valueOf(i); 
 			                    printLabel(customer,productCode,count);
  		                    }
  		                   JOptionPane.showMessageDialog(frame, "Label printed!", "Alert", JOptionPane.INFORMATION_MESSAGE);
  	            	   } else {
  	                       JOptionPane.showMessageDialog(frame, "Missing quantity value", "Error", JOptionPane.ERROR_MESSAGE);
  	            	   }
            		}
            	} catch (Exception err)
            	{
            		
            	}
            	
            	// Create a Timer to re-enable the button after a delay
                Timer timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        button.setEnabled(true); // Re-enable the button
                    }
                });
                timer.setRepeats(false); // Make sure the timer only runs once
                timer.start(); // Start the timer
           
            }
        });

        
        checkBox.doClick();
        
        this.add(inputPanel);
        this.add(autoPanel);
        this.add(quantityPanel);
        this.add(codePanel);
        this.add(button);
    }
    
    public void printLabel(String customer,String productCode,String count)
    {
        String printerIP = "167.110.88.226";  // Replace with your printer's IP
        int port = 9100;  // Default port for network printing
        int quantity = 1;

        //customer
        int customerfontSize = calculateFontSize(customer,800,250);
        System.out.println(customerfontSize);
        int customertextWidth = customerfontSize * customer.length() / 2;  // Approximate text width
        String customerfontSizeString = String.valueOf(customerfontSize);
        int customerstartX = 1215 - 50 - ((1215 - customertextWidth) / 2);
        if(customer.length() < 4)
        {
        	customerstartX += 100;
        }
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

    
	 public static Map<Integer,Integer> getMultiplesOf7(Map<Integer,Integer> result,int number) {
		 if(number == 0)
		 {
			 return result;
		 }

        if (number <= 56) {
    		result.put(number, result.getOrDefault(number, 0) + 1);
            number -= number;
            return getMultiplesOf7(result,number);
        }
        
        if (number - 56 > 45 && number - 56 <= 56)
        {
        	result.put(56, result.getOrDefault(56, 0) + 1);
            number -= 56;
            return getMultiplesOf7(result,number);
        }
        
        if(number % 56 == 0)
        {
        	result.put(56, result.getOrDefault(56, 0) + 1);
            number -= 56;
            return getMultiplesOf7(result,number);
        }
		 
		if(number % 7 == 0 && number % 50 != 0)
		{
	        // List of priorities in descending order
	        int[] priorities = {49, 42, 35, 21, 14, 7};
	
	        // Subtract the largest multiples first
	        for (int value : priorities) {
	        	if (number >= value) {
	                result.put(value, result.getOrDefault(value, 0) + 1);
	                number -= value;
	                return getMultiplesOf7(result,number);
	            }
	        }
		} else {
			 if (number >= 50) {
	                result.put(50, result.getOrDefault(50, 0) + 1);
		            number -= 50;
	                return getMultiplesOf7(result,number);
		     }
		}

        return getMultiplesOf7(result,number);
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