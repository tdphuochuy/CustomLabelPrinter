package paperwork.gen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import config.Config;
import paperwork.Product;

public class recap1stGen extends excelGen{
	private String name;
	private breast1stGen breast;
	private tender1stGen tender;
	private carcass1stGen carcass;
	private List<Integer> bloodcondemnList,greencondemnList;
	private List<Double> issuedList1;
	private List<Double> issuedList2;
	private String tenderCondemnTotal;
	private String dsiEOSCondemnTotal;
	public recap1stGen(String name,breast1stGen breast,tender1stGen tender,carcass1stGen carcass,List<Integer> bloodcondemnList,List<Integer> greencondemnList,List<Double> issuedList1,List<Double> issuedList2,String tenderCondemnTotal,String dsiEOSCondemnTotal)
	{
		this.name = name;
		this.breast = breast;
		this.tender = tender;
		this.carcass = carcass;
		this.bloodcondemnList = bloodcondemnList;
		this.greencondemnList = greencondemnList;
		this.issuedList1 = issuedList1;
		this.issuedList2 = issuedList2;
		this.tenderCondemnTotal = tenderCondemnTotal;
		this.dsiEOSCondemnTotal = dsiEOSCondemnTotal;
	}

	@Override
	public void setDate(Sheet sheet) {
    	setCellValue(sheet, "B", 8, getDate("MM/dd/yyyy"));
	}
	
	public void setName(Sheet sheet) {
    	setCellValue(sheet, "B", 31, name);
	}

	@Override
	public void clear(Workbook workbook, Sheet sheet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateExcel() {
		// TODO Auto-generated method stub
		generateRecap();
	}
	
	public void generateRecap()
	{
		try (FileInputStream fis = new FileInputStream(Config.ppwExcelPath);
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(4); 
	        	
	            // Save changes
	            try (FileOutputStream fos = new FileOutputStream(Config.ppwExcelPath)) {
	                workbook.write(fos);
	            }
	            
	        	setCellValue(sheet, "B", 6, formatDouble(breast.getTotalWeight()) + " lbs");
	        	//setCellValue(sheet, "C", 8, formatDouble(breast.getTotalWeightwRW()) + " lbs");
	        	setCellValue(sheet, "B", 11, formatDouble(tender.getTotalWeight()) + " lbs");
	        	setCellValue(sheet, "B", 16, formatDouble(tender.getKeelBoneWeight()) + " lbs");
	        	setCellValue(sheet, "B", 21, formatDouble(tender.getKneeBoneWeight()) + " lbs");
	        	setCellValue(sheet, "B", 26, formatDouble(tender.getTotalTrimWeight()) + " lbs");
	        	setCellValue(sheet, "B", 31, formatDouble(carcass.getTotalWeight()) + " lbs");
	        	
	        	String currentColumn = "B";
	        	int currentRow = 36;
	        	for(Product product: breast.getReworkList())
	        	{
	        		if(currentRow > 43)
	        		{
	        			currentColumn = "C";
	        			currentRow = 36;
	        		}
	        		String weightString = formatDouble(product.getWeight());
	        		if(!weightString.endsWith("40") && product.getWeight() >= 200)
	        		{
	        			setCellValue(sheet, currentColumn, currentRow, weightString + " lbs*");
	        		} else {
	        			setCellValue(sheet, currentColumn, currentRow, weightString + " lbs");
	        		}
		        	currentRow++;
	        	}
	        	
	        	currentColumn = "G";
	        	currentRow = 25;
	        	for(int i = 1; i <= greencondemnList.size();i++)
	        	{
	        		int condemnWeight = greencondemnList.get(i - 1);
	        		setCellValue(sheet, currentColumn, currentRow, i + ". " + String.valueOf(condemnWeight) + " lbs");
		        	currentRow++;
	        	}
	        	
	        	currentColumn = "I";
	        	currentRow = 25;
	        	for(int i = 1; i <= bloodcondemnList.size();i++)
	        	{
	        		int condemnWeight = bloodcondemnList.get(i - 1);
	        		setCellValue(sheet, currentColumn, currentRow, i + ". " + String.valueOf(condemnWeight) + " lbs");
		        	currentRow++;
	        	}
	        	
        		setCellValue(sheet, "H", 35, String.valueOf(getCondemnWeight()) + " lbs");
        		
        		if(issuedList1.size() > 0)
        		{
            		setCellValue(sheet, "G", 6, "1st: " + formatDouble(getIssuedTotal(issuedList1)) + " lbs");
        		}
        		
        		if(issuedList2.size() > 0)
        		{
            		setCellValue(sheet, "G", 7, "2nd: " + formatDouble(getIssuedTotal(issuedList2)) + " lbs");
        		}
        		
        		
        		setCellValue(sheet, "G", 17, tenderCondemnTotal + " lbs");
        		setCellValue(sheet, "G", 40, dsiEOSCondemnTotal + " lbs");

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
	
	public int getCondemnWeight()
	{
		int weight = 0;
		for(Integer condemnWeight : bloodcondemnList)
    	{
			weight += condemnWeight;
    	}
		for(Integer condemnWeight : greencondemnList)
    	{
			weight += condemnWeight;
    	}
		
		return weight;
	}
	
	public double getIssuedTotal(List<Double> issuedList)
	{
		double weight = 0;
		for(Double issuedWeight : issuedList)
    	{
			weight += issuedWeight;
    	}
		
		return weight;
	}
	
}