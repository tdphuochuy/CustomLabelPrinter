import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class tpCountPanel extends JPanel{
    public tpCountPanel(JFrame frame) {
       this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Set the main layout to BoxLayout (vertical)
       
       JPanel inputPanel = new JPanel();
       inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Center the label and text field
    
       this.add(inputPanel);
       
       //Customer Panel
       JLabel label = new JLabel("Customer");
       JTextField textField = new JTextField(15);
       inputPanel.add(label);
       inputPanel.add(textField);
       
       
    }
}