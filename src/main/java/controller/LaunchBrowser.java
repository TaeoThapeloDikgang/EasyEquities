package controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.ETFCurve;
import model.ETFCurves;
import model.ETFPoint;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LaunchBrowser extends JFrame {
    private static ChromeOptions options;
    private static WebDriver driver;
//    private static List<WebElement> fundTags;
//    private static List<WebElement> fundNames;
//    private static URL url;
//    private static HttpURLConnection conn;
//    private static int responseCode;
//    private static String responseMessage;
//    private static ETFCurves etfCurves;
    public static String dir = System.getProperty("user.dir"); //Getting user Directory
    public static String chromedriver = dir + "/drivers/chromedriver";//creating Chromedriver Directory

    public LaunchBrowser(String title, ETFCurves etfCurves) {
        super(title);
        // Create dataset
        DefaultCategoryDataset dataset = createDataset(etfCurves);
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart("EFTs Comparison", "Date", "Price", dataset, PlotOrientation.VERTICAL, true, false, false);

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private DefaultCategoryDataset createDataset(ETFCurves etfCurves) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (ETFCurve curve : etfCurves.getCurves()) {
            for (ETFPoint point: curve.getCurveData()) {
                dataset.addValue(point.getPrice(), curve.getCurveId(), point.getClosingDate().toString());
            }
        }

        return dataset;
    }

    public static void setUpBrowser() { // create options and driver
        System.setProperty("webdriver.chrome.driver", chromedriver);

        options = new ChromeOptions();
        options.addArguments("headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.get("https://etfs.easyequities.co.za/finder?");
    }

    public static Map<String, List<WebElement>> getWebElements() { // create fundtags and fundnames webelements
        Map<String, List<WebElement>> elementsArrays = new HashMap<>();
        List<WebElement> fundTags = driver.findElements(By.cssSelector ("div[data-jsecode]"));
        List<WebElement> fundNames = driver.findElements(By.className("fund-name"));
        elementsArrays.put("fundTags", fundTags);
        elementsArrays.put("fundNames", fundNames);
        return elementsArrays;
    }

    public static String getHttpContentResponse(String fundDataTargetCode) throws Exception {

        URL url = new URL("https://pricing.easyequities.co.za/api/prices?ISINCode=" + fundDataTargetCode + "&period=0");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode + "," + " Message: "+responseMessage);
        }
        else {
            // write all the API JSON data into a string using a scanner
            String inline = "";
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }
            conn.disconnect();
            scanner.close();
            return inline;
        }
    }

    public static ETFPoint createETFPointFromJsonString(String jsonString) throws Exception {
        ETFPoint point = new ObjectMapper().readValue(jsonString, ETFPoint.class);
        return point;
    }

    public static ETFCurve createETFCurve(JSONArray jsonArray, String fundId, String httpContentResponse) throws Exception {
        ArrayList<ETFPoint> fundData = new ArrayList<ETFPoint>();
        if (jsonArray != null) {
            //Iterating JSON array
            for (int j = 0; j<jsonArray.length();j++){
                String jsonString = jsonArray.get(j).toString();
                ETFPoint point = createETFPointFromJsonString(jsonString);
                fundData.add(point);
            }
        }
        ETFCurve curve = new ETFCurve(fundId, fundData, httpContentResponse);

        return curve;
    }

    public static ETFCurves createETFCurves(ArrayList<ETFCurve> curves) {
        ETFCurves etfCurves = new ETFCurves(curves);
        return  etfCurves;
    }

    public static ETFCurves getAPIData(Map<String, List<WebElement>> elementsArrays) throws Exception { // create EFTPoint, ETFCurve, and ETFCurves

        List<WebElement> fundTags = elementsArrays.get("fundTags");
        List<WebElement> fundNames = elementsArrays.get("fundNames");

        ArrayList curves = new ArrayList<ETFCurve>();
        for (int i=0; i<fundTags.size(); i++) {

            String fundName = fundNames.get(i).getText();
            String fundCode = fundTags.get(i).getAttribute("data-jsecode");
            String fundDataTargetCode = fundTags.get(i).getAttribute("data-target");
            String fundId = fundName + " - " + fundCode;

            String httpContentResponse = getHttpContentResponse(fundDataTargetCode);

             // todo - 2. add httpContentResponse to array, each item is an http response.

             // todo - 3. data from here downwards, can be manipulated to obtaine what we what from the strings and create our objects



            // convert the json string to json object
            JSONObject jsnobject = new JSONObject(httpContentResponse);

            // convert json object to an array
            JSONArray jsonArray = jsnobject.getJSONArray("Prices");

            // add this array to another array and return it

            ETFCurve curve = createETFCurve(jsonArray, fundId, httpContentResponse);

            curves.add(curve);

        }

        ETFCurves etfCurves = createETFCurves(curves);

        return etfCurves;
    }

    public static void showGraphWindow(ETFCurves etfCurves) {
        SwingUtilities.invokeLater(() -> {
            LaunchBrowser window = new LaunchBrowser("Fund Line Chart", etfCurves);
            window.setAlwaysOnTop(true);
            window.pack();
            window.setSize(600, 400);
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            window.setVisible(true);
        });
    }

    public static void main(String[] args) throws Exception {

        // setup browser
        setUpBrowser();

        // get browser elements needed
        Map<String, List<WebElement>> elementsArrays = getWebElements();

        // todo - 1. get api data as row strings in an array. go up for todo 2.

        // todo - 4. this down here will change after doing point 2. and 3.
        ETFCurves etfCurves = getAPIData(elementsArrays);

        // create your ojects

        // put all objects under parent object



        driver.close();
        driver.quit();


        System.out.println("My curves "+ etfCurves.getCurves().size());
        for (ETFCurve curve : etfCurves.getCurves()) {
            System.out.println(curve.toString());
            System.out.println("########################################################################################################################");
        }

        showGraphWindow(etfCurves);
    }
}
