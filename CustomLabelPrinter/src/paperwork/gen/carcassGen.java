package paperwork.gen;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class carcassGen extends excelGen{
	public carcassGen()
	{
		
	}
	
    public void setDate(Sheet sheet)
    {
    	setCellValue(sheet, "N", 2, getDate("MM/dd/yyyy"));
    }
    
    public void clear(Sheet sheet)
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