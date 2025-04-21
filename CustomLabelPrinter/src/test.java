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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class test {

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
    	File input = new File("whistle_order2.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        
        Element bodyElement = doc.body();
    	Element inputElement = bodyElement.select("[name=unnamed]").first();
    	for(Element table : inputElement.getElementsByTag("table"))
    	{
    		if(table.html().toLowerCase().contains("pmambo"))
    		{
    			Map<String,Product> map = new TreeMap<>();
    			int[] times = {20,23};
    			List<Integer> comdemnlist = new ArrayList<>();
    			comdemnlist.add(100);
    			comdemnlist.add(400);
    			comdemnlist.add(200);
    			comdemnlist.add(100);
    			paperworkGen ppw = new paperworkGen("pmambo","4292","test","","Huy",times,comdemnlist);
    			ppw.extractData(map,table);
    			ppw.evaluateData(map);
    			break;
    		}
    	}
    }
}
