import javax.swing.SwingUtilities;

public class reprintTask implements Runnable {
    private final String ipAddress;
    private final String type;
    private final int quantity;
    private final int delay;
    private final int interval;
    private volatile boolean running = true; // To control the task's execution
    private final Runnable onCompletion;
    
    public reprintTask(String ipAddress, String type, int quantity, int delay, int interval, Runnable onCompletion) {
        this.ipAddress = ipAddress;
        this.type = type;
        this.quantity = quantity;
        this.delay = delay;
        this.interval = interval;
        this.onCompletion = onCompletion;
    }

    public void stop() {
        running = false; // Stop the task gracefully
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= quantity && running; i++) {
                if (!running) break; // Check if the task is still running

                System.out.println(i + " " + ipAddress + " " + type);

                if (interval > 0 && i % interval == 0 && i < quantity) {
                    Thread.sleep(delay); // Pause for the specified delay
                }
                if(i < quantity)
                {
                	Thread.sleep(1600);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupt status
        }finally {
            if (onCompletion != null) {
                SwingUtilities.invokeLater(onCompletion); // Notify on completion
            }
        }
    }
}
