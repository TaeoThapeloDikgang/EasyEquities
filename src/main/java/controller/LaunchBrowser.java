package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class LaunchBrowser {
    public static String dir = System.getProperty("user.dir"); //Getting user Directory
    public static String chromedriver = dir + "/drivers/chromedriver";//creating Chromedriver Directory

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", chromedriver);
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.get("https://etfs.easyequities.co.za/finder?");

        // todo - see if you can find a way to run the application without openening the browser.
        //ChromeOptions object
//        ChromeOptions options = new ChromeOptions();
//        //headless parameter
//        options.addArguments("headless");
//        // set parameter to Chrome driver
//        WebDriver driver = new ChromeDriver(options);
//        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
//        driver.get("https://www.tutorialspoint.com/questions/index.php");

        // todo - start with one stock object, get the values you need and a create graph
        // todo - use stats to find the trend of the data (for now use the most simple trend method, the gradient)
        // todo - do the same for multiple objects
        // todo - sort the graphs by the most positive trends
        // todo - include variation in your equation to find the best trend
        // todo - check if you can host the application online so it runs everytime to monitor the data
        List<WebElement> allLinks = driver.findElements(By.linkText("Learn More"));
        //Traversing through the list and printing its text along with link address
//        for(WebElement link:allLinks){
//            System.out.println(link.getAttribute("href").substring(41, 53));
//            //System.out.println(link.getText() + " - " + link.getAttribute("href"));
//        }

        String link = allLinks.get(0).getAttribute("href").substring(41, 53);


        URL url = new URL("https://pricing.easyequities.co.za/api/prices?ISINCode=" + link + "&period=0");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {

            String inline = "";
            Scanner scanner = new Scanner(url.openStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            //Close the scanner
            scanner.close();

            System.out.println(inline);

            // todo - the inline is probably enough
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement je = JsonParser.parseString(inline);
            String prettyJsonString = gson.toJson(je);
            System.out.println(prettyJsonString);

            // todo - once all is good, try to optimize the code where you can
            // todo - break the code into multiple packages or use mvc, whatever is best
        }
    }
}
