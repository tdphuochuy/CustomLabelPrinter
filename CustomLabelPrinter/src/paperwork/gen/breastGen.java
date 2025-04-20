package paperwork.gen;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class breastGen extends excelGen{
	public breastGen()
	{
		
	}
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "M", 2, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Sheet sheet)
    {
    	
    }
}