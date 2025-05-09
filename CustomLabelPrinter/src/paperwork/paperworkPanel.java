package paperwork;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.json.simple.parser.ParseException;

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
        JTextField userField = new JTextField("pmambo",8);
        userPanel.add(userLabel);
        userPanel.add(userField);
        
        JLabel passLabel = new JLabel("Pass");
        JPasswordField  passField = new JPasswordField("4292",5);
        passPanel.add(passLabel);
        passPanel.add(passField);
        
        JPanel GCbuttonPanel = new JPanel();
        GCbuttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton gcWeightButton = new JButton("Print GC weights");
        gcWeightButton.setBackground(Color.white);
        GCbuttonPanel.add(gcWeightButton);
        
        gcWeightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	gcWeightButton.setEnabled(false);
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
	              			 String reworkOrderNum = reworkOrderField.getText();
	              			 Thread GCthread = new Thread(new GCweightsTask(username,password,orderNum,reworkOrderNum));
	              			 GCthread.start();
	              			 try {
	              				GCthread.join(); // Wait for the task to finish
	              			 } catch (InterruptedException ex) {
	              				 ex.printStackTrace();
	              			 }
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
                    	gcWeightButton.setEnabled(true); // Re-enable the button
                        userField.setText("pmambo");
                        passField.setText("4292");
                    }
                });
                timer.setRepeats(false); // Make sure the timer only runs once
                timer.start(); // Start the timer
            }
        });
        
        JLabel nameLabel = new JLabel("Name");
        JTextField  nameField = new JTextField("Huy",5);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        
        JLabel break1Label = new JLabel("1st break");
        JTextField  break1Field = new JTextField("20",2);
        JTextField  break2Field = new JTextField("23",2);
        JCheckBox break2cb = new JCheckBox("2nd break",true);
        break2cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
            	break2Field.setEnabled(break2cb.isSelected());
            }
        });
        timePanel.add(break1Label);
        timePanel.add(break1Field);
        timePanel.add(break2cb);
        timePanel.add(break2Field);
        
        JButton button = new JButton("Game Over");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        JCheckBox pdfOnlycb = new JCheckBox("Generate PDF only");
        checkboxesPanel.add(pdfOnlycb);
        JCheckBox sendEmailcb = new JCheckBox("Send email");
        checkboxesPanel.add(sendEmailcb);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(inputPanel);
        mainPanel.add(inputPanel2);
        mainPanel.add(userPanel);
        mainPanel.add(passPanel);
        mainPanel.add(GCbuttonPanel);
        mainPanel.add(namePanel);
        mainPanel.add(timePanel);
        mainPanel.add(checkboxesPanel);
        mainPanel.add(button);
        
        JPanel textareaPanel = new JPanel();
        textareaPanel.setLayout(new BoxLayout(textareaPanel, BoxLayout.Y_AXIS));
        textareaPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        JTextArea textArea = new JTextArea(14,12);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);   
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("Trim condemned:"));
        textareaPanel.add(labelPanel);
        textareaPanel.add(scrollPane);
        
        JPanel tenderCondenmnedPanel = new JPanel();
        tenderCondenmnedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        tenderCondenmnedPanel.add(new JLabel("Tender condemned:"));
        JTextField tendercondemnedField = new JTextField(5);
        tenderCondenmnedPanel.add(tendercondemnedField);
        
        textareaPanel.add(tenderCondenmnedPanel);
        
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        splitPanel.add(mainPanel);
        splitPanel.add(textareaPanel);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                try {
              	   String text = orderField.getText().equals("Order #") ? "" : orderField.getText();
              	   if(text.length() > 0)
              	   {
              		 if(userField.getText().length() > 0)
              		 {
              			if(passField.getText().length() > 0)
                 		{
              				if(textArea.getText().length() > 0)
              				{
		              			 String username = userField.getText();
		              			 String password = passField.getText();
		              			 String orderNum = orderField.getText();
		              			 String reworkOrderNum = reworkOrderField.getText().equals("Order # (optional)") ? "" : reworkOrderField.getText();
		              			 String name = nameField.getText();
		              			 int break1 = Integer.parseInt(break1Field.getText());
		              			 int break2 = Integer.parseInt(break2Field.getText());
		              			 if(!break2cb.isSelected())
		              			 {
		              				 break2 = 26;
		              			 }
		              			 int[] times = {break1,break2};
		              			 
		              			 String condemnText = textArea.getText();
		              			 String[] array = condemnText.split("\n");
		              			 List<Integer> comdemnList = new ArrayList<>();
		              	         for (String s : array) {
		              	        	comdemnList.add(Integer.parseInt(s));
		              	         }
		              	         System.out.println(comdemnList);
			              	       new Thread(() -> {
				             			 paperworkGen ppw = new paperworkGen(frame,username,password,orderNum,reworkOrderNum,name,times,comdemnList,pdfOnlycb.isSelected(),sendEmailcb.isSelected(),tendercondemnedField.getText());
				             			 try {
											ppw.start();
										} catch (ParseException | InterruptedException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
			              	        }).start();
              				} else {
       	                       JOptionPane.showMessageDialog(frame, "Missing condemned weights", "Error", JOptionPane.ERROR_MESSAGE);
              				}
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
                        userField.setText("pmambo");
                        passField.setText("4292");
                    }
                });
                timer.setRepeats(false); // Make sure the timer only runs once
                timer.start(); // Start the timer
            }
        });
        
        this.add(splitPanel);
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