import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class Main {
   public static void main(String[] args) {
       // Create the main frame
       JFrame frame = new JFrame("Custom Label Printer");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(500, 350);

       // Create a JTabbedPane
       JTabbedPane tabbedPane = new JTabbedPane();

       // Create panels for each tab
       JPanel tpCountPanel = new JPanel();
       tpCountPanel.add(new TpCountPanel(frame));


       JPanel countComboPanel = new JPanel();
       countComboPanel.add(new countComboPanel(frame));
       
       JPanel freePanel = new JPanel();
       freePanel.add(new FreePanel(frame));
       
       JPanel gcWeightPanel = new JPanel();
       gcWeightPanel.add(new GCweights(frame));

       JPanel reprintPanel = new JPanel();
       reprintPanel.add(new reprintPanel(frame));

       // Add tabs to the tabbedPane
       tabbedPane.addTab("TP count", tpCountPanel);
       tabbedPane.addTab("Combo count", countComboPanel);
       tabbedPane.addTab("GC weights", gcWeightPanel);
       tabbedPane.addTab("Reprint", reprintPanel);
       tabbedPane.addTab("no clue", freePanel);

       // Add the tabbedPane to the frame
       frame.add(tabbedPane, BorderLayout.CENTER);
       frame.setLocationRelativeTo(null);

       // Set the frame visibility
       frame.setVisible(true);
       
       
       /*LocalTime now = LocalTime.now();
       LocalTime limit = LocalTime.of(18, 30); // 6:30 PM

       if (now.isAfter(limit)) {
           System.exit(0);
       }*/
   }
}