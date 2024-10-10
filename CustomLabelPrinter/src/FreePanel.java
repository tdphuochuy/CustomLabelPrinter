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

public class FreePanel extends JPanel{
	private JFrame frame;
    public FreePanel(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel label = new JLabel("Text");
        JTextField textField = new JTextField(15);
        setPlaceholder(textField,"Any text");
        inputPanel.add(label);
        inputPanel.add(textField);
        
        JLabel quantityLabel = new JLabel("Qty");
        JTextField quantityField = new JTextField(3);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);
 
        JButton button = new JButton("Print");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                try {
              	   String text = textField.equals("Any text") ? "" : textField.getText();
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
        this.add(quantityPanel);
        this.add(button);
    }
    
    public void printLabel(String text)
    {
        String printerIP = "167.110.88.226";  // Replace with your printer's IP
        int port = 9100;  // Default port for network printing
        int quantity = 1;

        //text
        int textfontSize = calculateFontSize(text,800,300);
        System.out.println(textfontSize);
        int texttextWidth = textfontSize * text.length() / 2;  // Approximate text width
        String textfontSizeString = String.valueOf(textfontSize);
        int textstartX = 1215 - ((1215 - texttextWidth) / 2);
     

        // SBPL command to print "G" in the middle of an empty label
        String sbplCommand = "\u001BA"      // Initialize SBPL command
                           + "\u001B%1"
                            + "\u001BH50"  
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