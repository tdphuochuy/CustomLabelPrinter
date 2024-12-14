import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class reprintTask implements Runnable {
    private final String ipAddress;
    private final String type;
    private final int quantity;
    private final int delay;
    private final int interval;
    private volatile boolean running = true; // To control the task's execution
    private final Runnable onCompletion;
    private int printerPort = 9100;
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
                
                printLabel();

                if (interval > 0 && i % interval == 0 && i < quantity) {
                    Thread.sleep(delay); // Pause for the specified delay
                }
                if(i < quantity)
                {
                	Thread.sleep(1500);
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
    
    public void printLabel()
    {
    	byte[] reprintCommand;
    	if(type.equals("SDPL"))
    	{
            reprintCommand = "\u0002G".getBytes();  // The reprint command for the Sato CL4NX Plus
            
    	} else {
    		reprintCommand = "\u001BA\u001BC\u001BZ".getBytes();
    	}
    	
    	try (Socket socket = new Socket(ipAddress, printerPort);
	             OutputStream outStream = socket.getOutputStream()) {
	
	            // Check if the socket is connected
	            if (socket.isConnected()) {
	                System.out.println("Connected to printer. Sending reprint command...");
	
	                // Send the reprint command to the printer
	                outStream.write(reprintCommand);
	                outStream.flush();
	
	                System.out.println("Reprint command sent to printer.");
	            } else {
	                System.out.println("Failed to connect to the printer.");
	            }
	
	        } catch (IOException e) {
	            System.err.println("Error communicating with the printer: " + e.getMessage());
	            e.printStackTrace();
	        }
    }
}
