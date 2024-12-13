import java.awt.Color;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

class reprintTask implements Runnable {
    private JButton startStopButton;

    public reprintTask(JButton startStopButton) {
        this.startStopButton = startStopButton;
    }

    @Override
    public void run() {
        try {
            // Count from 1 to 10 with a 1-second delay between each
            for (int i = 1; i <= 10; i++) {
                System.out.println(i); // Printing the number to the console
                Thread.sleep(1000); // Delay for 1 second

                // You can update the GUI in this thread, if needed
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // For example, you can update a label or progress bar here if desired
                    }
                });
            }

            // After the counting completes, change the button back to Start
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    startStopButton.setText("Start");
                    startStopButton.setBackground(new Color(100, 255, 100));  // Green color for Start
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}