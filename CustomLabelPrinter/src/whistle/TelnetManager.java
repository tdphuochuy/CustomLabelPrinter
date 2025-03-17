package whistle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JTextArea;

public class TelnetManager{
	private final BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private whistleWorker worker;
    private SequenceGetter sequenceGetter;
    private JTextArea systemConsole;
	public TelnetManager(String orderNum,String username,String password,boolean autoSequence,JTextArea systemConsole) throws InterruptedException
	{
		this.systemConsole = systemConsole;
		sequenceGetter = new SequenceGetter(username,password);
		worker = new whistleWorker(orderNum, username, password, sequenceGetter,autoSequence,systemConsole);
        executor.submit(this::processCommands);
	}
	
	public void addCommand(Command command) {
        queue.offer(command);
    }
	
	public String getOrderHTML(String orderNum)
	{
		return sequenceGetter.getOrderHTML(orderNum);
	}
	
	 private void processCommands() {
	        while (true) {
	            try {
	                Command command = queue.take();
	                worker.process(command);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                break;
	            }
	        }
	 }
	 
	 public void stop() throws IOException
	 {
		 worker.stop();
		 executor.shutdown();
	 }
}