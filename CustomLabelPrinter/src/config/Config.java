package config;
public class Config {
    // Static global variable
    public static String printerIP = "167.110.88.225";
    public static String printer2IP = "167.110.88.243";
    
    public static String officePrinterIP = "167.110.88.204";
    public static String officePrinter2IP = "167.110.88.200";
    
    public static String username = "lcuevas";
    public static String password = "0403";
    
    public static String neowhistlePassword = "thanhnhan011695";
    
    public static String webSocketproxyIP = "154.12.13.186";
    public static int webSocketproxyPort = 5490;
    public static String webSocketproxyUser = "moneymine";
    public static String webSocketproxyPass = "5as12fbuq0aio";
    
    public static String ppwExcelPath = System.getProperty("user.home") + "\\Desktop\\recap_output\\recap.xlsx";
    public static String ppwPDFPath = System.getProperty("user.home") + "\\Desktop\\recap_output\\recap.pdf";

    public static String serverDomain = "erp-app-dc3826c87da0.herokuapp.com";

    public static String chatId = "DSI ERP";
    public static int dayTimeSaving = 1;
    public static boolean chatServer = true;
}