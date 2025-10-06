package paperwork.gen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import config.Config;
import paperwork.Product;

public class recapGenMarel extends excelGen{
	private String name,floormanName;
	private thighGen thigh;
	private drumGen drum;
	private wingGen wing;
	private Map<String,List<List<Integer>>> condemnMap;
	private Map<String,List<Product>> reworkMap;
	public recapGenMarel(String name, String floormanName,Map<String,List<Product>> reworkMap, thighGen thigh, drumGen drum, wingGen wing, Map<String,List<List<Integer>>> condemnMap) {
		this.name = name;
		this.floormanName = floormanName;
		this.thigh = thigh;
		this.drum = drum;
		this.wing = wing;
		this.condemnMap = condemnMap;
		this.reworkMap = reworkMap;
	}
	
	@Override
	public void setDate(Sheet sheet) {
    	setCellValue(sheet, "E", 6, getDate("MM/dd/yyyy"));
	}
	
	public void setDate2(Sheet sheet) {
    	setCellValue(sheet, "C", 3, getDate("MM/dd/yyyy"));
	}
	
	public void setDate3(Sheet sheet) {
    	setCellValue(sheet, "B", 2, getDate("MM/dd/yyyy"));
	}
	
	public void setName(Sheet sheet) {
    	setCellValue(sheet, "B", 6, name);
	}
	
	@Override
	public void clear(Workbook workbook, Sheet sheet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void generateExcel() {
		// TODO Auto-generated method stub
		generateRecap1();
		generateRecap2();
		generateCondemnSheet();
	}
	
	public void generateRecap1()
	{
		try (FileInputStream fis = new FileInputStream(Config.ppwExcelPath);
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(3); 

	            setDate(sheet);
	            //clear(workbook,sheet);
	            setName(sheet);
	            
	            //set WOGS
	        	setCellValue(sheet, "B", 10, formatDouble(drum.Break1WogWeight));
	        	setCellValue(sheet, "C", 10, formatDouble(drum.Break2WogWeight));
	        	setCellValue(sheet, "D", 10, formatDouble(drum.Break3WogWeight));
	        	setCellValue(sheet, "E", 10, formatDouble(drum.getTotalWog()));
	        	
	        	//set Legs
	        	setCellValue(sheet, "B", 11, formatDouble(drum.Break1LegWeight));
	        	setCellValue(sheet, "C", 11, formatDouble(drum.Break2LegWeight));
	        	setCellValue(sheet, "D", 11, formatDouble(drum.Break3LegWeight));
	        	setCellValue(sheet, "E", 11, formatDouble(drum.getTotalLeg()));
	        	
	        	//set Carcass
	        	setCellValue(sheet, "B", 12, formatDouble(drum.Break1CarcassWeight));
	        	setCellValue(sheet, "C", 12, formatDouble(drum.Break2CarcassWeight));
	        	setCellValue(sheet, "D", 12, formatDouble(drum.Break3CarcassWeight));
	        	setCellValue(sheet, "E", 12, formatDouble(drum.getTotalCarcass()));
	        	
	        	//set Thighs
	        	setCellValue(sheet, "B", 20, formatDouble(thigh.getBreak1Weight()));
	        	setCellValue(sheet, "C", 20, formatDouble(thigh.getBreak2Weight()));
	        	setCellValue(sheet, "D", 20, formatDouble(thigh.getBreak3Weight()));
	        	setCellValue(sheet, "E", 20, formatDouble(thigh.getTotalWeight()));
	        	
	        	//set Wings
	        	setCellValue(sheet, "B", 21, formatDouble(wing.getBreak1Weight()));
	        	setCellValue(sheet, "C", 21, formatDouble(wing.getBreak2Weight()));
	        	setCellValue(sheet, "D", 21, formatDouble(wing.getBreak3Weight()));
	        	setCellValue(sheet, "E", 21, formatDouble(wing.getTotalWeight()));
	        	
	        	//set Drums
	        	setCellValue(sheet, "B", 22, formatDouble(drum.getBreak1Weight()));
	        	setCellValue(sheet, "C", 22, formatDouble(drum.getBreak2Weight()));
	        	setCellValue(sheet, "D", 22, formatDouble(drum.getBreak3Weight()));
	        	setCellValue(sheet, "E", 22, formatDouble(drum.getTotalWeight()));
	        	
	        	//wing tips
	        	setCellValue(sheet, "B", 17, String.valueOf(getCondemnWeight(condemnMap.get("wingtips").get(0))));
	        	setCellValue(sheet, "C", 17, String.valueOf(getCondemnWeight(condemnMap.get("wingtips").get(1))));
	        	setCellValue(sheet, "D", 17, String.valueOf(getCondemnWeight(condemnMap.get("wingtips").get(2))));
	        	setCellValue(sheet, "E", 17, String.valueOf(getCondemnWeightTotal(condemnMap.get("wingtips"))));

	        	//wings
	        	setCellValue(sheet, "B", 16, String.valueOf(getCondemnWeight(condemnMap.get("wings").get(0))));
	        	setCellValue(sheet, "C", 16, String.valueOf(getCondemnWeight(condemnMap.get("wings").get(1))));
	        	setCellValue(sheet, "D", 16, String.valueOf(getCondemnWeight(condemnMap.get("wings").get(2))));
	        	setCellValue(sheet, "E", 16, String.valueOf(getCondemnWeightTotal(condemnMap.get("wings"))));
	        	
	        	//lollipop
	        	setCellValue(sheet, "B", 15, String.valueOf(getCondemnWeight(condemnMap.get("lollipop").get(0))));
	        	setCellValue(sheet, "C", 15, String.valueOf(getCondemnWeight(condemnMap.get("lollipop").get(1))));
	        	setCellValue(sheet, "D", 15, String.valueOf(getCondemnWeight(condemnMap.get("lollipop").get(2))));
	        	setCellValue(sheet, "E", 15, String.valueOf(getCondemnWeightTotal(condemnMap.get("lollipop"))));
	        	
	        	//miscut
	        	setCellValue(sheet, "B", 14, String.valueOf(getCondemnWeight(condemnMap.get("miscut").get(0))));
	        	setCellValue(sheet, "C", 14, String.valueOf(getCondemnWeight(condemnMap.get("miscut").get(1))));
	        	setCellValue(sheet, "D", 14, String.valueOf(getCondemnWeight(condemnMap.get("miscut").get(2))));
	        	setCellValue(sheet, "E", 14, String.valueOf(getCondemnWeightTotal(condemnMap.get("miscut"))));
	        	
	            // Save changes
	            try (FileOutputStream fos = new FileOutputStream(Config.ppwExcelPath)) {
	                workbook.write(fos);
	            }

	            System.out.println("Cell updated successfully!");
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	public void generateRecap2()
	{
		try (FileInputStream fis = new FileInputStream(Config.ppwExcelPath);
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(4); 

	            setDate2(sheet);
	            //clear(workbook,sheet);
	            
	            
	            //set rework
	            //coneline
	            if(reworkMap.containsKey("248422"))
	            {
	            	int i = 3;
	            	for(Product product: reworkMap.get("248422"))
	            	{
	            		setCellValue(sheet,i,29,formatDouble(product.getWeight()));
	            		i = i+ 2;
	            	}
	            	
	            	setCellValue(sheet,"P",29,formatDouble(getReworkWeightTotal("248422")));
	            }
	            
	            //thigh
	            if(reworkMap.containsKey("100504"))
	            {
	            	int i = 3;
	            	for(Product product: reworkMap.get("100504"))
	            	{
	            		setCellValue(sheet,i,30,formatDouble(product.getWeight()));
	            		i = i+ 2;
	            	}
	            	
	            	setCellValue(sheet,"P",30,formatDouble(getReworkWeightTotal("100504")));
	            }
	            
	            //leg 1/4
	            if(reworkMap.containsKey("101047"))
	            {
	            	int i = 3;
	            	for(Product product: reworkMap.get("101047"))
	            	{
	            		setCellValue(sheet,i,31,formatDouble(product.getWeight()));
	            		i = i+ 2;
	            	}
	            	
	            	setCellValue(sheet,"P",31,formatDouble(getReworkWeightTotal("101047")));
	            }
	            
	            //drum stick
	            if(reworkMap.containsKey("100570"))
	            {
	            	int i = 3;
	            	for(Product product: reworkMap.get("100570"))
	            	{
	            		setCellValue(sheet,i,32,formatDouble(product.getWeight()));
	            		i = i+ 2;
	            	}
	            	
	            	setCellValue(sheet,"P",32,formatDouble(getReworkWeightTotal("100570")));
	            }
	            
	            //Whole wings
	            if(reworkMap.containsKey("100627"))
	            {
	            	int i = 3;
	            	for(Product product: reworkMap.get("100627"))
	            	{
	            		setCellValue(sheet,i,33,formatDouble(product.getWeight()));
	            		i = i+ 2;
	            	}
	            	
	            	setCellValue(sheet,"P",33,formatDouble(getReworkWeightTotal("100627")));
	            }
	            
	          //Split wings
	            if(reworkMap.containsKey("100590"))
	            {
	            	int i = 3;
	            	for(Product product: reworkMap.get("100590"))
	            	{
	            		setCellValue(sheet,i,34,formatDouble(product.getWeight()));
	            		i = i+ 2;
	            	}
	            	
	            	setCellValue(sheet,"P",34,formatDouble(getReworkWeightTotal("100590")));
	            }
	        	
	            // Save changes
	            try (FileOutputStream fos = new FileOutputStream(Config.ppwExcelPath)) {
	                workbook.write(fos);
	            }

	            System.out.println("Cell updated successfully!");
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	public void generateCondemnSheet()
	{
		try (FileInputStream fis = new FileInputStream(Config.ppwExcelPath);
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(5); 

	            setDate3(sheet);
	            //clear(workbook,sheet);
	            
	            int currentRow = 5;
	            
            	setCellValue(sheet,"B",currentRow,floormanName);
            	
            	//Set wings break 1
	            for(Integer weight: condemnMap.get("wings").get(0))
	            {
	            	setCellValue(sheet,"C",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
	            //set wingtips break1
	            int currentColumn = 3;
	            for(Integer weight: condemnMap.get("wingtips").get(0))
	            {
	            	setCellValue(sheet,currentColumn,currentRow,String.valueOf(weight));
	            	currentColumn++;
	            	if(currentColumn > 3)
	            	{ 
	            		currentColumn = 3;
	            		currentRow++;
	            	}
	            }
	            if(currentColumn > 3)
	            {
	            	currentRow++;
	            }
	            
	            //Set lollipop break 1
	            for(Integer weight: condemnMap.get("lollipop").get(0))
	            {
	            	setCellValue(sheet,"G",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
	            //Set miscut break 1
	            for(Integer weight: condemnMap.get("miscut").get(0))
	            {
	            	setCellValue(sheet,"H",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
            	setCellValue(sheet,"B",currentRow,floormanName);
            	
            	//Set wings break 2
	            for(Integer weight: condemnMap.get("wings").get(1))
	            {
	            	setCellValue(sheet,"C",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
	            //set wingtips break 2
	            currentColumn = 3;
	            for(Integer weight: condemnMap.get("wingtips").get(1))
	            {
	            	setCellValue(sheet,currentColumn,currentRow,String.valueOf(weight));
	            	currentColumn++;
	            	if(currentColumn > 3)
	            	{ 
	            		currentColumn = 3;
	            		currentRow++;
	            	}
	            }
	            if(currentColumn > 3)
	            {
	            	currentRow++;
	            }
	            
	            //Set lollipop break 2
	            for(Integer weight: condemnMap.get("lollipop").get(1))
	            {
	            	setCellValue(sheet,"G",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
	            //Set miscut break 2
	            for(Integer weight: condemnMap.get("miscut").get(1))
	            {
	            	setCellValue(sheet,"H",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
            	setCellValue(sheet,"B",currentRow,floormanName);

            	//Set wings break 3
	            for(Integer weight: condemnMap.get("wings").get(2))
	            {
	            	setCellValue(sheet,"C",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
	            //set wingtips break 3
	            currentColumn = 3;
	            for(Integer weight: condemnMap.get("wingtips").get(2))
	            {
	            	setCellValue(sheet,currentColumn,currentRow,String.valueOf(weight));
	            	currentColumn++;
	            	if(currentColumn > 3)
	            	{ 
	            		currentColumn = 3;
	            		currentRow++;
	            	}
	            }
	            if(currentColumn > 3)
	            {
	            	currentRow++;
	            }
	            
	            //Set lollipop break 3
	            for(Integer weight: condemnMap.get("lollipop").get(2))
	            {
	            	setCellValue(sheet,"G",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
	            //Set miscut break 3
	            for(Integer weight: condemnMap.get("miscut").get(2))
	            {
	            	setCellValue(sheet,"H",currentRow,String.valueOf(weight));
	            	currentRow++;
	            }
	            
	        	setCellValue(sheet, "C", 30, String.valueOf(getCondemnWeightTotal(condemnMap.get("wings"))));
	        	setCellValue(sheet, "D", 30, String.valueOf(getCondemnWeightTotal(condemnMap.get("wingtips"))));
	        	setCellValue(sheet, "G", 30, String.valueOf(getCondemnWeightTotal(condemnMap.get("lollipop"))));
	        	setCellValue(sheet, "H", 30, String.valueOf(getCondemnWeightTotal(condemnMap.get("miscut"))));

	            
	            // Save changes
	            try (FileOutputStream fos = new FileOutputStream(Config.ppwExcelPath)) {
	                workbook.write(fos);
	            }

	            System.out.println("Cell updated successfully!");
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	public int getCondemnWeight(List<Integer> list)
	{
		int weight = 0;
		
		for(int condemn: list)
		{
			weight += condemn;
		}
		
		return weight;
	}
	
	public int getCondemnWeightTotal(List<List<Integer>> list)
	{
		int weight = 0;
		
		for(List<Integer> innerList: list)
		{
			for(int condemn: innerList)
			{
				weight += condemn;
			}
		}
		
		return weight;
	}
	
	public double getReworkWeightTotal(String itemPack)
	{
		double total = 0;
		
		for(Product product: reworkMap.get(itemPack))
		{
			total += product.getWeight();
		}
		
		return total;
	}
}