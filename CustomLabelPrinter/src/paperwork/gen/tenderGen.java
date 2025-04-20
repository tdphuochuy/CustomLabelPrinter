package paperwork.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import paperwork.Product;

public class tenderGen extends excelGen{
	public Map<String,Map<Integer,List<Product>>> trimMap = new TreeMap<>();;
	public Map<String,Map<Integer,List<Product>>> kneeboneMap = new TreeMap<>();;
	public Map<String,Map<Integer,List<Product>>> keelboneMap = new TreeMap<>();;
	public Map<String,Map<Integer,List<Product>>> skinMap = new TreeMap<>();;
	public tenderGen()
	{
		
	}
	
	public void addTrim(Product product)
	{
		String productCode = product.getCode();
		if(!trimMap.containsKey(productCode))
		{
			trimMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!trimMap.get(productCode).containsKey(hour))
		{
			trimMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		trimMap.get(productCode).get(hour).add(product);
	}
	
	public void addKneeBone(Product product)
	{
		String productCode = product.getCode();
		if(!kneeboneMap.containsKey(productCode))
		{
			kneeboneMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!kneeboneMap.get(productCode).containsKey(hour))
		{
			kneeboneMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		kneeboneMap.get(productCode).get(hour).add(product);
	}
	
	public void addKeelBone(Product product)
	{
		String productCode = product.getCode();
		if(!keelboneMap.containsKey(productCode))
		{
			keelboneMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!keelboneMap.get(productCode).containsKey(hour))
		{
			keelboneMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		keelboneMap.get(productCode).get(hour).add(product);
	}
	
	public void addSkin(Product product)
	{
		String productCode = product.getCode();
		if(!skinMap.containsKey(productCode))
		{
			skinMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!skinMap.get(productCode).containsKey(hour))
		{
			skinMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		skinMap.get(productCode).get(hour).add(product);
	}
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "N", 2, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Sheet sheet)
    {
    	
    }
}