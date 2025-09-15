package paperwork.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import config.Config;
import paperwork.Product;
import paperwork.dsi.paperworkDSIGen;

public class drumGen extends excelGen{
	private int currentRow;
	public Map<String,Map<Integer,List<Product>>> wogMap = new TreeMap<>();
	public Map<String,Map<Integer,List<Product>>> legMap = new TreeMap<>();
	public Map<String,Map<Integer,List<Product>>> carcassMap = new TreeMap<>();

	public double Break1CarcassWeight = 0;
    public double Break2CarcassWeight = 0;
    public double Break3CarcassWeight = 0;
    
	public double Break1WogWeight = 0;
    public double Break2WogWeight = 0;
    public double Break3WogWeight = 0;
    
	public double Break1LegWeight = 0;
    public double Break2LegWeight = 0;
    public double Break3LegWeight = 0;
	
	public drumGen(int[] times)
	{
		currentRow = 4;
		this.times = times;
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
	
	public void addCarcass(Product product)
	{
		String productCode = product.getCode();
		if(!carcassMap.containsKey(productCode))
		{
			carcassMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!carcassMap.get(productCode).containsKey(hour))
		{
			carcassMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		if(hour <= times[0])
		{
			Break1CarcassWeight += product.getWeight();
		} else if (hour > times[0] && hour <= times[1])
		{
			Break2CarcassWeight += product.getWeight();
		} else {
			Break3CarcassWeight += product.getWeight();
		}
		
		carcassMap.get(productCode).get(hour).add(product);
	}
	
	public void addWog(Product product)
	{
		String productCode = product.getCode();
		if(!wogMap.containsKey(productCode))
		{
			wogMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!wogMap.get(productCode).containsKey(hour))
		{
			wogMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		if(hour <= times[0])
		{
			Break1WogWeight += product.getWeight();
		} else if (hour > times[0] && hour <= times[1])
		{
			Break2WogWeight += product.getWeight();
		} else {
			Break3WogWeight += product.getWeight();
		}
		
		wogMap.get(productCode).get(hour).add(product);
	}
	
	public void addLeg(Product product)
	{
		String productCode = product.getCode();
		if(!legMap.containsKey(productCode))
		{
			legMap.put(productCode, new TreeMap<Integer,List<Product>>());
		}
		int hour = product.getHour();
		if(!legMap.get(productCode).containsKey(hour))
		{
			legMap.get(productCode).put(hour, new ArrayList<Product>());
		}
		
		if(hour <= times[0])
		{
			Break1LegWeight += product.getWeight();
		} else if (hour > times[0] && hour <= times[1])
		{
			Break2LegWeight += product.getWeight();
		} else {
			Break3LegWeight += product.getWeight();
		}
		
		legMap.get(productCode).get(hour).add(product);
	}
	
	public void generateExcel()
	{		
        try (FileInputStream fis = new FileInputStream(Config.ppwExcelPath);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(1); // Second sheet

            setDate(sheet);
            
            emptyBoxToExcel(workbook,sheet,2);
            caseToExcel(workbook,sheet);
            emptyBoxToExcel(workbook,sheet,2);
            emptyBoxToExcel(workbook,sheet,2);
            comboToExcel(workbook,sheet);
            emptyBoxToExcel(workbook,sheet,3);
            wogToExcel(workbook,sheet);
            legToExcel(workbook,sheet);
            carcassToExcel(workbook,sheet);
            // Save changes
            try (FileOutputStream fos = new FileOutputStream(Config.ppwExcelPath)) {
                workbook.write(fos);
            }

            System.out.println("Cell updated successfully!");
            
            //File file = new File("recap_output/breast.xlsx");
            //exportPDF(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void emptyBoxToExcel(Workbook workbook,Sheet sheet,int height)
	{
		setBorderAroundProductCell(workbook,sheet,height);
    	setProductCodeCell(workbook,sheet,height,"");
    	setTotalCell(workbook,sheet,height,"");
    	currentRow += height;
	}
	
	public void caseToExcel(Workbook workbook,Sheet sheet)
	{
		for(String key : productMap.keySet()) //for each productCode
        {
        	Map<Integer,List<Product>> map = productMap.get(key);
        	
        	Product firstProduct = map.values().stream()
        		    .filter(list -> list != null && !list.isEmpty())
        		    .map(list -> list.get(0))
        		    .findFirst()
        		    .orElse(null);
        	
        	if(firstProduct.isCombo())
        	{
        		continue;
        	}
        	
        	int count = 0;
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        			int column = ((count % 10) + 2);
        	    	setCellValue(sheet,column , currentRow + (count/10), String.valueOf(product.getQuantity()));
        			count++;
        		}
        	}
        	
        	int height = getHeight(map);
        	
            setBorderAroundProductCell(workbook,sheet,height);
        	setProductCodeCell(workbook,sheet,height,key);
        	String total = formatDouble(getTotalWeightByProduct(map)) + " lbs";
        	if(!firstProduct.isCombo())
        	{
        		total = total + "\n" + getTotalCaseByProduct(map) + " cs";
        	}
        	setTotalCell(workbook,sheet,height,total);

        	currentRow += height;
        }
	}
	
	public void producttoExcel(Workbook workbook,Sheet sheet,String productCode)
	{
    	Map<Integer,List<Product>> map = productMap.get(productCode);
    	
    	Product firstProduct = map.values().stream()
    		    .filter(list -> list != null && !list.isEmpty())
    		    .map(list -> list.get(0))
    		    .findFirst()
    		    .orElse(null);
    	    	
    	int count = 0;
    	for(Integer hour : map.keySet()) //for each hour
    	{
    		List<Product> list = map.get(hour); 
    		for(int i = 0; i < list.size();i++) { //for each product
    			Product product = list.get(i);
    			int column = ((count % 10) + 2);
    	    	setCellValue(sheet,column , currentRow + (count/10), String.valueOf(product.getQuantity()));
    			count++;
    		}
    	}
    	
    	int height = getHeight(map);
    	
        setBorderAroundProductCell(workbook,sheet,height);
    	setProductCodeCell(workbook,sheet,height,productCode);
    	String total = formatDouble(getTotalWeightByProduct(map)) + " lbs";
    	if(!firstProduct.isCombo())
    	{
    		total = total + "\n" + getTotalCaseByProduct(map) + " cs";
    	}
    	setTotalCell(workbook,sheet,height,total);

    	currentRow += height;
	}
	
	public void comboToExcel(Workbook workbook,Sheet sheet)
	{
		for(String key : productMap.keySet()) //for each productCode
        {
        	Map<Integer,List<Product>> map = productMap.get(key);
        	
        	Product firstProduct = map.values().stream()
        		    .filter(list -> list != null && !list.isEmpty())
        		    .map(list -> list.get(0))
        		    .findFirst()
        		    .orElse(null);
        	
        	if(!firstProduct.isCombo())
        	{
        		continue;
        	}
        	
    		int count = 0;
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        			int column = ((count % 10) + 2);
        	    	setCellValue(sheet,column , currentRow + (count/10), String.valueOf(product.getQuantity()));
        			count++;
        		}
        	}
        	
        	int height = getHeight(map);
        	
            setBorderAroundProductCell(workbook,sheet,height);
        	setProductCodeCell(workbook,sheet,height,key);
        	String total = formatDouble(getTotalWeightByProduct(map)) + " lbs";
        	if(!firstProduct.isCombo())
        	{
        		total = total + "\n" + getTotalCaseByProduct(map) + " cs";
        	}
        	setTotalCell(workbook,sheet,height,total);

        	currentRow += height;
        }
	}
	
	public void carcassToExcel(Workbook workbook,Sheet sheet)
	{
		currentRow = 29;
		for(String key : carcassMap.keySet()) //for each productCode
        {
        	Map<Integer,List<Product>> map = carcassMap.get(key);
        	
        	Product firstProduct = map.values().stream()
        		    .filter(list -> list != null && !list.isEmpty())
        		    .map(list -> list.get(0))
        		    .findFirst()
        		    .orElse(null);
        	
    		int count = 0;
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        			int column = ((count % 10) + 2);
        	    	setCellValue(sheet,column , currentRow + (count/10), String.valueOf(product.getQuantity()));
        			count++;
        		}
        	}
        	
        	int height = getHeight(map);
        	
            setBorderAroundProductCell(workbook,sheet,height);
        	setProductCodeCell(workbook,sheet,height,key);
        	String total = formatDouble(getTotalWeightByProduct(map)) + " lbs";
        	if(!firstProduct.isCombo())
        	{
        		total = total + "\n" + getTotalCaseByProduct(map) + " cs";
        	}
        	setTotalCell(workbook,sheet,height,total);

        	currentRow += height;
        }
	}
	
	public void wogToExcel(Workbook workbook,Sheet sheet)
	{
		currentRow = 26;
		
		for(String key : wogMap.keySet()) //for each productCode
        {
        	Map<Integer,List<Product>> map = wogMap.get(key);
        	
        	Product firstProduct = map.values().stream()
        		    .filter(list -> list != null && !list.isEmpty())
        		    .map(list -> list.get(0))
        		    .findFirst()
        		    .orElse(null);
        	
    		int count = 0;
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        			int column = ((count % 10) + 2);
        	    	setCellValue(sheet,column , currentRow + (count/10), String.valueOf(product.getQuantity()));
        			count++;
        		}
        	}
        	
        	String total = formatDouble(getTotalWeightByProduct(map)) + " lbs";
        	if(!firstProduct.isCombo())
        	{
        		total = total + "\n" + getTotalCaseByProduct(map) + " cs";
        	}
        	
	    	setCellValue(sheet,"M" , 26 , String.valueOf(total));

        }
	}
	
	public void legToExcel(Workbook workbook,Sheet sheet)
	{
		currentRow = 27;
		
		for(String key : legMap.keySet()) //for each productCode
        {
        	Map<Integer,List<Product>> map = legMap.get(key);
        	
        	Product firstProduct = map.values().stream()
        		    .filter(list -> list != null && !list.isEmpty())
        		    .map(list -> list.get(0))
        		    .findFirst()
        		    .orElse(null);
        	
    		int count = 0;
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        			int column = ((count % 10) + 2);
        	    	setCellValue(sheet,column , currentRow + (count/10), String.valueOf(product.getQuantity()));
        			count++;
        		}
        	}
        	
        	String total = formatDouble(getTotalWeightByProduct(map)) + " lbs";
        	if(!firstProduct.isCombo())
        	{
        		total = total + "\n" + getTotalCaseByProduct(map) + " cs";
        	}
        	
	    	setCellValue(sheet,"M" , 27 , String.valueOf(total));

        }
	}
	
	public void setTotalCell(Workbook workbook,Sheet sheet,int height,String quantity)
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
	
	public void setBorderAroundProductCell(Workbook workbook,Sheet sheet,int height)
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
	
	public void setProductCodeCell(Workbook workbook,Sheet sheet,int height,String productCode)
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
	
	public int getHeight(Map<Integer,List<Product>> map)
	{
		double amount = 0;
		for(List<Product> list : map.values())
		{
			amount += list.size();
		}
		
		int height = (int) Math.ceil(amount/10);
		
		return height > 2 ? height : 2;
	}
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "M", 2, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Workbook workbook,Sheet sheet)
    {
    	
    }
	
	public double getTotalComboWeight()
	{
		return Break1Weight + Break2Weight + Break3Weight;
	}
	
	public double getTotalWeight()
	{
		return getTotalComboWeight();
	}
	
}