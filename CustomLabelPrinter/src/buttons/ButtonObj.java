package buttons;

import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JLabel;

public class ButtonObj {
    private String productCode;
    private String quantity = "";
    private JLabel currentQuantitylbl,nextQuantitylbl;
    private boolean enabled;
    private Queue<String> queue;
    public ButtonObj(String productCode, String quantity,boolean enabled,JLabel currentQuantitylbl,JLabel nextQuantitylbl) {
        queue = new LinkedList<>();
    	this.productCode = productCode;
        setQuantity(quantity);
        this.quantity = quantity;
        this.enabled = enabled;
        this.currentQuantitylbl = currentQuantitylbl;
        this.nextQuantitylbl = nextQuantitylbl;
    }

    // Getters and setters
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getQuantity() {
    	if(queue.size() > 0)
    	{
    		String oldQuantity = queue.poll();
    		if(queue.size() > 0)
    		{
        		currentQuantitylbl.setText(queue.peek());
    		} else {
        		currentQuantitylbl.setText("0");
    		}
    		
    		if(queue.size() > 1)
    		{
    			String secondItem = ((LinkedList<String>) queue).get(1);
    			nextQuantitylbl.setText(secondItem);
    		} else {
    			nextQuantitylbl.setText("0");
    		}
			
			return oldQuantity;
    	}
    	
    	return null;
    }
    
    public void setQuantity(String text) {
    	if(!text.trim().equals(quantity.trim()))
    	{
    		quantity = text;
	    	queue.clear();
	    	String[] quantityArray = text.split(",");
			   for (String line : quantityArray) {
				   int quantity = 100;
				   String[] quantityText = line.split("#");
				   if(quantityText.length > 1)
				   {
					 quantity = Integer.parseInt(quantityText[1]);
				   } else if (quantityArray.length > 1)
				   {
					   quantity = 1;
				   }
				   for(int i = 0; i < quantity;i++)
				   {
					   queue.offer(quantityText[0]);
				   }
			   }
			   if(currentQuantitylbl != null)
			   {
				   currentQuantitylbl.setText(queue.peek());
				   if(queue.size() > 1)
				   {
					   String secondItem = ((LinkedList<String>) queue).get(1);
					   nextQuantitylbl.setText(secondItem);
				   } else {
		    			nextQuantitylbl.setText("0");
				   }
			   }
    	}
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
