import okhttp3.*;
import paperwork.Product;
import paperwork.dsi.paperworkDSIGen;
import paperwork.gen.breastGen;
import paperwork.gen.drumGen;
import paperwork.gen.recapGenMarel;
import paperwork.gen.thighGen;
import paperwork.gen.wingGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import config.Config;

public class test {

    public static void main(String[] args) throws Exception {
		double totalNoRibWeight = 50000;
		double totalDSITrimWeight = 6200;


		
		
		try (FileInputStream fis = new FileInputStream(System.getProperty("user.home") + "\\Desktop\\DSITrim.xlsx");
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(1);
	            
	            System.out.println(sheet.getSheetName());
	            int lastRowIndex = sheet.getLastRowNum();
	            System.out.println(lastRowIndex);
	            int rowIndex = lastRowIndex + 1;
	            
	            String date = getDate("MM/dd/yyyy");
	            
	            for(int i = rowIndex; i > Math.max(0, lastRowIndex - 5); i--)
	            {
	            	System.out.println(rowIndex);
	            	Row row = sheet.getRow(i);
	            	if (row == null) row = sheet.createRow(rowIndex);

	                Cell cell = row.getCell(0);
	                
	                if(cell != null)
	                {
		                if(cell.getStringCellValue().equals(date))
		                {
		                	rowIndex = i; 
		                	break;
		                }
	                }
	            }
	            
            	System.out.println(rowIndex);
	            Row row = sheet.getRow(rowIndex);
            	if (row == null) {
            		row = sheet.createRow(rowIndex);
            	}
            	if(row.getCell(0) == null)
            	{
            		row.createCell(0);
            		row.createCell(1);
            		row.createCell(2);
            	}
	            Cell dateCell = row.getCell(0);
	            dateCell.setCellValue(date);
	            Cell TrimCell = row.getCell(1);
	            TrimCell.setCellValue(totalDSITrimWeight);
	            Cell NoRibcell = row.getCell(2);
	            NoRibcell.setCellValue(totalNoRibWeight);
	            
	        	
	            // Save changes
	            try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + "\\Desktop\\DSITrim.xlsx")) {
	                workbook.write(fos);
	            }

	            System.out.println("DSI Trim updated successfully!");
	            

	        } catch (IOException e) {
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

}
