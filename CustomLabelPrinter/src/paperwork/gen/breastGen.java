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

import paperwork.Product;
import paperwork.dsi.paperworkGen;

public class breastGen extends excelGen{
	List<Product> reworkList = new ArrayList<>();
	private int currentRow;
	public double Break1CaseWeight = 0;
    public double Break2CaseWeight = 0;
    public double Break3CaseWeight = 0;
	public breastGen(int[] times)
	{
		currentRow = 5;
		this.times = times;
	}
	
	public void addProduct(Product product)
	{
		String productCode = product.getCode();
		if(productCode.equals("17261"))
		{
			reworkList.add(product);
			return;
		}
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
			if(product.isCombo())
			{
				Break1Weight += product.getWeight();
			} else {
				Break1CaseWeight += product.getWeight();
			}
		} else if (hour > times[0] && hour <= times[1])
		{
			if(product.isCombo())
			{
				Break2Weight += product.getWeight();
			} else {
				Break2CaseWeight += product.getWeight();
			}
		} else {
			if(product.isCombo())
			{
				Break3Weight += product.getWeight();
			} else {
				Break3CaseWeight += product.getWeight();
			}
		}
		
		productMap.get(productCode).get(hour).add(product);
	}
	
	public void generateExcel()
	{		
		try (InputStream inputStream = paperworkGen.class.getClassLoader().getResourceAsStream("paperwork/recap.xlsx");
			Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // First sheet

            setDate(sheet);
            //clear(workbook,sheet);
            caseToExcel(workbook,sheet);
            emptyBoxToExcel(workbook,sheet,4);
            comboToExcel(workbook,sheet);
            emptyBoxToExcel(workbook,sheet,5);

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
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
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "N", 2, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Workbook workbook,Sheet sheet)
    {
    	
    }
    
	public double getBreak1CaseWeight() {
		return Break1CaseWeight;
	}

	public double getBreak2CaseWeight() {
		return Break2CaseWeight;
	}

	public double getBreak3CaseWeight() {
		return Break3CaseWeight;
	}
	
	public double getTotalComboWeight()
	{
		return Break1Weight + Break2Weight + Break3Weight;
	}
	
	public double getTotalCaseWeight()
	{
		return Break1CaseWeight + Break2CaseWeight + Break3CaseWeight;
	}
	
	public int getTotalCase()
	{
		return (int) getTotalCaseWeight() / 40;
	}
	
	public double getTotalWeight()
	{
		return getTotalComboWeight() + getTotalCaseWeight();
	}
	
	public double getTotalWeightwRW()
	{
		return getTotalWeight() + getReworkWeight();
	}
	
	public List<Product> getReworkList()
	{
		return reworkList;
	}
	
	public double getReworkWeight()
	{
		double weight = 0;
		for(Product product: reworkList)
		{
			weight += product.getWeight();
		}
		
		return weight;
	}
	
}