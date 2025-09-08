import okhttp3.*;
import paperwork.dsi.paperworkDSIGen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.*;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import config.Config;

public class test {
	private static int currentRow;

    public static void main(String[] args) throws Exception {
    	currentRow = 4;
    	try (InputStream inputStream = paperworkDSIGen.class.getClassLoader().getResourceAsStream("paperwork/marel/recap_marel.xlsx");
    			Workbook workbook = new XSSFWorkbook(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0); // First sheet

                setDate(sheet);
                emptyBoxToExcel(workbook,sheet,2);

                // Save changes
                try (FileOutputStream fos = new FileOutputStream("C:\\Users\\tdphu\\OneDrive\\Desktop\\recap_marel.xlsx")) {
                    workbook.write(fos);
                }

                System.out.println("Cell updated successfully!");
                
                //File file = new File("recap_output/breast.xlsx");
                //exportPDF(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
    
	public static void emptyBoxToExcel(Workbook workbook,Sheet sheet,int height)
	{
		setBorderAroundProductCell(workbook,sheet,height);
    	setProductCodeCell(workbook,sheet,height,"");
    	setTotalCell(workbook,sheet,height,"");
    	currentRow += height;
	}
	
	public static void setTotalCell(Workbook workbook,Sheet sheet,int height,String quantity)
	{
		int startRow = currentRow -1 ;
        int endRow = currentRow + height - 2;
   	
   	 	CellRangeAddress mergedRegion = new CellRangeAddress(startRow, endRow, 12, 12);
        sheet.addMergedRegion(mergedRegion);
        
     // Create font: size 28, underline
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);

        // Create cell style with border, alignment, and font
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
       
        //set product code's cell style
        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) row = sheet.createRow(rowIndex);

            for (int colIndex = 12; colIndex <= 12; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) cell = row.createCell(colIndex);
                cell.setCellStyle(style);
            }
        }
        
        
        Cell topLeftCell = sheet.getRow(startRow).getCell(12);
        topLeftCell.setCellValue(quantity);
	}
	
	public static void setBorderAroundProductCell(Workbook workbook,Sheet sheet,int height)
	{
		int startRow = currentRow -1 ;
		int endRow = currentRow + height - 2;;
        int startCol = 0;
        int endCol = 12;
        
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        
        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) row = sheet.createRow(rowIndex);

            for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) cell = row.createCell(colIndex);

                CellStyle style = workbook.createCellStyle();
                style.setFont(font);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                // Default: thin borders all around
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);

                style.setTopBorderColor(IndexedColors.BLACK.getIndex());
                style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                style.setRightBorderColor(IndexedColors.BLACK.getIndex());

                // Now, override outer borders with medium
                if (rowIndex == startRow) {
                    style.setBorderTop(BorderStyle.MEDIUM);
                }
                if (rowIndex == endRow) {
                    style.setBorderBottom(BorderStyle.MEDIUM);
                }
                if (colIndex == startCol) {
                    style.setBorderLeft(BorderStyle.MEDIUM);
                }
                if (colIndex == endCol) {
                    style.setBorderRight(BorderStyle.MEDIUM);
                }

                cell.setCellStyle(style);
            }
        }
	}
	
	public static void setProductCodeCell(Workbook workbook,Sheet sheet,int height,String productCode)
	{
		int startRow = currentRow -1 ;
        int endRow = currentRow + height - 2;
   	
   	 CellRangeAddress mergedRegion = new CellRangeAddress(startRow, endRow, 0, 1);
        sheet.addMergedRegion(mergedRegion);
        
     // Create font: size 28, underline
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setUnderline(Font.U_SINGLE);

        // Create cell style with border, alignment, and font
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
       
        //set product code's cell style
        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) row = sheet.createRow(rowIndex);

            for (int colIndex = 0; colIndex <= 1; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) cell = row.createCell(colIndex);
                cell.setCellStyle(style);
            }
        }
        
        
        Cell topLeftCell = sheet.getRow(startRow).getCell(0);
        topLeftCell.setCellValue(productCode);
	}
    
	public static void setDate(Sheet sheet)
	{
		setCellValue(sheet, "M", 2, getDate("MM/dd/yyyy"));
	}
	
	  public static void setCellValue(Sheet sheet, String columnLetter, int rowNumber, String value) {
	        int columnIndex = columnLetterToIndex(columnLetter);
	        int rowIndex = rowNumber - 1; // Excel rows start at 1, POI uses 0-based

	        Row row = sheet.getRow(rowIndex);
	        if (row == null) row = sheet.createRow(rowIndex);

	        Cell cell = row.getCell(columnIndex);
	        if (cell == null) cell = row.createCell(columnIndex);

	        cell.setCellValue(value);
	    }
	  
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
