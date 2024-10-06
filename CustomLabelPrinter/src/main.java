import javax.swing.*;
import java.awt.*;

public class main {
   public static void main(String[] args) {
       // Create the main frame
       JFrame frame = new JFrame("Custom Label Printer");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(500, 300);

       // Create a JTabbedPane
       JTabbedPane tabbedPane = new JTabbedPane();

       // Create panels for each tab
       JPanel tpCountPanel = new JPanel();
       tpCountPanel.add(new tpCountPanel(frame));


       JPanel countComboPanel = new JPanel();
       countComboPanel.add(new countComboPanel(frame));
       
       


       // Add tabs to the tabbedPane
       tabbedPane.addTab("TP count", tpCountPanel);
       tabbedPane.addTab("Combo count", countComboPanel);

       // Add the tabbedPane to the frame
       frame.add(tabbedPane, BorderLayout.CENTER);
       frame.setLocationRelativeTo(null);

       // Set the frame visibility
       frame.setVisible(true);
   }
}