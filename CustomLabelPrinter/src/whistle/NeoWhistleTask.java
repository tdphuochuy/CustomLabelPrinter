package whistle;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import config.Config;

public class NeoWhistleTask implements Runnable {
	private String username;
    private String password;
    private String orderNum;
    private boolean autoSequence;
    private volatile boolean running = true;
    private JTextArea systemConsole;
    private JTextArea userConsole;
    private TelnetManager manager;
	public NeoWhistleTask(String username, String password, boolean autoSequence,JTextArea userConsole,JTextArea systemConsole) {
        this.username = username;
        this.password = password;
        this.autoSequence = autoSequence;
        this.systemConsole = systemConsole;
        this.userConsole = userConsole;
    }
	
	public NeoWhistleTask(String username, String password, boolean autoSequence,JTextArea userConsole,JTextArea systemConsole,String orderNum) {
        this.username = username;
        this.password = password;
        this.autoSequence = autoSequence;
        this.systemConsole = systemConsole;
        this.userConsole = userConsole;
        this.orderNum = orderNum;
    }
	
	@Override
    public void run()
	{
		
		try {
			Scanner scanner = new Scanner(System.in);
			if(orderNum == null)
			{
				userConsole.append("Enter order number:\n");
				orderNum = scanner.nextLine();
			}
			systemConsole.append("Starting with order #" + orderNum + "\n");
			manager = new TelnetManager(orderNum, username, password, autoSequence,systemConsole);
			
			while(running)
			{
				userConsole.append("Enter product code\n");
                userConsole.setCaretPosition(userConsole.getDocument().getLength());
				String prodNum = scanner.nextLine();
				userConsole.append("Enter quantity\n");
                userConsole.setCaretPosition(userConsole.getDocument().getLength());
				String quantity = scanner.nextLine();
				try {
					Integer.parseInt(quantity);
					Integer.parseInt(prodNum);
				} catch (Exception e)
				{
					continue;
				}
				
				if(prodNum.equals("17261"))
				{
					userConsole.append("Rework type: Combo (1) or Cases (2)\n");
	                userConsole.setCaretPosition(userConsole.getDocument().getLength());
					String reworkType = scanner.nextLine();
					if(reworkType.equals("2"))
					{
						int newQuantity = Integer.parseInt(quantity) + 40;
						quantity = String.valueOf(newQuantity);
					}
				}
				
				String sequence = "0";
				if(prodNum.equals("skip") || quantity.equals("skip"))
				{
					userConsole.append("Skipping...\n");
					continue;
				}
				if(!autoSequence)
				{
					userConsole.append("Enter sequence\n");
					sequence = scanner.nextLine();
				}
				manager.addCommand(new Command(prodNum,quantity,getHour(),sequence));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addCommand(String prodNum,String quantity,String sequence)
	{
		if(manager != null)
		{
			manager.addCommand(new Command(prodNum,quantity,getHour(),sequence));
		}
	}
	
	public void addCommand(String prodNum,String quantity,String hour,String sequence)
	{
		if(manager != null)
		{
			manager.addCommand(new Command(prodNum,quantity,hour,sequence));
		}
	}
	
	public String getHour()
	{
		LocalTime currentTime = LocalTime.now();

        // Get the current hour in 24-hour format
        int currentHour = currentTime.getHour();
        currentHour = currentTime.getHour() + Config.dayTimeSaving; //adjust day time saving

        // Adjust the hour by adding 24
        if(currentHour < 4)
        {        	
        	currentHour += 24;
        }
        
        return String.valueOf(currentHour);

	}
	
	public void stop() throws IOException {
        running = false;
        if(manager != null)
        {
        	manager.stop();
        }
    }
	
	public boolean isRunning()
	{
		return running;
	}
}