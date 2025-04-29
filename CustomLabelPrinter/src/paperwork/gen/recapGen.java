package paperwork.gen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import paperwork.Product;

public class recapGen extends excelGen{
	private String name;
	private breastGen breast;
	private tenderGen tender;
	private carcassGen carcass;
	private List<Integer> condemnList;
	private List<Double> issuedList;
	public recapGen(String name,breastGen breast,tenderGen tender,carcassGen carcass,List<Integer> condemnList,List<Double> issuedList)
	{
		this.name = name;
		this.breast = breast;
		this.tender = tender;
		this.carcass = carcass;
		this.condemnList = condemnList;
		this.issuedList = issuedList;
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
		generateRecap1();
		generateRecap2();
	}
	
	public void generateRecap1()
	{
		try (FileInputStream fis = new FileInputStream(filePath);
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(3); 

	            setDate(sheet);
	            //clear(workbook,sheet);
	            setName(sheet);
	            
	            //Set breast values
	        	setCellValue(sheet, "C", 15, formatDouble(breast.getBreak1Weight()));
	        	setCellValue(sheet, "E", 15, formatDouble(breast.getBreak2Weight()));
	        	setCellValue(sheet, "F", 15, formatDouble(breast.getBreak3Weight()));
	        	setCellValue(sheet, "H", 15, formatDouble(breast.getTotalComboWeight()));

	        	setCellValue(sheet, "C", 17, formatDouble(breast.getBreak1CaseWeight()));
	        	setCellValue(sheet, "E", 17, formatDouble(breast.getBreak2CaseWeight()));
	        	setCellValue(sheet, "F", 17, formatDouble(breast.getBreak3CaseWeight()));
	        	setCellValue(sheet, "H", 17, formatDouble(breast.getTotalCaseWeight()));
	        	setCellValue(sheet, "H", 19, formatDouble(breast.getTotalWeight()));

	        	//set trim values
	        	setCellValue(sheet, "C", 21, formatDouble(tender.getBreak1TrimWeight()));
	        	setCellValue(sheet, "E", 21, formatDouble(tender.getBreak2TrimWeight()));
	        	setCellValue(sheet, "F", 21, formatDouble(tender.getBreak3TrimWeight()));
	        	setCellValue(sheet, "H", 21, formatDouble(tender.getTotalTrimWeight()));
	        	
	        	//set tender values
	        	setCellValue(sheet, "C", 23, formatDouble(tender.getBreak1Weight()));
	        	setCellValue(sheet, "E", 23, formatDouble(tender.getBreak2Weight()));
	        	setCellValue(sheet, "F", 23, formatDouble(tender.getBreak3Weight()));
	        	setCellValue(sheet, "H", 23, formatDouble(tender.getTotalWeight()));
	        	
	        	//set skin values
	        	setCellValue(sheet, "C", 25, formatDouble(tender.getBreak1SkinWeight()));
	        	setCellValue(sheet, "E", 25, formatDouble(tender.getBreak2SkinWeight()));
	        	setCellValue(sheet, "F", 25, formatDouble(tender.getBreak3SkinWeight()));
	        	setCellValue(sheet, "H", 25, formatDouble(tender.getTotalSkinWeight()));
	        	
	        	//set carcass values
	        	setCellValue(sheet, "C", 27, formatDouble(carcass.getBreak1Weight()));
	        	setCellValue(sheet, "E", 27, formatDouble(carcass.getBreak2Weight()));
	        	setCellValue(sheet, "F", 27, formatDouble(carcass.getBreak3Weight()));
	        	setCellValue(sheet, "H", 27, formatDouble(carcass.getTotalWeight()));
	        	
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
	
	public void generateRecap2()
	{
		try (FileInputStream fis = new FileInputStream(filePath);
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(4); 
	        	
	            // Save changes
	            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
	                workbook.write(fos);
	            }
	            
	        	setCellValue(sheet, "B", 6, formatDouble(breast.getTotalWeight()) + " lbs");
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
	        		if(!weightString.endsWith("40"))
	        		{
	        			setCellValue(sheet, currentColumn, currentRow, weightString + " lbs*");
	        		} else {
	        			setCellValue(sheet, currentColumn, currentRow, weightString + " lbs");
	        		}
		        	currentRow++;
	        	}
	        	
	        	currentColumn = "G";
	        	currentRow = 30;
	        	for(int i = 1; i <= condemnList.size();i++)
	        	{
	        		int condemnWeight = condemnList.get(i - 1);
	        		if(currentRow > 40)
	        		{
	        			currentColumn = "I";
	        			currentRow = 30;
	        		}
	        		setCellValue(sheet, currentColumn, currentRow, i + ". " + String.valueOf(condemnWeight) + " lbs");
		        	currentRow++;
	        	}
        		setCellValue(sheet, "H", 41, String.valueOf(getCondemnWeight()) + " lbs");

        		setCellValue(sheet, "G", 5, "Total rework issued: " + formatDouble(getIssuedTotal()) + " lbs");

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
	
	public int getCondemnWeight()
	{
		int weight = 0;
		for(Integer condemnWeight : condemnList)
    	{
			weight += condemnWeight;
    	}
		
		return weight;
	}
	
	public double getIssuedTotal()
	{
		double weight = 0;
		for(Double issuedWeight : issuedList)
    	{
			weight += issuedWeight;
    	}
		
		return weight;
	}
	
}