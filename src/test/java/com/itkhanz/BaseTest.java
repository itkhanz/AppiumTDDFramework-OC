package com.itkhanz;

import com.itkhanz.constants.Constants;
import com.itkhanz.utils.XMLUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
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
    protected static HashMap<String, String> stringsMap = new HashMap<>();
    protected static String platform;
    InputStream configStream;
    InputStream stringsStream;

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(driver),this);
    }

    @Parameters({"platformName", "deviceName", "udid"})
    @BeforeTest
    public void setup(String platformName, String deviceName, String udid) throws Exception{
        try {
            String xmlFileName = "strings/strings.xml";
            stringsStream = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            stringsMap = XMLUtils.parseStringXML(stringsStream);

            props = new Properties();
            String propFileName = "config.properties";
            configStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            props.load(configStream);

            try {
                platform = platformName; //we declared platform as protected class variable because we need this info in test cases
                URL url = new URL(props.getProperty("appiumURL"));
                switch (platformName) {
                    case "Android" -> {
                        //get the complete path of the app on local machine (it will append the root path with the property)
                        String appURL = Objects.requireNonNull(getClass().getResource(props.getProperty("androidAppLocation"))).getFile();
                        UiAutomator2Options options = new UiAutomator2Options()
                                .setAutomationName(props.getProperty("androidAutomationName"))
                                .setPlatformName(platformName)
                                .setDeviceName(deviceName)   //AvdId (not needed with udid)
                                .setUdid(udid) //UDID is preferred over platform version and platform name to uniquely identify device
                                .setAppPackage(props.getProperty("androidAppPackage"))
                                .setAppActivity(props.getProperty("androidAppActivity"))
                                //.setApp(appURL) //not needed when app is pre-installed
                                .setAppWaitActivity(props.getProperty("androidAppWaitActivity"))    //wait for the main activity to start, must use it when using appurl instead of appPackage and appActivity
                                //.setAvd(deviceName)  //hw device name of emulator e.g. Pixel_5, it automatically opens up the emulator
                                ;
                        driver = new AndroidDriver(url, options);
                    }
                    case "iOS" -> {
                        //get the complete path of the app on local machine (it will append the root path with the property)
                        String appURL = Objects.requireNonNull(getClass().getResource(props.getProperty("iOSAppLocation"))).getFile();
                        XCUITestOptions options = new XCUITestOptions()
                                .setAutomationName(props.getProperty("iOSAutomationName"))
                                .setPlatformName(platformName)
                                .setDeviceName(deviceName)   // not needed with udid
                                .setUdid(udid) //not needed when device name and platform version are enough to locate the emulator running
                                .setBundleId(props.getProperty("iOSBundleId")) //not needed with appUrl
                                //.setApp(appURL) //not needed when app is pre-installed
                                .setUsePrebuiltWda(true)    //speeds up the test execution if WDA is already on the device
                                ;
                        driver = new IOSDriver(url, options);
                    }
                    default -> throw new RuntimeException("Inavlid platform! - " + platformName);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw  new RuntimeException("Failed to initialize URL for Appium Session: " + "http://127.0.0.1:4723");
            }

            String sessionID = driver.getSessionId().toString();
            System.out.println("Appium Driver is initialized with session id: " + sessionID);

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

    public String getLabelText(WebElement e) {
        String txt = null;
        return switch (platform) {
            case "Android" -> getAttribute(e, "text");
            case "iOS" -> getAttribute(e, "label");
            default -> null;
        };
    }
}
