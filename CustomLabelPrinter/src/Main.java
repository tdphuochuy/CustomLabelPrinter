import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import chatSystem.ChatPanel;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import whistle.NeoWhistlePanel;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
   public static void main(String[] args) throws UnknownHostException, URISyntaxException, InterruptedException {
       // Create the main frame
       JFrame frame = new JFrame("Custom Label Printer");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(600, 450);

       // Create a JTabbedPane
       JTabbedPane tabbedPane = new JTabbedPane();
       
       tabbedPane.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent e) {
               JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
               int selectedIndex = sourceTabbedPane.getSelectedIndex();
               String selectedTabTitle = sourceTabbedPane.getTitleAt(selectedIndex);
               if(selectedTabTitle.equals("Chat"))
               {
            	   tabbedPane.setBackgroundAt(selectedIndex, null);
               }
           }
       });
       
       IconFontSwing.register(FontAwesome.getIconFont());

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
       
       JPanel neoWhistlePanel = new JPanel();
       neoWhistlePanel.add(new NeoWhistlePanel(frame));

       // Add tabs to the tabbedPane
       tabbedPane.addTab("TP count", tpCountPanel);
       tabbedPane.addTab("Combo count", countComboPanel);
       tabbedPane.addTab("", IconFontSwing.buildIcon(FontAwesome.EXCLAMATION_TRIANGLE, 12, Color.red), neoWhistlePanel);
       tabbedPane.addTab("GC weights", gcWeightPanel);
       tabbedPane.addTab("Reprint", reprintPanel);
       tabbedPane.addTab("Chat", new ChatPanel(frame,tabbedPane));
       tabbedPane.addTab("no clue", freePanel);

       // Add the tabbedPane to the frame
       frame.setLayout(new BorderLayout());
       frame.add(tabbedPane, BorderLayout.CENTER);
       frame.setLocationRelativeTo(null);

       // Set the frame visibility
       frame.setVisible(true);
   }
}