package paperwork;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.json.simple.parser.ParseException;

import config.Config;
import paperwork.dsi.comboWeightTask;
import paperwork.dsi.paperworkDSIGen;

public class paperwork1stDSIPanel extends JPanel{
	private JFrame frame;
	private boolean isVerified = false;
	public paperwork1stDSIPanel(JFrame frame) {
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
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel checkboxesPanel = new JPanel();
        checkboxesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        checkboxesPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        
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
        JTextField userField = new JTextField("NPHAM",8);
        userPanel.add(userLabel);
        userPanel.add(userField);
        
        JLabel passLabel = new JLabel("Pass");
        JPasswordField  passField = new JPasswordField("2242",5);
        passPanel.add(passLabel);
        passPanel.add(passField);
        

        
        JLabel nameLabel = new JLabel("Name");
        JTextField  nameField = new JTextField("NHAN",5);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        

        
        JButton button = new JButton("Game Over");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        JCheckBox pdfOnlycb = new JCheckBox("Generate PDF only");
        checkboxesPanel.add(pdfOnlycb);
        JCheckBox sendEmailcb = new JCheckBox("Send email");
        sendEmailcb.setSelected(true);
        checkboxesPanel.add(sendEmailcb);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(inputPanel);
        mainPanel.add(inputPanel2);
        mainPanel.add(userPanel);
        mainPanel.add(passPanel);
        mainPanel.add(namePanel);
        mainPanel.add(timePanel);
        mainPanel.add(checkboxesPanel);
        mainPanel.add(button);
        
        JPanel textareaPanel = new JPanel();
        textareaPanel.setLayout(new BoxLayout(textareaPanel, BoxLayout.Y_AXIS));
        JTextArea textArea = new JTextArea(6,18);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);   
        
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("Bloody condemn:"));

        
        JPanel bloodCondenmnedPanel = new JPanel();
        bloodCondenmnedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        bloodCondenmnedPanel.add(new JLabel("Bloody Condemn"));
        JTextField bloodcondemnedField = new JTextField(5);
        bloodCondenmnedPanel.add(bloodcondemnedField);
        
        
        JPanel tenderCondenmnedPanel = new JPanel();
        tenderCondenmnedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        tenderCondenmnedPanel.add(new JLabel("Green Tenders:"));
        JTextField tendercondemnedField = new JTextField(5);
        tenderCondenmnedPanel.add(tendercondemnedField);
        
        JPanel breastSurplusPanel = new JPanel();
        breastSurplusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        breastSurplusPanel.add(new JLabel("Breast Combo Surplus:"));
        JTextField breastSurplusField = new JTextField("30",5);
        breastSurplusPanel.add(breastSurplusField);
        
        textareaPanel.add(bloodCondenmnedPanel);
        textareaPanel.add(tenderCondenmnedPanel);
        textareaPanel.add(breastSurplusPanel);
        
        
        
        
        
        
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        splitPanel.add(mainPanel);
        splitPanel.add(textareaPanel);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	while(true)
            	{
            	
	            	JPasswordField passwordField = new JPasswordField();
	                
	                JPanel panel = new JPanel();
	                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	                
	                JTextPane messageLabel1 = new JTextPane();
	         	   messageLabel1.setText("Please enter password");
	         	   messageLabel1.setEditable(false);
	         	   
	         	   panel.add(messageLabel1);
	                panel.add(Box.createVerticalStrut(5));
	                panel.add(passwordField);
	                
	         	   int option = JOptionPane.showConfirmDialog(
	                        null,
	                        panel
	                        ,
	                        "Một thứ gì đó kì lạ vừa xuất hiện :D",
	                        JOptionPane.OK_CANCEL_OPTION,
	                        JOptionPane.PLAIN_MESSAGE
	                );
	
	         	   if (option != JOptionPane.OK_OPTION) {
	                    break;
	                }
            	
            	
            	 String enteredPassword = new String(passwordField.getPassword());

                 // Correct password
                 String correctPassword = "nhaniuhuy1";

                 if (enteredPassword.equals(correctPassword)) {
		                button.setEnabled(false);
		                try {
		              	   String text = orderField.getText().equals("Order #") ? "" : orderField.getText();
		              	   if(text.length() > 0)
		              	   {
		              		 if(userField.getText().length() > 0)
		              		 {
		              			if(passField.getText().length() > 0)
		                 		{
		
			              			 String username = userField.getText();
			              			 String password = passField.getText();
			              			 String orderNum = orderField.getText();
			              			 String reworkOrderNum = reworkOrderField.getText().equals("Order # (optional)") ? "" : reworkOrderField.getText();
			              			 String name = nameField.getText();
			              			 int[] times = {11,14};
			              			 
			              			 List<Integer> bloodcomdemnList = new ArrayList<>();
			              	         
		
			              			 List<Integer> greencomdemnList = new ArrayList<>();
			              	         
		
			              			 List<Integer> DsiTrimList = new ArrayList<>();
		
			              	         
			              	         
				              	       new Thread(() -> {
					             			 paperworkDSIGen ppw = new paperworkDSIGen(frame,username,password,orderNum,reworkOrderNum,name,times,bloodcomdemnList,greencomdemnList,DsiTrimList,pdfOnlycb.isSelected(),sendEmailcb.isSelected(),tendercondemnedField.getText(),breastSurplusField.getText(),bloodcondemnedField.getText(),false);
					             			 try {
												ppw.start();
											} catch (Exception e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
				              	        }).start();
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
		             	   ex.printStackTrace();
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
		                break;
		            }
            	}
            }
        });
        
        this.add(splitPanel);
        
        File file = new File(System.getProperty("user.home") + "\\Desktop\\gcweights.txt");

        try (FileWriter writer = new FileWriter(file)) {
            // Opening the file in write mode will automatically overwrite the contents, 
            // so the file will be cleared.
            writer.write(""); // Clear the file by writing an empty string
            System.out.println("File has been cleared.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    
    public void setVerified(boolean verified)
    {
    	this.isVerified = verified;
    }
    
    public boolean isVerified()
    {
    	return isVerified;
    }
}