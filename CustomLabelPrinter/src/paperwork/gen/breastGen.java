package paperwork.gen;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class breastGen extends excelGen{
	public breastGen()
	{
		
	}
	
	public void generateExcel()
	{
		
	}
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "M", 2, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Sheet sheet)
    {
    	
    }
    
    public String hourToLetter(int number) {
    	// Convert number to letter using offset
        int ascii = number + 52;

        // Ensure result is a valid uppercase letter
        if (ascii < 'A' || ascii > 'Z') {
            throw new IllegalArgumentException("Resulting letter is out of A-Z range for input: " + number);
        }

        return String.valueOf((char) ascii);
    }
}