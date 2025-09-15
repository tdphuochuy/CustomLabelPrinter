import okhttp3.*;
import paperwork.Product;
import paperwork.dsi.paperworkDSIGen;
import paperwork.gen.breastGen;
import paperwork.gen.drumGen;
import paperwork.gen.recapGenMarel;
import paperwork.gen.thighGen;
import paperwork.gen.wingGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.net.ssl.*;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import config.Config;

public class test {
	private static int currentRow;

    public static void main(String[] args) throws Exception {
    	int[] times = {20,23};
    	thighGen thighExcel = new thighGen(times);
    	wingGen wingExcel = new wingGen(times);
    	drumGen drumExcel = new drumGen(times);

		Map<String,Product> productMap = new TreeMap<>();
		Element dataTable = getData("","lcuevas");
		extractData(productMap,dataTable);
		
		for(String key: productMap.keySet())
		{
			Product product = productMap.get(key);
			if(product.getType().equals("thigh"))
			{
				thighExcel.addProduct(product);
			} else if(product.getType().equals("wing"))
			{
				wingExcel.addProduct(product);
			} else if(product.getType().equals("drums"))
			{
				drumExcel.addProduct(product);
			} else if(product.getType().equals("carcass"))
			{
				drumExcel.addCarcass(product);
			} else if(product.getType().equals("wog"))
			{
				drumExcel.addWog(product);
			}else if(product.getType().equals("leg 1/4"))
			{
				drumExcel.addLeg(product);
			}
		}
		
		thighExcel.generateExcel();
		wingExcel.generateExcel();
		drumExcel.generateExcel();
		
		recapGenMarel recapGen = new recapGenMarel("Lam", thighExcel, drumExcel, wingExcel,null); 
		recapGen.generateExcel();
    	
    }
    
	public static Element getData(String orderNum,String searchKey) throws IOException
	{

		File input = new File(System.getProperty("user.home") + "\\Desktop\\XMLReport.html");
        Document doc = Jsoup.parse(input, "UTF-8");
		Element bodyElement = doc.body();
		Element inputElement = bodyElement.select("[name=unnamed]").first();
		for(Element table : inputElement.getElementsByTag("table"))
		{
			if(table.html().toLowerCase().contains(searchKey))
			{
				return table;
			}
		}
		
		return null;
	}
	
	public static void extractData(Map<String,Product> map,Element table) throws ParseException, IOException
	{
		InputStream inputStream = paperworkDSIGen.class.getClassLoader().getResourceAsStream("paperwork/product_data.json");
        String inputData = new BufferedReader(new InputStreamReader(inputStream))
                            .lines()
                            .collect(Collectors.joining("\n"));
    	JSONParser jsonParser = new JSONParser();
    	JSONObject productObj = (JSONObject) jsonParser.parse(inputData);
		for(Element tr : table.getElementsByTag("tr"))
		{
			String content = tr.html();
			if(content.toLowerCase().contains("lcuevas"))
			{
				String trackingNum = tr.getElementsByTag("a").get(0).text();
				if(content.contains("#ff0000"))
				{
					map.remove(trackingNum);
					continue;
				}
				Elements td = tr.getElementsByTag("td");
				String itemPack = td.get(2).text() + td.get(3).text();
				System.out.println(itemPack);
				String productCode = ((JSONObject)productObj.get(itemPack)).get("Product").toString();
				String description =  td.get(4).text();
				String lotNumber =  td.get(5).text();
				int hour = Integer.parseInt(lotNumber.substring(lotNumber.length() - 6,lotNumber.length() - 4));
				if(hour == 98)
				{
					hour = getHourby98(trackingNum,getData("","transaction id"));
				}
				if(hour > 35 && hour < 54)
				{
					hour = hour - 30;
				} else if (hour > 29 && hour < 35) {
					hour = hour - 6;
				} else if (hour > 26 && hour < 29)
				{
					hour = 26;
				}
				
				int quantity = (int) Double.parseDouble(td.get(8).text().replace(",", ""));
				double weight = Double.parseDouble(td.get(10).text().replace(",", ""));
				if(productCode.equals("17333"))
				{
					quantity = 2000;
				} else if (productCode.equals("15896") && quantity == 2000)
				{
					quantity = 1950;
					weight = 1950;
				}
				boolean isCombo = (((JSONObject)productObj.get(itemPack)).get("Container Type").toString().toLowerCase()).contains("combo");
				String type = getType(description);
				map.put(trackingNum,new Product(productCode,trackingNum,hour,type,quantity,weight,isCombo));
			}
		}
	}
	
	public static int getHourby98(String tracking,Element table)
	{
		for(Element tr : table.getElementsByTag("tr"))
		{
			if(tr.html().contains(tracking))
			{
				Elements td = tr.getElementsByTag("td");
				String transactionID = td.get(1).text();
				String[] split = transactionID.split("-");
			}
		}
		
		return 0;
	}
	
	public static String getType(String description)
	{
		String type = "";
		if(description.contains("THGH"))
		{
			type = "thigh";
		} else if (description.contains("FRNT 1/2 W/O")) {
			type = "wog";
		}else if (description.contains("LEG 1/4")) {
			type = "leg 1/4";
		} else if (description.contains("WING"))
		{
			type = "wing";
		} else if (description.contains("DRMS")) {
			type = "drums";
		} else if (description.contains("CARC") || description.contains("CKN BACKBONE AND TAILS"))
		{
			type = "carcass";
		} 

		return type;
	}

}
