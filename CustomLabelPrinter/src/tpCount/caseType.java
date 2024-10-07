package tpCount;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class caseType extends JPanel{
    public caseType(JFrame frame) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel autoPanel = new JPanel();
        autoPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        
        JLabel label = new JLabel("Customer");
        JTextField textField = new JTextField(10);
        inputPanel.add(label);
        inputPanel.add(textField);
        
        
        JCheckBox checkBox = new JCheckBox("Auto split");
        JTextField caseQtyField = new JTextField(7);
        caseQtyField.setEnabled(false);
        setPlaceholder(caseQtyField,"# of cases");
        
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	caseQtyField.setEnabled(true);
                } else {
                	caseQtyField.setEnabled(false);
                }
            }
        });
        autoPanel.add(checkBox);
        autoPanel.add(caseQtyField);
        
        this.add(inputPanel);
        this.add(autoPanel);
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