import javax.swing.*;

import tpCount.CaseType;
import tpCount.ComboType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TpCountPanel extends JPanel {
    private JPanel mainPanel;  // To hold the main panel reference for dynamic updates
    private JPanel contentPanel;  // A panel that will be updated on button clicks

    public TpCountPanel(JFrame frame) {
        mainPanel = this;
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

        // Add ActionListeners to switch between panels
        btnCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
    }
}
