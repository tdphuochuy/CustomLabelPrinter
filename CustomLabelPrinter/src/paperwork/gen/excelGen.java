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

import paperwork.Product;

abstract class excelGen {
	public Map<String,Map<Integer,List<Product>>> productMap = new TreeMap<>();
	abstract public void setDate(Sheet sheet);
	
	abstract public void clear(Workbook workbook,Sheet sheet);
	
	abstract public void generateExcel();
	
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
    
	
	public static void exportPDF(String excelFilePath)
	{
        String outputDir = "C:\\Users\\tdphu\\git\\customlabelprinter\\CustomLabelPrinter\\recap_output\\";

        // Full path to LibreOffice Portable or installed soffice.exe
        String libreOfficePath = "D:\\Download\\LibreOfficePortable\\App\\libreoffice\\program\\soffice.exe"; // Adjust path if needed

        // Construct the command to run LibreOffice headless
        String command = libreOfficePath + " --headless --convert-to pdf --outdir " + outputDir + " " + excelFilePath;

        try {
            // Run the process
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Conversion successful!");
            } else {
                System.out.println("Error during conversion. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
	 
	 abstract public String hourToLetter(int hour);
	 
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
	            return String.valueOf(value);             // Keep decimal if needed
	        }
	    }
}