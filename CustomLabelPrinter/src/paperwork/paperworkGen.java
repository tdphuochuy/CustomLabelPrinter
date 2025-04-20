package paperwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import paperwork.gen.breastGen;
import paperwork.gen.carcassGen;
import paperwork.gen.tenderGen;

public class paperworkGen{
	private String username,password,orderNum;
	private String sessionId = "";
	public paperworkGen(String username,String password,String orderNum)
	{
		this.username = username;
		this.password = password;
		this.orderNum = orderNum;
		//sessionId = getSessionId(); TO-DO uncomment
		
	}
	
	public void start() throws ParseException
	{
		Element dataTable = getData();
		Map<String,Product> map = extractData(dataTable);
		evaluateData(map);
	}
	
	public String getSessionId()
	{
		OkHttpClient client = new OkHttpClient();
		FormBody formBody = new FormBody.Builder()
                .add("user", username)
                .add("pass", password)
                .add("submit1", "Go")
                .add("r", "login")
                .add("f", "n")
                .build();
		
		Request request = new Request.Builder()
                .url("http://whistleclient/cgi-bin/yield/") // Example URL
                .post(formBody)
                .build();
		
		try (
				Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
            	String body = response.body().string();
            	String session = body.substring(body.indexOf("?session=")+9,body.indexOf("&cmp="));
            	return session;
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	public Element getData()
	{
		OkHttpClient client = new OkHttpClient();

		FormBody formBody = new FormBody.Builder()
                .add("fileName", "reports/SingleOrderProductionViewer.txt")
                .add("Order", orderNum)
                .add("submit1", "Go")
                .add("r", "XMLReport")
                .add("f", "n")
                .add("session", sessionId)
                .build();
		
		Request request = new Request.Builder()
                .url("http://whistleclient/cgi-bin/yield/") // Example URL
                .post(formBody)
                .build();
		
		try (
				Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
            	String body = response.body().string();
            	List<String> list = new ArrayList<>();
            	Document doc = Jsoup.parse(body);
            	Element bodyElement = doc.body();
            	Element inputElement = bodyElement.select("[name=unnamed]").first();
            	for(Element table : inputElement.getElementsByTag("table"))
            	{
            		if(table.html().toLowerCase().contains(username.toLowerCase()))
            		{
            			return table;
            		}
            	}
            } else {
            	System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	public Map<String,Product> extractData(Element table) throws ParseException
	{
		Map<String,Product> map = new TreeMap<>();
		InputStream inputStream = paperworkGen.class.getClassLoader().getResourceAsStream("paperwork/product_data.json");
        String inputData = new BufferedReader(new InputStreamReader(inputStream))
                            .lines()
                            .collect(Collectors.joining("\n"));
    	JSONParser jsonParser = new JSONParser();
    	JSONObject productObj = (JSONObject) jsonParser.parse(inputData);
		for(Element tr : table.getElementsByTag("tr"))
		{
			String content = tr.html();
			if(content.toLowerCase().contains(username.toLowerCase()))
			{
				String trackingNum = tr.getElementsByTag("a").get(0).text();
				if(content.contains("#ff0000"))
				{
					map.remove(trackingNum);
					continue;
				}
				Elements td = tr.getElementsByTag("td");
				String itemPack = td.get(2).text() + td.get(3).text();
				String productCode = ((JSONObject)productObj.get(itemPack)).get("Product").toString();
				String description =  td.get(4).text();
				String lotNumber =  td.get(5).text();
				int hour = Integer.parseInt(lotNumber.substring(lotNumber.length() - 6,lotNumber.length() - 4));
				int quantity = (int) Double.parseDouble(td.get(8).text().replace(",", ""));
				double weight = Double.parseDouble(td.get(10).text().replace(",", ""));
				boolean isCombo = (((JSONObject)productObj.get(itemPack)).get("Container Type").toString().toLowerCase()).contains("combo");
				String type = getType(description);
				map.put(trackingNum,new Product(productCode,trackingNum,hour,type,quantity,weight,isCombo));
			}
		}
		
		return map;
	}
	
	public void evaluateData(Map<String,Product> map)
	{
		breastGen breastExcel = new breastGen();
		tenderGen tenderExcel = new tenderGen();
		carcassGen carcassExcel = new carcassGen();
		
		for(String key: map.keySet())
		{
			Product product = map.get(key);
			if(product.getType().equals("breast"))
			{
				breastExcel.addProduct(product);
			} else if (product.getType().equals("tender"))
			{
				tenderExcel.addProduct(product);
			} else if (product.getType().equals("carcass"))
			{
				carcassExcel.addProduct(product);
			} else if (product.getType().equals("keelbone"))
			{
				tenderExcel.addKeelBone(product);
			} else if (product.getType().equals("kneebone"))
			{
				tenderExcel.addKneeBone(product);
			} else if (product.getType().equals("skin"))
			{
				tenderExcel.addSkin(product);
			}
		}
		
		breastExcel.generateExcel();
		tenderExcel.generateExcel();
		carcassExcel.generateExcel();
	}
	
	public String getType(String description)
	{
		String type = "";
		if(description.contains("TENDER"))
		{
			type = "tender";
		} else if (description.contains("CARC"))
		{
			type = "carcass";
		} else if (description.contains("TRIMGS")) {
			type = "trim";
		} else if (description.contains("BRST")) {
			type = "breast";
		} else if (description.contains("KNEE SOFT BONE")) {
			type = "kneebone";
		} else if (description.contains("KEELBONES")) {
			type = "keelbone";
		} else if (description.contains("SKN ")) {
			type = "skin";
		}
		
		

		return type;
	}
}