package paperwork.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
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

public class tenderGen extends excelGen{
	public Map<String,Map<Integer,List<Product>>> trimMap = new TreeMap<>();
	public Map<String,Map<Integer,List<Product>>> kneeboneMap = new TreeMap<>();
	public Map<String,Map<Integer,List<Product>>> keelboneMap = new TreeMap<>();
	public Map<String,Map<Integer,List<Product>>> skinMap = new TreeMap<>();
	public double Break1TrimWeight = 0;
    public double Break2TrimWeight = 0;
    public double Break3TrimWeight = 0;

	public double Break1SkinWeight = 0;
    public double Break2SkinWeight = 0;
    public double Break3SkinWeight = 0;
    
	private int currentRow;
	public tenderGen(int[] times)
	{
		currentRow = 5;
		this.times = times;
	}
	
	public void generateExcel()
	{
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(1); // First sheet

            setDate(sheet);
            //clear(workbook,sheet);
            setDate(sheet);
            clear(workbook,sheet);
            caseToExcel(workbook,sheet);
            comboToExcel(workbook,sheet);
            emptyBoxToExcel(workbook,sheet,4);
            emptyBoxToExcel(workbook,sheet,5);
            otherToExcel(workbook,sheet,skinMap);
            otherToExcel(workbook,sheet,keelboneMap);
            otherToExcel(workbook,sheet,trimMap);
            otherToExcel(workbook,sheet,kneeboneMap);
            emptyBoxToExcel(workbook,sheet,5);

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }

            System.out.println("Cell updated successfully!");
            
            //File file = new File("recap_output/tender.xlsx");
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
        	
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		String columnLetter = hourToLetter(hour);
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        	    	setCellValue(sheet, columnLetter, currentRow + i, String.valueOf(product.getQuantity()));
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
        	
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		String columnLetter = hourToLetter(hour);
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        	    	setCellValue(sheet, columnLetter, currentRow + i, String.valueOf(product.getQuantity()));
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
	
	public void otherToExcel(Workbook workbook,Sheet sheet,Map<String,Map<Integer,List<Product>>> productMap)
	{
		for(String key : productMap.keySet()) //for each productCode
        {
        	Map<Integer,List<Product>> map = productMap.get(key);
        	
        	Product firstProduct = map.values().stream()
        		    .filter(list -> list != null && !list.isEmpty())
        		    .map(list -> list.get(0))
        		    .findFirst()
        		    .orElse(null);
        	
        	for(Integer hour : map.keySet()) //for each hour
        	{
        		String columnLetter = hourToLetter(hour);
        		List<Product> list = map.get(hour); 
        		for(int i = 0; i < list.size();i++) { //for each product
        			Product product = list.get(i);
        	    	setCellValue(sheet, columnLetter, currentRow + i, String.valueOf(product.getQuantity()));
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
	
	public void setTotalCell(Workbook workbook,Sheet sheet,int height,String quantity)
	{
		int startRow = currentRow -1 ;
        int endRow = currentRow + height - 2;
   	
   	 	CellRangeAddress mergedRegion = new CellRangeAddress(startRow, endRow, 14, 15);
        sheet.addMergedRegion(mergedRegion);
        
        // Create font: size 28, underline
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 22);

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

            for (int colIndex = 14; colIndex <= 15; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) cell = row.createCell(colIndex);
                cell.setCellStyle(style);
            }
        }
        
        
        Cell topLeftCell = sheet.getRow(startRow).getCell(14);
        topLeftCell.setCellValue(quantity);
	}
	
	public void setBorderAroundProductCell(Workbook workbook,Sheet sheet,int height)
	{
		int startRow = currentRow -1 ;
		int endRow = currentRow + height - 2;;
        int startCol = 2;
        int endCol = 15;
        
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 20);
        
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
   	
   	 CellRangeAddress mergedRegion = new CellRangeAddress(startRow, endRow, 2, 3);
        sheet.addMergedRegion(mergedRegion);
        
     // Create font: size 28, underline
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 28);
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

            for (int colIndex = 2; colIndex <= 3; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) cell = row.createCell(colIndex);
                cell.setCellStyle(style);
            }
        }
        
        
        Cell topLeftCell = sheet.getRow(startRow).getCell(2);
        topLeftCell.setCellValue(productCode);
	}
	
	public int getHeight(Map<Integer,List<Product>> map)
	{
		int height = 2;
		for(List<Product> list : map.values())
		{
			if(list.size() > height)
			{
				height = list.size();
			}
		}
		
		return height;
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
		
		if(hour <= times[0])
		{
			Break1TrimWeight += product.getWeight();
		} else if (hour > times[0] && hour <= times[1])
		{
			Break2TrimWeight += product.getWeight();
		} else {
			Break3TrimWeight += product.getWeight();
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
		
		if(hour <= times[0])
		{
			Break1SkinWeight += product.getWeight();
		} else if (hour > times[0] && hour <= times[1])
		{
			Break2SkinWeight += product.getWeight();
		} else {
			Break3SkinWeight += product.getWeight();
		}
		
		skinMap.get(productCode).get(hour).add(product);
	}
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "N", 2, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Workbook workbook,Sheet sheet)
    {
        CellStyle blankStyle = workbook.createCellStyle();

    	// E5 = row 4, col 4; N15 = row 14, col 13
        for (int rowIndex = 4; rowIndex <= 46; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            for (int colIndex = 2; colIndex <= 15; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    cell.setBlank(); // Clears the value
                    //cell.setCellStyle(blankStyle);
                    // Alternatively, you can do: row.removeCell(cell);
                }
            }
        }
    }
    
	public double getBreak1TrimWeight() {
		return Break1TrimWeight;
	}

	public double getBreak2TrimWeight() {
		return Break2TrimWeight;
	}

	public double getBreak3TrimWeight() {
		return Break3TrimWeight;
	}
	
	public double getTotalTrimWeight() {
		return getBreak1TrimWeight() + getBreak2TrimWeight() + getBreak3TrimWeight();
	}
	
	
	public double getBreak1SkinWeight() {
		return Break1SkinWeight;
	}

	public double getBreak2SkinWeight() {
		return Break2SkinWeight;
	}

	public double getBreak3SkinWeight() {
		return Break3SkinWeight;
	}
	
	public double getKeelBoneWeight()
	{
		double totalWeight = 0;
		for(String key: keelboneMap.keySet())
		{
			totalWeight += getTotalWeightByProduct(keelboneMap.get(key));
		}
		
		return totalWeight;
	}
	
	public double getKneeBoneWeight()
	{
		double totalWeight = 0;
		for(String key: kneeboneMap.keySet())
		{
			totalWeight += getTotalWeightByProduct(kneeboneMap.get(key));
		}
		
		return totalWeight;
	}
	
	public double getTotalSkinWeight() {
		return getBreak1SkinWeight() + getBreak2SkinWeight() + getBreak3SkinWeight();
	}

}