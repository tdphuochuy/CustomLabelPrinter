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
	private String bloodyCondemnTotal;
	public recap1stGen(String name,breast1stGen breast,tender1stGen tender,carcass1stGen carcass,List<Integer> bloodcondemnList,List<Integer> greencondemnList,List<Double> issuedList1,List<Double> issuedList2,String tenderCondemnTotal,String dsiEOSCondemnTotal,String bloodyCondemnTotal)
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
		this.bloodyCondemnTotal = bloodyCondemnTotal;
	}

	@Override
	public void setDate(Sheet sheet) {
    	setCellValue(sheet, "H", 3, getDate("MM/dd/yyyy"));
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

	            Sheet sheet = workbook.getSheetAt(3); 

	            setDate(sheet);
	            //clear(workbook,sheet);
	            
	            //Set breast values
	        	setCellValue(sheet, "C", 4, formatDouble(breast.getTotalWeight()));
	        	
	        	//Set Defat LBS
	        	setCellValue(sheet, "D", 7, formatDouble(breast.getDefatWeight()));
	        	
	        	//set Defat Cases
	        	setCellValue(sheet, "D", 8, formatDouble(breast.getDefatTotalCase()));

	        	//set trim values
	        	setCellValue(sheet, "C", 10, formatDouble(tender.getTotalTrimWeight()));
	        	
	        	//set tender values
	        	setCellValue(sheet, "C", 14, formatDouble(tender.getTotalWeight()));
	        	
	        	//set carcass values
	        	setCellValue(sheet, "C", 18, formatDouble(carcass.getTotalWeight()));
	        	
	        	//set skin values
	        	setCellValue(sheet, "C", 22, formatDouble(tender.getTotalSkinWeight()));
	        	
	        	//set Bloody Condemn
	        	setCellValue(sheet, "D", 26, bloodyCondemnTotal);
	        	
	        	//set Rework weight
	        	setCellValue(sheet, "C", 30, formatDouble(breast.getReworkWeight()));

	        	
	        	//set green Tenders
	        	setCellValue(sheet, "C", 42, tenderCondemnTotal);
	        	
	        	//set keel bones
	        	setCellValue(sheet, "C", 46, formatDouble(tender.getKeelBoneWeight()));

	        	
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