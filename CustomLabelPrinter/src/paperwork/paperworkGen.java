package paperwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
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
import paperwork.gen.recapGen;
import paperwork.gen.tenderGen;

public class paperworkGen{
	private String username,password,orderNum,reworkOrderNum,name;
	private int[] times;
	private List<Integer> condemnList;
	private String sessionId = "";
	public paperworkGen(String username,String password,String orderNum,String reworkOrderNum,String name,int[] times,List<Integer> condemnList)
	{
		this.username = username;
		this.password = password;
		this.orderNum = orderNum;
		this.reworkOrderNum = reworkOrderNum;
		this.name = name;
		this.times = times;
		this.condemnList = condemnList;
		//sessionId = getSessionId(); TO-DO uncomment
		
	}
	
	public void start() throws ParseException, InterruptedException
	{
		deleteOldRecap();
		Map<String,Product> productMap = new TreeMap<>();

		Element dataTable = getData(orderNum);
		extractData(productMap,dataTable);
		if(reworkOrderNum.length() > 0)
		{
			Element dataTableRework = getData(reworkOrderNum);
			extractData(productMap,dataTableRework);
		}
		
		evaluateData(productMap);
	}
	
	public void deleteOldRecap()
	{
		File file = new File("recap_output/recap.pdf");

        if (!file.exists()) {
            System.out.println("File does not exist.");
        } else {
            if (file.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        }
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
	
	public Element getData(String orderNum)
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
	
	public void extractData(Map<String,Product> map,Element table) throws ParseException
	{
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
				if(hour > 29 && hour < 54)
				{
					hour = hour - 30;
				} else if (hour > 26 && hour < 29)
				{
					hour = 26;
				}
				int quantity = (int) Double.parseDouble(td.get(8).text().replace(",", ""));
				double weight = Double.parseDouble(td.get(10).text().replace(",", ""));
				boolean isCombo = (((JSONObject)productObj.get(itemPack)).get("Container Type").toString().toLowerCase()).contains("combo");
				String type = getType(description);
				map.put(trackingNum,new Product(productCode,trackingNum,hour,type,quantity,weight,isCombo));
			}
		}
	}
	
	public void evaluateData(Map<String,Product> map) throws InterruptedException
	{
		breastGen breastExcel = new breastGen(times);
		tenderGen tenderExcel = new tenderGen(times);
		carcassGen carcassExcel = new carcassGen(times);
		
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
			} else if (product.getType().equals("trim"))
			{
				tenderExcel.addTrim(product);
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
		
		recapGen recapExcel = new recapGen(name,breastExcel,tenderExcel,carcassExcel,condemnList);
		recapExcel.generateExcel();
		
        File file = new File("recap_output/recap.xlsx");
        exportExceltoPDF(file.getAbsolutePath());
        
        sendtoPrinterJob();
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
	
	public static void exportExceltoPDF(String excelFilePath)
	{
        String outputDir = "C:\\Users\\tdphu\\git\\customlabelprinter\\CustomLabelPrinter\\recap_output\\";

        // Full path to LibreOffice Portable or installed soffice.exe
        String libreOfficePath = "D:\\Download\\LibreOfficePortable\\App\\libreoffice\\program\\soffice.exe"; // Adjust path if needed

        // Construct the command to run LibreOffice headless
        String command = libreOfficePath + " --headless --convert-to pdf --outdir " + outputDir + " " + excelFilePath;

        try {
            // Run the process
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Conversion successful!");
            } else {
                System.out.println("Error during conversion. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	public void sendtoPrinterJob() throws InterruptedException
	{
		File file = new File("recap_output/recap.pdf");

        int timeoutSeconds = 30;
        int waited = 0;

        while (!file.exists()) {
            System.out.println("Waiting for file to appear...");
            Thread.sleep(1000); // Wait 1 second
            waited++;

            if (waited >= timeoutSeconds) {
                System.out.println("Timeout reached. File not found.");
                return;
            }
        }
        
        System.out.println("File found!!!.");
		
		String printerIp = "167.110.88.204"; // Replace with your Ricoh's IP
        int printerPort = 9100; // Most printers listen on port 9100 for raw jobs
        String filePath = "recap_output/recap.pdf"; // Can be .txt, .pcl, .ps, or supported PDF

        try (Socket socket = new Socket(printerIp, printerPort);
             OutputStream out = socket.getOutputStream();
             FileInputStream fileInput = new FileInputStream(new File(filePath))) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInput.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
            System.out.println("File sent to printer successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}