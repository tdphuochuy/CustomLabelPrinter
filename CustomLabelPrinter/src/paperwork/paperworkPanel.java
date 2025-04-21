package paperwork;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class paperworkPanel extends JPanel{
	private JFrame frame;

	public paperworkPanel(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel inputPanel2 = new JPanel();
        inputPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel label = new JLabel("Order #");
        JTextField orderField = new JTextField(15);
        setPlaceholder(orderField,"Order #");
        inputPanel.add(label);
        inputPanel.add(orderField);
        
        JLabel label2 = new JLabel("Rework order #");
        JTextField reworkOrderField = new JTextField(11);
        setPlaceholder(reworkOrderField,"Order # (optional)");
        inputPanel2.add(label2);
        inputPanel2.add(reworkOrderField);
        
        JLabel userLabel = new JLabel("Username");
        JTextField userField = new JTextField("pmambo",8);
        userPanel.add(userLabel);
        userPanel.add(userField);
        
        JLabel passLabel = new JLabel("Pass");
        JPasswordField  passField = new JPasswordField("4292",5);
        passPanel.add(passLabel);
        passPanel.add(passField);
        
        JLabel nameLabel = new JLabel("Name");
        JTextField  nameField = new JTextField("Huy",5);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
 
        JButton button = new JButton("Game Over");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                try {
              	   String text = orderField.equals("Order #") ? "" : orderField.getText();
              	   if(text.length() > 0)
              	   {
              		 if(userField.getText().length() > 0)
              		 {
              			if(passField.getText().length() > 0)
                 		{
	              			 String username = userField.getText();
	              			 String password = passField.getText();
	              			 String orderNum = orderField.getText();
	              			 
	                         JOptionPane.showMessageDialog(frame, "File sent to the office!", "Alert", JOptionPane.INFORMATION_MESSAGE);
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
        this.add(inputPanel2);
        this.add(userPanel);
        this.add(passPanel);
        this.add(namePanel);
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