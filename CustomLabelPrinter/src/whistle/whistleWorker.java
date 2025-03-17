package whistle;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class whistleWorker{
	public int count = 0;
	public boolean autoSequence;
	public String username;
	public String password;
	public String orderNum;
    public String prodNum;
    public String quantity;
    public String sequenceInput;
    private Telnet telnet;
	private boolean backflush;
	private boolean notfound;
    public SequenceGetter sequenceGetter;
    private JTextArea systemConsole;
    public whistleWorker(String orderNum,String username,String password,SequenceGetter sequenceGetter,boolean autoSequence,JTextArea systemConsole) throws InterruptedException{
		this.orderNum = orderNum;
		this.username = username;
		this.password = password;
		this.sequenceGetter = sequenceGetter;
		this.autoSequence = autoSequence;
		this.systemConsole = systemConsole;
		initialize();
	}
    
    public void initialize() throws InterruptedException
    {
    	//take 10-20 secs to start
    	telnet = new Telnet("167.110.212.137", 23, System.out);
		Thread myThread = new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   telnet.execute();
               } catch (Exception ex) {
            	   ex.printStackTrace();
               }
           }
       });
		myThread.start();
		initialize(telnet);
		Thread.sleep(1000);
    }
    
	public void process(Command command)
	{
	   setData(command);
	   backflush = false;
	   notfound = false;
       outer:while(true)
       {
    	   if(Integer.parseInt(quantity) > 5000)
    	   {
    		   systemConsole.append("Invalid quantity, skipping...\n");
    		   break;
    	   }
    	   if(checkCondition(telnet,"Order # [[0;7m") && !checkCondition(telnet,"Prod [[0;7m"))
    	   {
    		   telnet.sendCommand(orderNum + "\n");
    	   }
    	   systemConsole.append("Waiting for order to be ready...\n");
    	    //waitResponse(telnet,"Prod [[0;7m");
           try {
        	   if(!verifyOrder(telnet))
        	   {
        		   reset(telnet);
        		   Thread.sleep(300);
        		   continue outer;
        	   }
	           while(true)
	           {
	   				String response = telnet.getResponse();
	   				if(response.contains("Prod [[0;7m"))
	   				{
	   					break;
	   				} else if (response.contains("ReportProd"))
	   				{
	   					systemConsole.append("OOPS!! returning to production...\n");
	   					reset(telnet);
	   					Thread.sleep(300);
	   					continue outer;
	   				}
	   	    	   Thread.sleep(300);
	           }
		       systemConsole.append("Filling product number\n");
		       telnet.sendCommand(prodNum + "\n");
		       while(true)
	           {
	   				String response = telnet.getResponse();
	   				if(response.contains("Item [[0;7m"))
	   				{
	   					break;
	   				} else if (response.contains("Product not found on order"))
	   				{
	   					systemConsole.append("Product not found!\n");
	   					notfound = true;
	   					reset(telnet);
	   					break;
	   				} else if (response.contains("Invalid Order Number")) {
	   					notfound = true;
	   					reset(telnet);
	   					systemConsole.append("Invalid Order Number!\n");
	   					break;
	   				}
	   	    	   Thread.sleep(300);
	           }
		       if(notfound)
		       {
		    	   break;
		       }
		       String[] itemPack = getItemPack(telnet);
		       String itemPackNum = itemPack[0].trim() + itemPack[1].trim();
		       if(autoSequence)
		       {
			       systemConsole.append("Item: " + itemPack[0] + "\n");
			       systemConsole.append("Pack: " + itemPack[1] + "\n");
			       sequenceInput =String.valueOf(sequenceGetter.getSequence(orderNum,itemPack[0].trim(),itemPack[1].trim()));
		       }
		       int sequenceInteger = 1000 + Integer.parseInt(sequenceInput);
		       String sequence = String.valueOf(sequenceInteger);
		       
		       systemConsole.append("Setting quantity\n");
		       if(!setQuantity(telnet,quantity))
		       {
		    	   continue;   
		       }
		       Thread.sleep(300);
		       if(!setDate(telnet))
		       {
		    	   continue;   
		       }
		       //Thread.sleep(300);
		       if(!waitResponseCount(telnet,"Hour"))
		       {
		    	   continue;
		       }
		       String hour = setHour(telnet);
		       waitResponse(telnet,"Sequence");
		       if(!setSequence(telnet,sequence))
		       {
		    	   continue;
		       }
		       if(prodNum.equals("22486"))
		       {
		    	   String copiesNum = "4";
		    	   setCopiesQuantity(telnet,copiesNum);
		    	   Thread.sleep(300);
		       }
		       systemConsole.append("Bulding label\n");
		       boolean success = buildLabel(telnet);
		       if(success)
		       {
		    	   sequenceGetter.updateSequence(itemPackNum, Integer.valueOf(hour) , Integer.valueOf(sequenceInput));
		    	   systemConsole.append(sequenceGetter.getSequenceMap().toString() + "\n");
		    	   break;
		       } else {
	    		   reset(telnet);
		    	   if(backflush)
		    	   {
		    		   break;
		    	   } else {
		    		   continue;
		    	   }
		       }
           } catch (Exception e)
           {
        	   e.printStackTrace();
           }
	    }
	}
	
	public boolean buildLabel(Telnet telnet) throws InterruptedException, IOException, ParseException
	{
		while(!checkCondition(telnet,"([0;7m  OK"))
		{
			if(checkCondition(telnet,"([0;7m  Yes"))
			{
				telnet.sendCommand("\n");
			} else {
				if(prodNum.equals("22486"))
				{
					telnet.sendCommand(getArrowKey("down"));
				} else {
					telnet.sendCommand(getArrowKey("up"));
				}
			}
			Thread.sleep(300);
		}
		Thread.sleep(300);
		telnet.sendCommand("\n");
		Thread.sleep(300);
		systemConsole.append("Checking ready\n");
		String buildResponse = checkBuildResponse(telnet);
		if(buildResponse.equals("ready"))
		{
			systemConsole.append("Ready!\n");
			telnet.sendCommand("\n");
			if(!setKillDate(telnet))
			{
				return false;
			}
			while(!checkCondition(telnet,"Order # [[0;7m"))
   			{
				if(checkCondition(telnet,"Lot Table"))
				{
					systemConsole.append("Updating lot table...\n");
				} else if (checkCondition(telnet,"Entry must appear"))
				{
					telnet.sendCommand("\n");
					telnet.sendCommand(getArrowKey("backspace"));
					Thread.sleep(500);
					setKillDate(telnet);
				}
				systemConsole.append("Looking for new order\n");
   		    	telnet.sendCommand("\n");
   		    	Thread.sleep(300);
   			}
			systemConsole.append("Done!!!\n");
			count = count + 1;
		    systemConsole.append("Successful builds: " + count + "\n");
		    return true;
		} else if (buildResponse.equals("Backflush")){
			backflush = true;
			return false;
		} else {
			systemConsole.append(buildResponse + "\n");
			return false;
		}
	}
	
	public boolean verifyOrder(Telnet telnet) throws InterruptedException
	{
		int count = 0;
 	   while(true)
 	   {
 		   	count++;
				systemConsole.append("Verifying order number filled...\n");
				if(checkCondition(telnet,"[" + orderNum))
				{
					return true;
				}
				Thread.sleep(300);
				if(count > 10)
				{
					return false;
				}
 	   }
	}
	
	public boolean setKillDate(Telnet telnet) throws InterruptedException, IOException
	{
		systemConsole.append("Setting kill date\n");
		waitResponse(telnet,"Kill Date");
		if(checkCondition(telnet,"Enter kill date"))
		{
			telnet.sendCommand(getDate("MMdd"));
			Thread.sleep(300);
			//telnet.sendCommand("");
		} else {
			Thread.sleep(200);
			while(checkCondition(telnet,"[[0;7m     "))
			{
				telnet.sendCommand(getDate("yyyy-MM-dd"));
				Thread.sleep(200);
			}
			systemConsole.append("Kill date set!\n");
			int count = 0;
			while(true)
			{
				count++;
				systemConsole.append("Confirming kill date...\n");
				if(checkCondition(telnet,"Kill Date [[0;7m" + getDate("yyyy-MM-dd") + " "))
				{
					break;
				}
				Thread.sleep(300);
				if(count > 30)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public void setCopiesQuantity(Telnet telnet,String copiesNum) throws IOException, InterruptedException
	{
		systemConsole.append("Setting copies quantity\n");
		while(!checkCondition(telnet,"Copies [[0;7m"))
		{
			systemConsole.append("Looking for copies quantity input\n");
		    telnet.sendCommand(getArrowKey("up"));
			Thread.sleep(300);
		}
	    telnet.sendCommand(copiesNum + "\n");
	}
	
	public String checkBuildResponse(Telnet telnet) throws InterruptedException, IOException, ParseException
	{
		int count = 0;
		while(true)
		{
			count++;
			systemConsole.append("Checking build response...\n");
			String response = telnet.getResponse();
			if(response.contains("Ready to build"))
			{
				return "ready";
			} else if(response.contains("Backflush"))
			{
				exactBackflush(response);
				return "Backflush";
			} else if (response.contains("([0;7m  OK"))
			{
				telnet.sendCommand("\n");
			}
			Thread.sleep(300);
			if(count > 30)
			{
				return "timeout";
			}
		}
	}
	
	
	public boolean setSequence(Telnet telnet,String sequence) throws InterruptedException, IOException
	{
		systemConsole.append("Setting sequence\n");
		if(prodNum.equals("12623"))
		{
			telnet.sendCommand("0");
		} else {
			telnet.sendCommand(sequence);
		}
	    Thread.sleep(300);
	    int count = 0;
	    while(!checkCondition(telnet,"([0;7mOkay"))
		{
	    	systemConsole.append("Confirming sequence\n");
		    telnet.sendCommand(getArrowKey("up"));
			Thread.sleep(300);
			count++;
			if(count > 30)
			{
				reset(telnet);
				return false;
			}
		}
		telnet.sendCommand("\n");
		Thread.sleep(300);
		systemConsole.append("Finalizing sequence\n");
		if(checkCondition(telnet,"Sequence ["))
		{
			//issue
			return setSequence(telnet,sequence);
		}
		return true;
	}
	
	public String setHour(Telnet telnet) throws InterruptedException
	{
		String hour = getHour();
		systemConsole.append("Setting hour\n");
		if(prodNum.equals("12623"))
		{
			telnet.sendCommand("98\n");
		} else {
			telnet.sendCommand(hour + "\n");
		}
	    Thread.sleep(300);
	    telnet.sendCommand("\n");
	    return hour;
	}
	
	public String getHour()
	{
		LocalTime currentTime = LocalTime.now();

        // Get the current hour in 24-hour format
        int currentHour = currentTime.getHour();
        currentHour = currentTime.getHour() + 1; //adjust day time saving

        // Adjust the hour by adding 24
        if(currentHour < 4)
        {        	
        	currentHour += 24;
        }
        
        return String.valueOf(currentHour);

	}
	
	
	public String[] getItemPack(Telnet telnet) throws InterruptedException
	{
		String[] array = new String[2];
		String response = telnet.getResponse();
		int itemIndex = response.indexOf("Item [[0;7m") + 12;
		array[0] = response.substring(itemIndex,itemIndex + 8);
		if(response.contains("Pack ["))
		{
			int packIndex = response.indexOf("Pack [") + 6;
			array[1] = response.substring(packIndex,packIndex + 6);
		} else {
			array[1] = "";
		}
		return array;
	}
	
	public boolean setDate(Telnet telnet) throws InterruptedException, IOException
	{
		systemConsole.append("Setting prod date\n");
		int count = 0;
		while(!checkCondition(telnet,"Prod Date [[0;7m"))
		{
			systemConsole.append("Looking for prod date\n");
		    telnet.sendCommand(getArrowKey("up"));
			Thread.sleep(300);
			if(checkCondition(telnet,"Full pallet is"))
		    {
		    	systemConsole.append("CONFIRMING PALLET QUANTITY...\n");
		    	 while(!checkCondition(telnet,"([0;7m  Yes"))
		 		{
		 		    telnet.sendCommand(getArrowKey("up"));
		 			Thread.sleep(300);
		 		}
			    telnet.sendCommand("\n");
		    } else if (checkCondition(telnet,"([0;7m  Yes"))
		    {
			    telnet.sendCommand("\n");
	 			Thread.sleep(500);
		    }
			count++;
			if(count > 30)
			{
				reset(telnet);
				return false;
			}
		}
		Thread.sleep(300);
	    telnet.sendCommand(getDate("MMdd") + "\n");
	    //telnet.sendCommand("\n");
	    return true;
	}
	
	public String getDate(String dateFormat)
	{
		LocalDate today;
		LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();
		if(currentHour < 5)
		{
			today = LocalDate.now().minusDays(1);
		} else {
			today= LocalDate.now();
		}
		
        // Define the formatter for MMDD
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        // Format the date
        String formattedDate = today.format(formatter);
        
        return formattedDate;
	}
	
	public boolean setQuantity(Telnet telnet,String quantity) throws InterruptedException, IOException
	{
		systemConsole.append("Setting quantity\n");
		int count = 0;
		while(true)
		{
			if(checkCondition(telnet,"Quantity [[0;7m") && !checkCondition(telnet,"Track #"))
			{
				break;
			}
			systemConsole.append("Looking for quantity input\n");
		    telnet.sendCommand(getArrowKey("up"));
			Thread.sleep(300);
			count++;
			if(count > 30)
			{
				reset(telnet);
				return false;
			}
		}
		systemConsole.append("Setting quantity input of " + quantity + "\n");
	    telnet.sendCommand(quantity + "\n");
	    Thread.sleep(300);
	    if(checkCondition(telnet,"Full pallet is"))
	    {
	    	systemConsole.append("CONFIRMING PALLET QUANTITY...\n");
	    	 while(!checkCondition(telnet,"([0;7m  Yes"))
	 		{
	 		    telnet.sendCommand(getArrowKey("up"));
	 			Thread.sleep(300);
	 		}
		    telnet.sendCommand("\n");
	    }
	    return true;
	}
	
	public void reset(Telnet telnet) throws IOException, InterruptedException
	{
		while(!checkCondition(telnet,"Order # [[0;7m"))
		{
			while(!checkCondition(telnet,"ReportProd"))
			{
			       telnet.sendCommand(getArrowKey("esc"));
			       Thread.sleep(700);
			       if(checkCondition(telnet,"Inventory"))
			       {
					    telnet.sendCommand("1");
			       } else if (checkCondition(telnet,"Do you really wish to log out"))
			       {
				       telnet.sendCommand(getArrowKey("esc"));
				       Thread.sleep(700);
					    telnet.sendCommand("2");
			       } else if (checkCondition(telnet,"Yes"))
			       {
				       telnet.sendCommand(getArrowKey("esc"));
				       Thread.sleep(700);
					    telnet.sendCommand("2");
			       }
			       Thread.sleep(700);
			}
		    telnet.sendCommand("1");
		    Thread.sleep(700);
		}
	}
	
	public void initialize(Telnet telnet) throws InterruptedException
	{
		   systemConsole.append("Loggining in telnet\n");
	       Thread.sleep(1000);
	       telnet.sendCommand("pdgwinterm7\n");
	       Thread.sleep(300);
	       telnet.sendCommand("Lucky7isthenumber!\n");
	       Thread.sleep(300);
	       telnet.sendCommand("poultry\n");
	       waitResponse(telnet,"Logon");
	       Thread.sleep(1000);
		   systemConsole.append("Loggining in user: " + username + "\n");
	       telnet.sendCommand(username + "\n");
	       Thread.sleep(300);
	       telnet.sendCommand(password + "\n");
	       Thread.sleep(300);
	       telnet.sendCommand("\n");
	       waitResponse(telnet,"Split Pallet");
	        Thread.sleep(300);
	       telnet.sendCommand("2");
	       Thread.sleep(300);
	       telnet.sendCommand("1");
	       waitResponse(telnet,"Order #");
	       systemConsole.append("Worker ready!\n");
	}
	
	public boolean checkCondition(Telnet telnet,String condition)
	{
		String response = telnet.getResponse();
		if(response.contains(condition))
		{
			return true;
		}		
		return false;
	}
	
	public void waitResponse(Telnet telnet,String condition) throws InterruptedException
	{
		outer:while(true)
        {
				String response = telnet.getResponse();
				if(response.contains(condition))
				{
					break outer;
				}
	    	   Thread.sleep(300);
        }
	}
	
	public boolean waitResponseCount(Telnet telnet,String condition) throws InterruptedException, IOException
	{
		int count = 0;
		while(true)
        {
				count++;
				String response = telnet.getResponse();
				if(response.contains(condition))
				{
					return true;
				}
	    	   Thread.sleep(300);
	    	   if(count > 30)
	    	   {		
					reset(telnet);
					return false;
	    	   }
        }
	}
	
	public String getArrowKey(String arrowKey) throws IOException {
	    String arrowCommand;
	    switch (arrowKey.toLowerCase()) {
	        case "up":
	            arrowCommand = "\u001B[A";   // Up Arrow
	            break;
	        case "down":
	            arrowCommand = "\u001B[B"; // Down Arrow
	            break;
	        case "right":
	            arrowCommand = "\u001B[C"; // Right Arrow
	            break;
	        case "left":
	            arrowCommand = "\u001B[D";  // Left Arrow
	            break;
	        case "esc":
	            arrowCommand = "\u001B";  // Esc key
	            break;
	        case "backspace": //backspace
	        	arrowCommand = "\u0008";
	        	break;
	        default:
	            throw new IllegalArgumentException("Invalid arrow key: " + arrowKey);
	    }

	    return arrowCommand;
	}
	
	public void exactBackflush(String input) throws IOException, ParseException
	{
		// Regular expression to match numbers, including decimal points
        Pattern pattern = Pattern.compile("\\d+\\.\\d+|\\d+");
        Matcher matcher = pattern.matcher(input);

        // List to store numbers that are greater than 100000
        List<Double> largeNumbers = new ArrayList<>();

        while (matcher.find()) {
            String numberStr = matcher.group();
            double number = Double.parseDouble(numberStr);
            
            if (number > 100000) {
                largeNumbers.add(number);
            }
        }
        
        for (double number : largeNumbers) {
        	systemConsole.append("BACKFLUSH: " + number + "\n");
        	saveBackflush(String.valueOf(number));
        }
	}
	
	public void saveBackflush(String number) throws IOException, ParseException
	{
		FileReader reader = new FileReader("backflush.json");

        // Parse the JSON content
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        
        if(!jsonObject.containsKey(prodNum))
        {
        	jsonObject.put(prodNum, new JSONArray());
        }
        
        JSONArray array = (JSONArray) jsonObject.get(prodNum);
        if(!array.contains(number))
        {
        	array.add(number);
        }
        
        FileWriter writer = new FileWriter("backflush.json");
        writer.write(jsonObject.toJSONString());
        writer.flush();
        writer.close();
    }
	
	public void setData(Command command)
	{
		this.prodNum = command.getProdNum();
		this.quantity = command.getQuantity();
		this.sequenceInput = command.getSequence();
	}

}