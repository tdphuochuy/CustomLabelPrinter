package paperwork.gen;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import config.Config;
import paperwork.Product;

abstract class excelGen {
	public Map<String,Map<Integer,List<Product>>> productMap = new TreeMap<>();
	public String filePath = System.getProperty("user.home") + "\\OneDrive\\Desktop\\recap_output\\recap.xlsx";
    public String outputPath = System.getProperty("user.home") + "\\OneDrive\\Desktop\\recap_output\\recap.xlsx";
	public double Break1Weight = 0;
    public double Break2Weight = 0;
    public double Break3Weight = 0;
    public int[] times;
	abstract public void setDate(Sheet sheet);
	
	abstract public void clear(Workbook workbook,Sheet sheet);
	
	abstract public void generateExcel();
	
    public double getBreak1Weight() {
		return Break1Weight;
	}

	public double getBreak2Weight() {
		return Break2Weight;
	}

	public double getBreak3Weight() {
		return Break3Weight;
	}
	
	public double getTotalWeight() {
		return Break1Weight + Break2Weight + Break3Weight;
	}
	
	public void addProduct(Product product)
	{
		String productCode = product.getCode();
		if(!productMap.containsKey(productCode))
		{
			productMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!productMap.get(productCode).containsKey(hour))
		{
			productMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		if(hour <= times[0])
		{
			Break1Weight += product.getWeight();
		} else if (hour > times[0] && hour <= times[1])
		{
			Break2Weight += product.getWeight();
		} else {
			Break3Weight += product.getWeight();
		}
		
		
		productMap.get(productCode).get(hour).add(product);
	}
	   // Converts column letter to index and sets cell value
    public void setCellValue(Sheet sheet, String columnLetter, int rowNumber, String value) {
        int columnIndex = columnLetterToIndex(columnLetter);
        int rowIndex = rowNumber - 1; // Excel rows start at 1, POI uses 0-based

        Row row = sheet.getRow(rowIndex);
        if (row == null) row = sheet.createRow(rowIndex);

        Cell cell = row.getCell(columnIndex);
        if (cell == null) cell = row.createCell(columnIndex);

        cell.setCellValue(value);
    }
    
    public void setCellValue(Sheet sheet, int columnIndex, int rowNumber, String value) {
        int rowIndex = rowNumber - 1; // Excel rows start at 1, POI uses 0-based

        Row row = sheet.getRow(rowIndex);
        if (row == null) row = sheet.createRow(rowIndex);

        Cell cell = row.getCell(columnIndex);
        if (cell == null) cell = row.createCell(columnIndex);

        cell.setCellValue(value);
    }

    // Helper to convert column letters (e.g. "A", "C", "AA") to zero-based index
    public int columnLetterToIndex(String column) {
        column = column.toUpperCase();
        int index = 0;
        for (int i = 0; i < column.length(); i++) {
            index *= 26;
            index += column.charAt(i) - 'A' + 1;
        }
        return index - 1;
    }
	
	 public static String getDate(String dateFormat)
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
	 
	   public String hourToLetter(int number) {
	    	// Convert number to letter using offset
	        int ascii = number + 52;

	        // Ensure result is a valid uppercase letter
	        if (ascii < 'A' || ascii > 'Z') {
	        	ascii = getHour() + 52;
	        }

	        return String.valueOf((char) ascii);
	  } 
	   
	   
		public int getHour()
		{
			LocalTime currentTime = LocalTime.now();

	        // Get the current hour in 24-hour format
	        //int currentHour = currentTime.getHour();
	        int currentHour = currentTime.getHour() + Config.dayTimeSaving;

	        // Adjust the hour by adding 24
	        if(currentHour < 5)
	        {        	
	        	currentHour += 24;
	        }
	        
	        return Math.min(26, currentHour);
		}
    
	 public double getTotalWeightByProduct(Map<Integer,List<Product>> map)
	 {
		 double totalWeight = 0;
		 for(Integer key : map.keySet())
		 {
			 List<Product> list = map.get(key);
			 for(Product product: list)
			 {
				 totalWeight += product.getWeight();
			 }
		 }
		 
		 return totalWeight;
	 }
	 
	 public int getTotalCaseByProduct(Map<Integer,List<Product>> map)
	 {
		 int totalcase = 0;
		 for(Integer key : map.keySet())
		 {
			 List<Product> list = map.get(key);
			 for(Product product: list)
			 {
				 totalcase += product.getQuantity();
			 }
		 }
		 
		 return totalcase;
	 }
	 
	 public String formatDouble(double value) {
	        if (value == (long) value) {
	            return String.format("%d", (long) value);  // Remove .0 if whole number
	        } else {
	            return String.format("%.2f", value);          // Keep decimal if needed
	        }
	    }
}