package paperwork;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.simple.parser.ParseException;

import config.Config;
import paperwork.dsi.comboWeightTask;
import paperwork.dsi.paperworkDSIGen;
import paperwork.marel.paperworkMarelGen;

public class paperworkMarelPanel extends JPanel{
	private JFrame frame;
	private boolean isVerified = false;
	public paperworkMarelPanel(JFrame frame) {
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
        JTextField userField = new JTextField("lcuevas",8);
        userPanel.add(userLabel);
        userPanel.add(userField);
        
        JLabel passLabel = new JLabel("Pass");
        JPasswordField  passField = new JPasswordField("0403",5);
        passPanel.add(passLabel);
        passPanel.add(passField);
        

        
        JLabel nameLabel = new JLabel("Name");
        JTextField  nameField = new JTextField("Lam",5);
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
        mainPanel.add(namePanel);
        mainPanel.add(timePanel);
        mainPanel.add(checkboxesPanel);
        mainPanel.add(button);
       
        
        JPanel comdemnPanel =  new JPanel();
        comdemnPanel.setLayout(new BoxLayout(comdemnPanel, BoxLayout.Y_AXIS));
        String[] columnNames = {"Wing tips", "Wings", "Lollipop", "Miscut"};
        Object[][] data = new Object[7][4];
        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        JTable table = new JTable(model);
        // Scroll pane with table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(275, 100));
        scrollPane.setBorder(BorderFactory.createTitledBorder("1st break"));
        
        
        Object[][] data2 = new Object[7][4];
        DefaultTableModel model2 = new DefaultTableModel(data2, columnNames);
        JTable table2 = new JTable(model2);
        // Scroll pane with table
        JScrollPane scrollPane2 = new JScrollPane(table2);
        scrollPane2.setPreferredSize(new Dimension(275, 100));
        scrollPane2.setBorder(BorderFactory.createTitledBorder("2nd break"));
        
        
        Object[][] data3 = new Object[7][4];
        DefaultTableModel model3 = new DefaultTableModel(data3, columnNames);
        JTable table3 = new JTable(model3);
        // Scroll pane with table
        JScrollPane scrollPane3 = new JScrollPane(table3);
        scrollPane3.setPreferredSize(new Dimension(275, 100));
        scrollPane3.setBorder(BorderFactory.createTitledBorder("Go home"));
        
        comdemnPanel.add(scrollPane);        
        comdemnPanel.add(scrollPane2);      
        comdemnPanel.add(scrollPane3);        


        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        splitPanel.add(mainPanel);
        splitPanel.add(comdemnPanel);
        
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
              				
              				Map<String,List<List<Integer>>> condemnMap = new HashMap<>();
              				setCondemnMap(condemnMap,"wingtips",0,table,table2,table3);
              				setCondemnMap(condemnMap,"wings",1,table,table2,table3);
              				setCondemnMap(condemnMap,"lollipop",2,table,table2,table3);
              				setCondemnMap(condemnMap,"miscut",3,table,table2,table3);
              				
              				
              				new Thread(() -> {
              					paperworkMarelGen ppw = new paperworkMarelGen(frame,username,password,orderNum,reworkOrderNum,name,times,condemnMap,pdfOnlycb.isSelected(),sendEmailcb.isSelected());
		             			 try {
									ppw.start();
								} catch (ParseException | InterruptedException | IOException e1) {
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
                        userField.setText(Config.username);
                        passField.setText(Config.password);
                    }
                });
                timer.setRepeats(false); // Make sure the timer only runs once
                timer.start(); // Start the timer
            }
        });
        
        this.add(splitPanel);

	}
	
	public void setCondemnMap(Map<String,List<List<Integer>>> condemnMap,String key,int position,JTable table1,JTable table2, JTable table3)
	{
		List<List<Integer>> condemnList = new ArrayList<>();
		
		condemnList.add(setCondemnMapHelper(table1,position));
		condemnList.add(setCondemnMapHelper(table2,position));
		condemnList.add(setCondemnMapHelper(table3,position));

		condemnMap.put(key,condemnList);
	}
	
	public List<Integer> setCondemnMapHelper(JTable table, int position)
	{
		List<Integer> list = new ArrayList<>();
		
		for(int i = 0; i < 7; i++)
		{
			if(table.getValueAt(i, position) != null)
			{
				int value = Integer.valueOf(table.getValueAt(i, position).toString().trim());
				list.add(value);
			}
		}
		
		return list;
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