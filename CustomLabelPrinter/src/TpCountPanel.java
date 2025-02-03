import javax.swing.*;

import tpCount.CaseType;
import tpCount.ComboType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.TimerTask;
import java.util.Timer;

public class TpCountPanel extends JPanel {
    private JPanel mainPanel;  // To hold the main panel reference for dynamic updates
    private JPanel contentPanel;  // A panel that will be updated on button clicks
    private int pressCount = 0;
    private boolean activated;
    public TpCountPanel(JFrame frame) {
        mainPanel = this;
        activated = false;
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Set the main layout to BoxLayout (vertical)

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Set layout for the label and buttons
        this.add(typePanel);

        JLabel label1 = new JLabel("Type");
        JButton btnCombo = new JButton("Combo");
        btnCombo.setBackground(Color.white);
        JButton btnCases = new JButton("Cases");
        btnCases.setBackground(Color.white);
        typePanel.add(label1);
        typePanel.add(btnCombo);
        typePanel.add(btnCases);

        // Create the dynamic panels that will be shown/hidden
        JPanel comboTypePanel = new ComboType(frame);
        JPanel caseTypePanel = new CaseType(frame);

        // Create a content panel to add dynamic content to it
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());  // You can change the layout as needed
        this.add(contentPanel);  // Add the content panel to the main panel
        
        javax.swing.Timer resetTimer = new javax.swing.Timer(1000, e -> pressCount = 0); // Reset after 1 second of inactivity
        resetTimer.setRepeats(false);

        // Add ActionListeners to switch between panels
        btnCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(!activated)
            	{
	            	pressCount++;
	                resetTimer.restart();
	
	                if (pressCount == 3) {
	                	activated = true;
	                	System.out.println("Shutdown cancelled");
	                }
            	}
            	
            	btnCombo.setBackground(Color.decode("#b8cfe5"));
                btnCases.setBackground(Color.white);
                // Remove all existing components from the content panel
                contentPanel.removeAll();
                // Add the comboTypePanel
                contentPanel.add(comboTypePanel, BorderLayout.CENTER);
                // Revalidate and repaint the panel to refresh the UI
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });

        btnCases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	btnCases.setBackground(Color.decode("#b8cfe5"));
                btnCombo.setBackground(Color.white);
                // Remove all existing components from the content panel
                contentPanel.removeAll();
                // Add the caseTypePanel
                contentPanel.add(caseTypePanel, BorderLayout.CENTER);
                // Revalidate and repaint the panel to refresh the UI
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        
        LocalTime shutdownStartTime = LocalTime.of(17, 15);
        LocalTime currentTime = LocalTime.now();
        
        if (currentTime.isAfter(shutdownStartTime)) {
            startShutdownTimer();
        } else {
            scheduleShutdownAt(shutdownStartTime);
        }
    }
    
    private void startShutdownTimer() {
    	System.out.println("Shutdown timer started");
    	Timer shutdownTimer = new Timer();
        shutdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
            	if(!activated)
            	{
                	System.exit(0);
            	}
            }
        }, 3000);
    }

    private void scheduleShutdownAt(LocalTime shutdownStartTime) {
        long delay = java.time.Duration.between(LocalTime.now(), shutdownStartTime).toMillis();
        Timer scheduleTimer = new Timer();
        scheduleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startShutdownTimer();
            }
        }, delay);
    }
}
