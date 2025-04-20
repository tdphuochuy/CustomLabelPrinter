import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import paperwork.Product;
import paperwork.paperworkGen;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class test {

    public static void main(String[] args) throws IOException, ParseException {
    	File input = new File("whistle_order2.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        
        Element bodyElement = doc.body();
    	Element inputElement = bodyElement.select("[name=unnamed]").first();
    	for(Element table : inputElement.getElementsByTag("table"))
    	{
    		if(table.html().toLowerCase().contains("pmambo"))
    		{
    			paperworkGen ppw = new paperworkGen("pmambo","4292","test");
    			Map<String,Product> map = ppw.extractData(table);
    			ppw.evaluateData(map);
    			break;
    		}
    	}
    }
}
