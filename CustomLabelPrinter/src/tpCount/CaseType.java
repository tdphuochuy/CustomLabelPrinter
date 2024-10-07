package tpCount;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CaseType extends JPanel{
	private JFrame frame;
    public CaseType(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel autoPanel = new JPanel();
        autoPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel codePanel = new JPanel();
        codePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        
        JLabel label = new JLabel("Customer");
        JTextField textField = new JTextField(10);
        inputPanel.add(label);
        inputPanel.add(textField);
        
        
        JCheckBox checkBox = new JCheckBox("Auto split");
        JTextField caseQtyField = new JTextField(7);
        caseQtyField.setEnabled(false);
        setPlaceholder(caseQtyField,"# of cases");
        
        JLabel quantityLabel = new JLabel("Qty");
        JTextField quantityField = new JTextField(3);
        JLabel casePerPalletLabel = new JLabel("# cases");
        JTextField casePerPalletField = new JTextField(5);
        setPlaceholder(casePerPalletField,"optional");
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);
        quantityPanel.add(casePerPalletLabel);
        quantityPanel.add(casePerPalletField);
        
        JLabel codeLabel = new JLabel("Product code");
        JTextField codeField = new JTextField(8);
        setPlaceholder(codeField,"optional");
        codePanel.add(codeLabel);
        codePanel.add(codeField);
        codePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	caseQtyField.setEnabled(true);
                	quantityField.setEnabled(false);
                	casePerPalletField.setEnabled(false);
                } else {
                	caseQtyField.setEnabled(false);
                	quantityField.setEnabled(true);
                	casePerPalletField.setEnabled(true);
                }
            }
        });
        autoPanel.add(checkBox);
        autoPanel.add(caseQtyField);
        
        JButton button = new JButton("Print");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.white);
        
        checkBox.doClick();
        
        this.add(inputPanel);
        this.add(autoPanel);
        this.add(quantityPanel);
        this.add(codePanel);
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