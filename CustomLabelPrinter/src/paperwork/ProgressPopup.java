package paperwork;

import javax.swing.*;
import java.awt.*;

public class ProgressPopup {

    private final JDialog dialog;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public ProgressPopup(JFrame parent) {
        dialog = new JDialog(parent, "Processing", false);
        dialog.setDefaultCloseOperation(
                JDialog.HIDE_ON_CLOSE
        );

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        statusLabel = new JLabel("Starting...");

        JButton hideButton = new JButton("Hide");
        hideButton.addActionListener(e -> dialog.setVisible(false));
        hideButton.setBackground(Color.white);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(hideButton, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(parent);
    }

    public void showPopup() {
        dialog.setVisible(true);
    }

    public void hidePopup() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    public void updateProgress(int value, String status) {
        progressBar.setValue(value);
        statusLabel.setText(status);
    }

    public void complete() {
        progressBar.setValue(100);
        statusLabel.setText("Completed");
    }
}