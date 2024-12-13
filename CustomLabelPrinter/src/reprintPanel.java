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

public class reprintPanel extends JPanel{
	private JFrame frame;
    boolean running = false;
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
        String[] items = {"Printer 1", "Printer 2", "Custom"};

        // Create a JComboBox with the items array
        JComboBox<String> dropdown = new JComboBox<>(items);
        dropdown.setPreferredSize(new Dimension(185, dropdown.getPreferredSize().height));
        
        printerInputPanel.add(label);
        printerInputPanel.add(dropdown);
        
        JLabel ipLabel = new JLabel("IP Address");
        JTextField ipAddressInput = new JTextField(10);
        ipAddressInputPanel.add(ipLabel);
        ipAddressInputPanel.add(ipAddressInput);
        
       dropdown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selectedItem = (String) dropdown.getSelectedItem();
                
                // Update the UI based on the selected item
                SwingUtilities.invokeLater(() -> {
                    if ("Custom".equals(selectedItem)) {
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
       JTextField delayInput2 = new JTextField(2);
       delayInput2.setEnabled(false);
       
       JCheckBox checkBox = new JCheckBox("Pause");
       
       checkBox.addItemListener(new ItemListener() {
           @Override
           public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.SELECTED) {
            	   delayInput.setEnabled(true);
            	   delayInput2.setEnabled(true);
               } else {
            	   delayInput.setEnabled(false);
                   delayInput2.setEnabled(false);
               }
           }
       });
       
       setPlaceholder(delayInput,"2200");
       JLabel delayLabel = new JLabel("ms");
       JLabel delayLabel2 = new JLabel("every");
       JLabel delayLabel3 = new JLabel("labels");
       pausePanel.add(checkBox);
       pausePanel.add(delayInput);
       pausePanel.add(delayLabel);
       pausePanel.add(delayLabel2);
       pausePanel.add(delayInput2);
       pausePanel.add(delayLabel3);

        printerPanel.add(printerInputPanel);
        
        JButton button = new JButton("Start");
        button.setIcon(new GreenPlayIcon(10,10));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(!running)
            	{
            		running = true;
            		button.setText("Stop");
                    button.setIcon(new RedStopIcon(10,10));
            	} else {
            		running = false;
            		button.setText("Start");
                    button.setIcon(new GreenPlayIcon(10,10));

            	}
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
