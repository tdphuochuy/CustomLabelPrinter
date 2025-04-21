package paperwork.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import paperwork.Product;

public class carcassGen extends excelGen{
	public carcassGen(int[] times)
	{
		this.times = times;
	}
	
	public void generateExcel()
	{
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(2); // First sheet

            setDate(sheet);
            //clear(workbook,sheet);
            
            for(String key : productMap.keySet()) //for each productCode
            {
            	Map<Integer,List<Product>> map = productMap.get(key);
            	for(Integer hour : map.keySet()) //for each hour
            	{
            		String columnLetter = hourToLetter(hour);
            		List<Product> list = map.get(hour); 
            		for(int i = 0; i < list.size();i++) { //for each product
            			Product product = list.get(i);
            	    	setCellValue(sheet, columnLetter, i + 5, String.valueOf(product.getQuantity()));
            		}
            	}
            }
            
            //set total weight
	    	setCellValue(sheet, "O", 5, formatDouble(getTotalWeightByProduct(productMap.get("22486"))) + " lbs");

            
            // Save changes
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
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
    	setCellValue(sheet, "N", 2, getDate("MM/dd/yyyy"));
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
}