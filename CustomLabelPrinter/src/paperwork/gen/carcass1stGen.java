package paperwork.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import config.Config;
import paperwork.Product;

public class carcass1stGen extends excelGen{
	private int comboCount;
	public carcass1stGen(int[] times)
	{
		this.times = times;
		comboCount = 0;
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
		
		if(product.isCombo())
		{
			comboCount = comboCount + 1;
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
	
	public void generateExcel()
	{
        try (FileInputStream fis = new FileInputStream(Config.ppwExcelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(2); // First sheet

            setDate(sheet);
            //clear(workbook,sheet);
            
            for(String key : productMap.keySet()) //for each productCode
            {
            	Map<Integer,List<Product>> map = productMap.get(key);
            	for(Integer hour : map.keySet()) //for each hour
            	{
            		String columnLetter = hourToLetter1st(hour);
            		List<Product> list = map.get(hour); 
            		for(int i = 0; i < list.size();i++) { //for each product
            			Product product = list.get(i);
            			if(key.equals("21102"))
            			{
            				setCellValue(sheet, columnLetter, i + 18, String.valueOf(product.getQuantity()));
            			} else {
            				setCellValue(sheet, columnLetter, i + 6, String.valueOf(product.getQuantity()));
            			}
            		}
            	}
            }
            
            //set total weight 21102
	    	setCellValue(sheet, "K", 18, formatDouble(getTotalWeightByProduct(productMap.get("21102"))) + " lbs");

            //set total weight boxes
	    	if(productMap.keySet().size() > 1)
	    	{
	        	String total = formatDouble(getTotalWeightByProduct(productMap.get("10468"))) + " lbs";
        		total = total + "\n\n" + getTotalCaseByProduct(productMap.get("10468")) + " cs";

		    	setCellValue(sheet, "K", 6, total);
	    	}
	    	
	    	//set Total Combo
	    	setCellValue(sheet, "K", 28, String.valueOf(getComboCount()));
	    	
	    	//set Total LBS
	    	setCellValue(sheet, "K", 29, formatDouble(getTotalWeight()));
	    	
            // Save changes
            try (FileOutputStream fos = new FileOutputStream(Config.ppwExcelPath)) {
                workbook.write(fos);
            }

            System.out.println("Cell updated successfully!");
            
            //File file = new File("recap_output/carcass.xlsx");
            //exportPDF(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "B", 3, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Workbook workbook,Sheet sheet)
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
    
    public int getComboCount()
    {
    	return comboCount;
    }
    
}