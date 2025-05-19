package buttons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import config.Config;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class buttonsPanel extends JPanel{
	private JFrame frame;
	private Map<String, ButtonObj> buttonMap = new HashMap<>();
	private JLabel whistlestatus;
	private Map<String, TableEntry> hourSequenceMap =  new TreeMap<>();
	public buttonsPanel(JFrame frame) throws ParseException {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        hourSequenceMap.put("17900", new TableEntry("98","0"));
        hourSequenceMap.put("17901", new TableEntry("98","0"));
        hourSequenceMap.put("25814", new TableEntry("98","0"));
        hourSequenceMap.put("16887", new TableEntry("98","0"));
        hourSequenceMap.put("23978", new TableEntry("98","0"));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 75, 0));
        
        InputStream inputStream = buttonsPanel.class.getClassLoader().getResourceAsStream("buttons/buttons.json");
        String content = new BufferedReader(new InputStreamReader(inputStream))
                            .lines()
                            .collect(Collectors.joining("\n"));
    	JSONParser jsonParser = new JSONParser();
	    JSONArray buttonsArray= (JSONArray) jsonParser.parse(content);
	    
	    for(Object buttonObject : buttonsArray)
	    {
	    	JSONObject buttonObj = (JSONObject) buttonObject;
	    	String buttonName = buttonObj.get("name").toString();
	    	String productCode = buttonObj.get("productCode").toString();
	    	String quantity = buttonObj.get("quantity").toString();
	    	long delay = (long) buttonObj.get("delay");
	    	boolean enabled = (boolean) buttonObj.get("enable");
	    	
	    	JLabel currentQuantitylbl = new JLabel(quantity);
	        JLabel iconlbl = new JLabel(IconFontSwing.buildIcon(FontAwesome.CARET_RIGHT, 12,Color.decode("#69a5de")));
	        JLabel nextQuantitylbl = new JLabel(quantity);
	    	
	    	buttonMap.put(buttonName, new ButtonObj(productCode,quantity,enabled,currentQuantitylbl,nextQuantitylbl,delay));
	    	
	        JPanel buttonPanel = new JPanel();
	        buttonPanel.add(Box.createRigidArea(new Dimension(0, 25)));
	        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
	        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/buttons/images/" + buttonName + ".png"));
	        
	        Image scaledImage = imageIcon.getImage().getScaledInstance(92, 65, Image.SCALE_SMOOTH);
	        ImageIcon scaledIcon = new ImageIcon(scaledImage);
	        
	        JLabel label = new JLabel(scaledIcon);
	        label.setAlignmentX(Component.CENTER_ALIGNMENT);
	        buttonPanel.add(label);
	        
	        JCheckBox checkbox = new JCheckBox("Enabled");
	        checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
	        checkbox.setSelected(enabled);
	        checkbox.addItemListener(new ItemListener() {
	            @Override
	            public void itemStateChanged(ItemEvent e) {
	                if (e.getStateChange() == ItemEvent.SELECTED) {
		                buttonMap.get(buttonName).setEnabled(true);
	                } else {
		                buttonMap.get(buttonName).setEnabled(false);
	                }
	            }
	        });
	        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	        buttonPanel.add(checkbox);
	        
	        JTextField productCodeField = new JTextField(5);
	        productCodeField.setHorizontalAlignment(JTextField.CENTER);
	        productCodeField.setText(productCode);
	        productCodeField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { update(); }
                public void removeUpdate(DocumentEvent e) { update(); }
                public void changedUpdate(DocumentEvent e) { update(); } // Usually not used for plain JTextField

                private void update() {
                    String text = productCodeField.getText();
                    buttonMap.get(buttonName).setProductCode(text);
                }
            });
	        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	        buttonPanel.add(productCodeField);
	        
	        JTextField quantityField = new JTextField(5);
	        quantityField.setHorizontalAlignment(JTextField.CENTER);
	        quantityField.setText(quantity);
	        quantityField.addFocusListener(new FocusAdapter() {
	            @Override
	            public void focusLost(FocusEvent e) {
	            	String text = quantityField.getText();
                    buttonMap.get(buttonName).setQuantity(text);
	            }
	        });
	        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	        buttonPanel.add(quantityField);
	        
	        JPanel labelPanel = new JPanel();
	        labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	        
	        JPanel delayPanel = new JPanel();
	        delayPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	        JTextField delayField = new JTextField(String.valueOf(delay / 1000),3);
	        delayField.setHorizontalAlignment(JTextField.CENTER);
	        delayField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { update(); }
                public void removeUpdate(DocumentEvent e) { update(); }
                public void changedUpdate(DocumentEvent e) { update(); } // Usually not used for plain JTextField

                private void update() {
                    String text = delayField.getText();
                    try {
                    	buttonMap.get(buttonName).setDelay(Long.valueOf(text) * 1000);
                    } catch (Exception e)
                    {
                    	
                    }
                }
            });
	        JLabel delaylbl = new JLabel("Delay");
	        JLabel mslbl = new JLabel("s");
	        delayPanel.add(delaylbl);
	        delayPanel.add(delayField);
	        delayPanel.add(mslbl);
	       
	        labelPanel.add(currentQuantitylbl);
	        labelPanel.add(iconlbl);
	        labelPanel.add(nextQuantitylbl);
	        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	        buttonPanel.add(delayPanel);
	        buttonPanel.add(labelPanel);

	        buttonsPanel.add(buttonPanel);
	    }
	    
        JPanel statusPanel = new JPanel();
	    JLabel status = new JLabel("Neo Whistle's status:");
	    whistlestatus = new JLabel("inactive");
	    whistlestatus.setForeground(Color.RED);
	    
	    statusPanel.add(status);
	    statusPanel.add(whistlestatus);
	    
        JPanel sequenceHourbtnPanel = new JPanel();
        JButton sequenceHourbtn = new JButton("<html><u>Setup Hour/Sequence</u></html>");
        sequenceHourbtn.setContentAreaFilled(false);
        sequenceHourbtn.setBorderPainted(false);
        sequenceHourbtn.setOpaque(false);
        sequenceHourbtn.setForeground(Color.decode("#1b93f5")); // Optional: make it look like a hyperlink
        sequenceHourbtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Optional: pointer cursor
        
        sequenceHourbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	SequenceHourPopup dialog = new SequenceHourPopup(frame,hourSequenceMap);
                dialog.setVisible(true);
                
                if (dialog.isApplied()) {
                    System.out.println("Updated Map:");
                    hourSequenceMap.forEach((k, v) -> System.out.println(k + " -> " + v));
                }
            }
        });
        sequenceHourbtnPanel.add(sequenceHourbtn);
	    
	    this.add(statusPanel);
	    this.add(sequenceHourbtnPanel);
	    this.add(buttonsPanel);
    }
    
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
    
    public void setWhistleStatus(boolean running)
    {
    	if(running)
    	{
    		whistlestatus.setText("active");
    	    whistlestatus.setForeground(Color.decode("#57eb66"));
    	} else {
    		whistlestatus.setText("inactive");
    	    whistlestatus.setForeground(Color.RED);
    	}
    }
    
    public ButtonObj getButton(String buttonName)
    {
    	return buttonMap.get(buttonName);
    }
    
    public Map<String, TableEntry> getHourSequenceMap() {
		return hourSequenceMap;
	}
}