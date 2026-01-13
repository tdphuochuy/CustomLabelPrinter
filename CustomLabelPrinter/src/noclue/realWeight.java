package noclue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.Config;
import paperwork.marel.paperworkMarelGen;
import whistle.SequenceGetter;

public class realWeight extends JPanel{
	private JFrame frame;
	public realWeight(JFrame frame) throws IOException, ParseException {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel orderNumPanel = new JPanel();
        orderNumPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JLabel orderNumLbl = new JLabel("Order #");
        JTextField orderNumInput = new JTextField(10);
        orderNumPanel.add(orderNumLbl);
        orderNumPanel.add(orderNumInput);
        
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JLabel productLbl = new JLabel("ItemPack");
        JTextField productInput = new JTextField(10);
        productPanel.add(productLbl);
        productPanel.add(productInput);
        
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JLabel result = new JLabel("");
        resultPanel.add(result);
        
        JButton button = new JButton("Calculate");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                try {
                	String orderNum = orderNumInput.getText();
                	String product = productInput.getText();
                	if(orderNum.length() > 0 && product.length() > 0)
                	{
                  	  SequenceGetter sequenceGetter = new SequenceGetter(Config.username,Config.password);
                  	  double weight = sequenceGetter.getRealAdageWeight(orderNum, product);
                  	  result.setText("Total: " + weight + " lbs");
                	} else {
	                       JOptionPane.showMessageDialog(frame, "Missing required input", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        this.add(orderNumPanel);
        this.add(productPanel);
        this.add(resultPanel);
        this.add(button);
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