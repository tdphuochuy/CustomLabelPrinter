package paperwork.gen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import config.Config;

public class recapGenMarel extends excelGen{
	private String name;
	private thighGen thigh;
	private drumGen drum;
	private wingGen wing;
	private Map<String,List<List<Integer>>> condemnMap;
	public recapGenMarel(String name, thighGen thigh, drumGen drum, wingGen wing, Map<String,List<List<Integer>>> condemnMap) {
		this.name = name;
		this.thigh = thigh;
		this.drum = drum;
		this.wing = wing;
		this.condemnMap = condemnMap;
	}
	
	@Override
	public void setDate(Sheet sheet) {
    	setCellValue(sheet, "E", 6, getDate("MM/dd/yyyy"));
	}
	
	public void setName(Sheet sheet) {
    	setCellValue(sheet, "B", 6, name);
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
		try (FileInputStream fis = new FileInputStream(Config.ppwExcelPath);
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(3); 

	            setDate(sheet);
	            //clear(workbook,sheet);
	            setName(sheet);
	            
	            //set WOGS
	        	setCellValue(sheet, "B", 10, formatDouble(drum.Break1WogWeight));
	        	setCellValue(sheet, "C", 10, formatDouble(drum.Break2WogWeight));
	        	setCellValue(sheet, "D", 10, formatDouble(drum.Break3WogWeight));
	        	setCellValue(sheet, "E", 10, formatDouble(drum.getTotalWog()));
	        	
	        	//set Legs
	        	setCellValue(sheet, "B", 11, formatDouble(drum.Break1LegWeight));
	        	setCellValue(sheet, "C", 11, formatDouble(drum.Break2LegWeight));
	        	setCellValue(sheet, "D", 11, formatDouble(drum.Break3LegWeight));
	        	setCellValue(sheet, "E", 11, formatDouble(drum.getTotalLeg()));
	        	
	        	//set Carcass
	        	setCellValue(sheet, "B", 12, formatDouble(drum.Break1CarcassWeight));
	        	setCellValue(sheet, "C", 12, formatDouble(drum.Break2CarcassWeight));
	        	setCellValue(sheet, "D", 12, formatDouble(drum.Break3CarcassWeight));
	        	setCellValue(sheet, "E", 12, formatDouble(drum.getTotalCarcass()));
	        	
	        	//set Thighs
	        	setCellValue(sheet, "B", 20, formatDouble(thigh.getBreak1Weight()));
	        	setCellValue(sheet, "C", 20, formatDouble(thigh.getBreak2Weight()));
	        	setCellValue(sheet, "D", 20, formatDouble(thigh.getBreak3Weight()));
	        	setCellValue(sheet, "E", 20, formatDouble(thigh.getTotalWeight()));
	        	
	        	//set Wings
	        	setCellValue(sheet, "B", 21, formatDouble(wing.getBreak1Weight()));
	        	setCellValue(sheet, "C", 21, formatDouble(wing.getBreak2Weight()));
	        	setCellValue(sheet, "D", 21, formatDouble(wing.getBreak3Weight()));
	        	setCellValue(sheet, "E", 21, formatDouble(wing.getTotalWeight()));
	        	
	        	//set Drums
	        	setCellValue(sheet, "B", 22, formatDouble(drum.getBreak1Weight()));
	        	setCellValue(sheet, "C", 22, formatDouble(drum.getBreak2Weight()));
	        	setCellValue(sheet, "D", 22, formatDouble(drum.getBreak3Weight()));
	        	setCellValue(sheet, "E", 22, formatDouble(drum.getTotalWeight()));
	        	
	        	//wing tips
	        	setCellValue(sheet, "B", 17, String.valueOf(condemnMap.get("wingtips").get(0)));
	        	setCellValue(sheet, "C", 17, String.valueOf(condemnMap.get("wingtips").get(1)));
	        	setCellValue(sheet, "D", 17, String.valueOf(condemnMap.get("wingtips").get(2)));
	        	setCellValue(sheet, "E", 17, String.valueOf(getCondemnWeightTotal(condemnMap.get("wingtips"))));

	        	//wings
	        	setCellValue(sheet, "B", 16, String.valueOf(condemnMap.get("wings").get(0)));
	        	setCellValue(sheet, "C", 16, String.valueOf(condemnMap.get("wings").get(1)));
	        	setCellValue(sheet, "D", 16, String.valueOf(condemnMap.get("wings").get(2)));
	        	setCellValue(sheet, "E", 16, String.valueOf(getCondemnWeightTotal(condemnMap.get("wings"))));
	        	
	        	//lollipop
	        	setCellValue(sheet, "B", 15, String.valueOf(condemnMap.get("lollipop").get(0)));
	        	setCellValue(sheet, "C", 15, String.valueOf(condemnMap.get("lollipop").get(1)));
	        	setCellValue(sheet, "D", 15, String.valueOf(condemnMap.get("lollipop").get(2)));
	        	setCellValue(sheet, "E", 15, String.valueOf(getCondemnWeightTotal(condemnMap.get("lollipop"))));
	        	
	        	//miscut
	        	setCellValue(sheet, "B", 14, String.valueOf(condemnMap.get("miscut").get(0)));
	        	setCellValue(sheet, "C", 14, String.valueOf(condemnMap.get("miscut").get(1)));
	        	setCellValue(sheet, "D", 14, String.valueOf(condemnMap.get("miscut").get(2)));
	        	setCellValue(sheet, "E", 14, String.valueOf(getCondemnWeightTotal(condemnMap.get("miscut"))));
	        	
	            // Save changes
	            try (FileOutputStream fos = new FileOutputStream(Config.ppwExcelPath)) {
	                workbook.write(fos);
	            }

	            System.out.println("Cell updated successfully!");
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	public void generateRecap2()
	{
		
	}
	
	public int getCondemnWeight(List<Integer> list)
	{
		int weight = 0;
		
		for(int condemn: list)
		{
			weight += condemn;
		}
		
		return weight;
	}
	
	public int getCondemnWeightTotal(List<List<Integer>> list)
	{
		int weight = 0;
		
		for(List<Integer> innerList: list)
		{
			for(int condemn: innerList)
			{
				weight += condemn;
			}
		}
		
		return weight;
	}
}