import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class test {

    public static void main(String[] args) {
        String filePath = "recap/carcass.xlsx";
        String outputPath = "recap_output/carcass.xlsx";

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // First sheet

            // Example usage: write "Test value" to cell C2
            setDate(sheet);
            clearValue(sheet);
            // Save changes
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }

            System.out.println("Cell updated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "N", 2, getDate("MM/dd/yyyy"));
    }
    
    public static void clearValue(Sheet sheet)
    {
    	// E5 = row 4, col 4; N15 = row 14, col 13
        for (int rowIndex = 4; rowIndex <= 14; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            for (int colIndex = 4; colIndex <= 13; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    cell.setBlank(); // Clears the value
                    // Alternatively, you can do: row.removeCell(cell);
                }
            }
        }
    }

    // Converts column letter to index and sets cell value
    public static void setCellValue(Sheet sheet, String columnLetter, int rowNumber, String value) {
        int columnIndex = columnLetterToIndex(columnLetter);
        int rowIndex = rowNumber - 1; // Excel rows start at 1, POI uses 0-based

        Row row = sheet.getRow(rowIndex);
        if (row == null) row = sheet.createRow(rowIndex);

        Cell cell = row.getCell(columnIndex);
        if (cell == null) cell = row.createCell(columnIndex);

        cell.setCellValue(value);
    }

    // Helper to convert column letters (e.g. "A", "C", "AA") to zero-based index
    public static int columnLetterToIndex(String column) {
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
}
