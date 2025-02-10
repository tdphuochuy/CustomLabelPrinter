import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import config.Config;

public class reprintPanel extends JPanel{
	private JFrame frame;
    boolean running = false;
    private reprintTask task;
    private Thread thread;
    public reprintPanel(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel printerPanel = new JPanel();
        printerPanel.setLayout(new BoxLayout(printerPanel, BoxLayout.Y_AXIS));
        
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JPanel pausePanel = new JPanel();
        pausePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        pausePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel printerInputPanel = new JPanel();
        printerInputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel ipAddressInputPanel = new JPanel();
        ipAddressInputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel label = new JLabel("Printer");
        String[] items = {"Right", "Left", "Custom"};
        Map<String,String> printerMap = new TreeMap<>();
        printerMap.put("Right",Config.printer2IP);
        printerMap.put("Left",Config.printerIP);
        Map<String,String> typeMap = new TreeMap<>();
        typeMap.put("Right", "SDPL");
        typeMap.put("Left", "SBPL");
        // Create a JComboBox with the items array
        JComboBox<String> printerDropDown = new JComboBox<>(items);
        printerDropDown.setPreferredSize(new Dimension(190, printerDropDown.getPreferredSize().height));
        
        printerInputPanel.add(label);
        printerInputPanel.add(printerDropDown);
        
        JLabel ipLabel = new JLabel("IP Address");
        String[] types = {"SDPL", "SBPL"};
        JComboBox<String> typeDropDown = new JComboBox<>(types);

        JTextField ipAddressInput = new JTextField(9);
        ipAddressInputPanel.add(ipLabel);
        ipAddressInputPanel.add(ipAddressInput);
        ipAddressInputPanel.add(typeDropDown);

        printerDropDown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selectedItem = (String) printerDropDown.getSelectedItem();
                
                // Update the UI based on the selected item
                SwingUtilities.invokeLater(() -> {
                    if (selectedItem.equals("Custom")) {
                        if (!printerPanel.isAncestorOf(ipAddressInputPanel)) {
                            printerPanel.add(ipAddressInputPanel);
                            frame.revalidate();
                            frame.repaint();
                        }
                    } else {
                        if (printerPanel.isAncestorOf(ipAddressInputPanel)) {
                            printerPanel.remove(ipAddressInputPanel);
                            frame.revalidate();
                            frame.repaint();
                        }
                    }
                });
            }
        });
       
       JLabel quantityLabel = new JLabel("Qty");
       JTextField quantityField = new JTextField(3);
       quantityPanel.add(quantityLabel);
       quantityPanel.add(quantityField);
       
       JTextField delayInput = new JTextField(3);
       delayInput.setEnabled(false);
       JTextField intervalInput = new JTextField(2);
       intervalInput.setEnabled(false);
       
       JCheckBox checkBox = new JCheckBox("Pause");
       
       checkBox.addItemListener(new ItemListener() {
           @Override
           public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.SELECTED) {
            	   delayInput.setEnabled(true);
            	   intervalInput.setEnabled(true);
               } else {
            	   delayInput.setEnabled(false);
                   intervalInput.setEnabled(false);
               }
           }
       });
       
       setPlaceholder(delayInput,"1500");
       JLabel delayLabel = new JLabel("ms");
       JLabel delayLabel2 = new JLabel("/");
       JLabel delayLabel3 = new JLabel("labels");
       pausePanel.add(checkBox);
       pausePanel.add(delayInput);
       pausePanel.add(delayLabel);
       pausePanel.add(delayLabel2);
       pausePanel.add(intervalInput);
       pausePanel.add(delayLabel3);

        printerPanel.add(printerInputPanel);
        
        JButton button = new JButton("Start");
        button.setIcon(new GreenPlayIcon(10,10));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);

            	if(!running)
            	{
            		running = true;
            		button.setText("Stop");
                    button.setIcon(new RedStopIcon(10,10));
                    
                    try {
	                    String ipAddress = "";
	                    String type = "";
	                    int quantity = 50;
	                    int delay = 1000;
	                    int interval = 0;
	                    
	                    String printer = (String) printerDropDown.getSelectedItem();
	                    if(printer.equals("Custom"))
	                    {
	                    	if(ipAddressInput.getText().length() > 0)
	                    	{
		                    	ipAddress = ipAddressInput.getText();
	                    	} else {
	   	                       JOptionPane.showMessageDialog(frame, "Missing IP address", "Error", JOptionPane.ERROR_MESSAGE);
	   	                       throw new Exception("Missing IP address"); 
	                    	}
	                    	type = (String) typeDropDown.getSelectedItem();
	                    } else {
	                    	ipAddress = printerMap.get(printer);
	                    	type = typeMap.get(printer);
	                    }
	                    
	                    if(quantityField.getText().length() > 0)
	                    {
	                    	quantity = Integer.parseInt(quantityField.getText());
	                    }
	                    
	                    if(checkBox.isSelected())
	                    {
	                    	delay = Integer.parseInt(delayInput.getText());
	                    	interval = intervalInput.getText().length() > 0 ? Integer.parseInt(intervalInput.getText()) : 0;
	                    }
	                    
	                    Runnable onCompletion = () -> {
	                    	running = false;
	                		button.setText("Start");
	                        button.setIcon(new GreenPlayIcon(10,10));
	                    };
	                    
	                    task = new reprintTask(ipAddress, type, quantity, delay, interval,onCompletion);
	
	                    // Create and start the thread
	                    thread = new Thread(task);
	                    thread.setDaemon(true);
	                    thread.start();
                    } catch (Exception err)
                    {
                    	running = false;
                		button.setText("Start");
                        button.setIcon(new GreenPlayIcon(10,10));
                    }
            	} else {
                    if (task != null) {
                        task.stop();
                    }
                    if (thread != null && thread.isAlive()) {
                        thread.interrupt();
                    }
                    
                    running = false;
            		button.setText("Start");
                    button.setIcon(new GreenPlayIcon(10,10));
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
        
        this.add(printerPanel);
        this.add(quantityPanel);
        this.add(pausePanel);
        this.add(button);
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

//Custom Green Play Button Icon (Triangle)
class GreenPlayIcon implements Icon {
 private int width;
 private int height;

 // Constructor to set width and height of the icon
 public GreenPlayIcon(int width, int height) {
     this.width = width;
     this.height = height;
 }

 @Override
 public int getIconWidth() {
     return width;
 }

 @Override
 public int getIconHeight() {
     return height;
 }

 @Override
 public void paintIcon(Component c, Graphics g, int x, int y) {
     // Set the color for the play button (green)
     g.setColor(Color.GREEN);

     // Draw a filled triangle (play button icon)
     int[] xPoints = {x, x + width, x};  // Triangle points for the play button
     int[] yPoints = {y, y + height / 2, y + height};
     g.fillPolygon(xPoints, yPoints, 3);
 }
}

class RedStopIcon implements Icon {
    private int width;
    private int height;

    // Constructor to set width and height of the icon
    public RedStopIcon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        // Set the color for the stop button (red)
        g.setColor(new Color(255, 110, 110));

        // Draw a filled rectangle (stop button icon)
        g.fillRect(x, y, width, height);
    }
}
