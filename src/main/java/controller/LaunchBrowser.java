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
import javax.swing.border.Border;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LaunchBrowser extends JFrame {
    private static ChromeOptions options;
    private static WebDriver driver;
    public static String dir = System.getProperty("user.dir"); //Getting user Directory
    public static String chromedriver = dir + "/drivers/chromedriver";//creating Chromedriver Directory

    public static void main(String[] args) throws Exception {

        // setup browser
        setUpBrowser();

        // get browser elements needed
        Map<String, List<WebElement>> elementsArrays = getWebElements();

        // get api data as strings in an array
        Map<String, String> httpContentResponses= getAPIData(elementsArrays);

        // create ETFCurves object.
        ETFCurves etfCurves = createETFCurves(httpContentResponses, elementsArrays);

        driver.close();
        driver.quit();

        // print curves out for verification
        System.out.println("My curves "+ etfCurves.getCurves().size());
        for (ETFCurve curve : etfCurves.getCurves()) {
            System.out.println(curve.toString().substring(0,100));
        }

        new LaunchBrowser("Funds ", etfCurves);
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

    public static Map<String, String>  getAPIData(Map<String, List<WebElement>> elementsArrays) throws Exception { // create EFTPoint, ETFCurve, and ETFCurves

        List<WebElement> fundTags = elementsArrays.get("fundTags");
        List<WebElement> fundNames = elementsArrays.get("fundNames");

        Map<String, String> responses = new HashMap<>();

        for (int i=0; i<1; i++) { // (int i=0; i<fundTags.size(); i++)

            String fundName = fundNames.get(i).getText();
            String fundCode = fundTags.get(i).getAttribute("data-jsecode");
            String fundDataTargetCode = fundTags.get(i).getAttribute("data-target");
            String fundId = fundCode + " - " + fundName;

            String fundData = getHttpContentResponse(fundDataTargetCode);
            responses.put(fundId, fundData);
        }
        return responses;
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

    public static ETFCurves createETFCurves(Map<String, String> responses, Map<String, List<WebElement>> elementsArrays ) throws Exception {

        List<WebElement> fundTags = elementsArrays.get("fundTags");
        List<WebElement> fundNames = elementsArrays.get("fundNames");


        ArrayList<ETFCurve> etfCurveArray = new ArrayList<>();
        for (Map.Entry<String, String> pair : responses.entrySet()) {
            //convert the json string to json object
            JSONObject jsnobject = new JSONObject(pair.getValue());

            // convert json object to an array
            JSONArray jsonArray = jsnobject.getJSONArray("Prices");

            ETFCurve curve = createETFCurve(jsonArray, pair.getKey(), pair.getValue());
            etfCurveArray.add(curve);
        }

        ETFCurves etfCurves = new ETFCurves(etfCurveArray);

        return  etfCurves;
    }

    public static ETFCurve createETFCurve(JSONArray jsonArray, String fundId, String fundDataString) throws Exception {
        ArrayList<ETFPoint> fundDataArray = new ArrayList<ETFPoint>();
        if (jsonArray != null) {
            //Iterating JSON array
            for (int j = 0; j<jsonArray.length();j++){
                String jsonString = jsonArray.get(j).toString();
                ETFPoint point = createETFPointFromJsonString(jsonString);
                fundDataArray.add(point);
            }
        }
        ETFCurve curve = new ETFCurve(fundId, fundDataArray, fundDataString);

        return curve;
    }

    public static ETFPoint createETFPointFromJsonString(String jsonString) throws Exception {
        ETFPoint point = new ObjectMapper().readValue(jsonString, ETFPoint.class);
        return point;
    }

    public LaunchBrowser(String title, ETFCurves etfCurves) {

        // Create dataset
        DefaultCategoryDataset dataset = createDataset(etfCurves);
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart("EFTs Comparison", "Date", "Price", dataset, PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);

        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0,0,400,400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        Border border = BorderFactory.createLineBorder(Color.black);
        Container container = getContentPane();

        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new java.awt.BorderLayout());
        graphPanel.setPreferredSize(new Dimension(200, 200));
        graphPanel.add(chartPanel);

        JPanel statsPanel=new JPanel();
        statsPanel.setLayout(new java.awt.BorderLayout());
        statsPanel.setPreferredSize(new Dimension(200, 100));
        StringBuilder sb = new StringBuilder();
        // center the text in html maybe it will center in the panel
        sb.append("<html> <h4>Statistics</h4> <br/>").
                append("Gradient: 0.6 <br/>").
                append("Variation: 0.2</html>");
        JLabel label=new JLabel(sb.toString());
        statsPanel.add(label);

        JPanel fundPanel = new JPanel();
        fundPanel.setLayout(new BoxLayout(fundPanel, BoxLayout.Y_AXIS));
        fundPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        fundPanel.setPreferredSize(new Dimension(200, 300));
        fundPanel.add(graphPanel);
        fundPanel.add(statsPanel);
        fundPanel.setBorder(border);

        container.add(fundPanel);

        setVisible(true);
    }

    private DefaultCategoryDataset createDataset(ETFCurves etfCurves) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<ETFPoint> curveData;
        for (ETFCurve curve : etfCurves.getCurves()) { // for each curve
            for (ETFPoint point: curve.getCurveData()) { // for each point in a curve
                dataset.addValue(point.getPrice(), curve.getCurveId(), point.getClosingDate().toString());
            }
//            curveData = curve.getCurveData();
//            int divider = curveData.size() / curveData.size();
//            for (int i = 0; i < curveData.size(); i=i+divider) {
//                dataset.addValue(curveData.get(i).getPrice(), curve.getCurveId(), curveData.get(i).getClosingDate().toString());
//            }
        }

        return dataset;
    }
}
