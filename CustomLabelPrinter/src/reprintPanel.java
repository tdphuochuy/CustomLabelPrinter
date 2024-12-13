import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class reprintPanel extends JPanel{
	private JFrame frame;
    public reprintPanel(JFrame frame) {
    	this.frame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel printerPanel = new JPanel();
        printerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel label = new JLabel("Printer");
        String[] items = {"Printer 1", "Printer 2", "Custom"};

        // Create a JComboBox with the items array
        JComboBox<String> dropdown = new JComboBox<>(items);
        dropdown.setPreferredSize(new Dimension(100, dropdown.getPreferredSize().height));

        printerPanel.add(label);
        printerPanel.add(dropdown);
        
        this.add(printerPanel);
    }
}