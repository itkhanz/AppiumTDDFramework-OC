package com.itkhanz;

import com.itkhanz.constants.Constants;
import com.itkhanz.utils.XMLUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

public class BaseTest {

    //TODO move driver initialization to driver factory and make driver threadlocal
    //TODO move driver initialization to properties utils
    protected static AppiumDriver driver;
    protected static Properties props;
    protected static HashMap<String, String> stringsMap = new HashMap<String, String>();
    InputStream configStream;
    InputStream stringsStream;

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(driver),this);
    }

    @Parameters({"platformName", "platformVersion", "deviceName", "udid"})
    @BeforeTest
    public void setup(String platformName, String platformVersion, String deviceName, String udid) throws Exception{
        try {
            String xmlFileName = "strings/strings.xml";
            stringsStream = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            stringsMap = XMLUtils.parseStringXML(stringsStream);

            props = new Properties();
            String propFileName = "config.properties";
            configStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            props.load(configStream);

            String appURL = Objects.requireNonNull(getClass().getResource(props.getProperty("androidAppLocation"))).getFile();

            UiAutomator2Options options = new UiAutomator2Options()
                    .setAutomationName("UIAutomator2")
                    .setPlatformName(platformName)
                    .setPlatformVersion(platformVersion)
                    .setDeviceName(deviceName)   //AvdId (not needed with udid)
                    .setUdid(udid) //not needed when device name and platform version are enough to locate the emulator running
                    .setAppPackage(props.getProperty("androidAppPackage"))
                    .setAppActivity(props.getProperty("androidAppActivity"))
                    //.setApp(appURL) //not needed when app is pre-installed
                    //.setAppWaitActivity(props.getProperty("androidAppWaitActivity"))    //wait for the main activity to start, must use it when using appurl instead of appPackage and appActivity
                    //.setAvd("pixel_5")  //hw device name of emulator
                    ;

            try {
                URL url = new URL(props.getProperty("appiumURL"));
                driver = new AppiumDriver(url, options);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw  new RuntimeException("Failed to initialize URL for Appium Session: " + "http://127.0.0.1:4723");
            }

            String sessionID = driver.getSessionId().toString();
            System.out.println("Appium Driver is initialized with session id: " + sessionID);
            //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        } catch (IOException e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to load the config.properties");
        } finally {
            if (configStream != null) configStream.close();
            if (stringsStream != null) stringsStream.close();
        }

    }

    //TODO driver must be initialized and closed after each test test method
    //TODO add alwaysrun true

    @AfterTest
    public void teardown() {
        if (driver!= null) driver.quit();
    }

    //TODO create separate Util class to manage waits
    //TODO overload the waitForVisibility method to accept custom waiting time
    //TODO create the wait methods for intractability, presence etc.
    public void waitForVisibility(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(Constants.WAIT));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    //TODO Create separate Driver Utility classes to manage driver specific actions
    public void click(WebElement element) {
        waitForVisibility(element);
        element.click();
    }

    public void clear(WebElement element) {
        waitForVisibility(element);
        element.clear();
    }

    public void sendKeys(WebElement element, String text) {
        waitForVisibility(element);
        element.sendKeys(text);
    }

    public String getAttribute(WebElement element, String attribute) {
        waitForVisibility(element);
        return element.getAttribute(attribute);
    }
}
