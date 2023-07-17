package com.itkhanz;

import com.itkhanz.constants.Constants;
import com.itkhanz.utils.TestUtils;
import com.itkhanz.utils.XMLUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

public class BaseTest {

    //TODO move driver initialization to driver factory and make driver threadlocal
    //TODO move driver initialization to properties utils
    protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
    protected static ThreadLocal<Properties> props = new ThreadLocal<Properties>();
    protected static ThreadLocal<HashMap<String, String>> stringsMap = new ThreadLocal<HashMap<String, String>>();
    protected static ThreadLocal<String>  platform = new ThreadLocal<String>();
    protected static ThreadLocal<String>  device = new ThreadLocal<String>();
    public static ThreadLocal<String> dateTime = new ThreadLocal<String>();

    TestUtils testUtils = new TestUtils();

    public AppiumDriver getDriver() {
        return driver.get();
    }
    public void setDriver(AppiumDriver dr) {
        driver.set(dr);
    }

    public Properties getProps() {
        return props.get();
    }
    public void setProps(Properties pr) {
        props.set(pr);
    }

    public HashMap<String, String> getStringsMap() {
        return stringsMap.get();
    }
    public void setStringsMap(HashMap<String, String> stMap) {
        stringsMap.set(stMap);
    }

    public String getPlatform() {
        return platform.get();
    }
    public void setPlatform(String pl) {
        platform.set(pl);
    }

    public String getDevice() {
        return device.get();
    }
    public void setDevice(String deviceName) {
        device.set(deviceName);
    }

    public String getDateTime() {
        return dateTime.get();
    }
    public void setDateTime(String dt) {
        dateTime.set(dt);
    }

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()),this);
    }

    @Parameters({"platformName", "udid", "deviceName", "emulator", "systemPort", "chromeDriverPort", "wdaLocalPort"})
    @BeforeTest
    public void setup(String platformName, String udid, String deviceName,
                      @Optional("androidOnly")String emulator,
                      @Optional("10000")String systemPort,
                      @Optional("11000")String chromeDriverPort,
                      @Optional("8100")String wdaLocalPort) throws Exception{
        InputStream configStream = null;
        InputStream stringsStream = null;
        Properties props = new Properties();
        AppiumDriver driver;

        String strFile = "logs" + File.separator + platformName + "_" + deviceName;
        File logFile = new File(strFile);
        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        //route logs to separate file for each thread
        ThreadContext.put("ROUTINGKEY", strFile);
        testUtils.log().info("log path: " + strFile);

        setPlatform(platformName); //we declared platform as protected class variable because we need this info in test cases
        setDevice(deviceName);
        setDateTime(TestUtils.getFormattedDateTime());
        try {
            String xmlFileName = "strings/strings.xml";
            stringsStream = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            setStringsMap(XMLUtils.parseStringXML(stringsStream));

            String propFileName = "config.properties";
            configStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            props.load(configStream);
            setProps(props);

            try {
                //Use this url if running single appium server
                //if running multiple servers on different ports, then comment this, and use the url inside switch separately for both drivers
                URL url = new URL(getProps().getProperty("appiumURL") + ":" + getProps().getProperty("appiumServerDefaultPort"));
                switch (platformName) {
                    case "Android" -> {
                        //get the complete path of the app on local machine (it will append the root path with the property)
                        String appURL = Objects.requireNonNull(getClass().getResource(getProps().getProperty("androidAppLocation"))).getFile();
                        UiAutomator2Options options = new UiAutomator2Options()
                                .setAutomationName(getProps().getProperty("androidAutomationName"))
                                .setPlatformName(platformName)
                                .setDeviceName(deviceName)   //AvdId (not needed with udid)
                                .setUdid(udid) //UDID is preferred over platform version and platform name to uniquely identify device
                                .setAppPackage(getProps().getProperty("androidAppPackage"))
                                .setAppActivity(getProps().getProperty("androidAppActivity"))
                                //.setApp(appURL) //not needed when app is pre-installed
                                .setAppWaitActivity(getProps().getProperty("androidAppWaitActivity"))    //wait for the main activity to start, must use it when using appurl instead of appPackage and appActivity
                                .setSystemPort(Integer.parseInt(systemPort))
                                .setChromedriverPort(Integer.parseInt(chromeDriverPort))
                                ;

                        /*if (emulator.equalsIgnoreCase("true")) {
                            options.setAvd(deviceName) ; //hw device name of emulator e.g. Pixel_5, it automatically opens up the emulator
                        }*/

                        //URL url = new URL(getProps().getProperty("appiumURL") + ":" + getProps().getProperty("androidPort"));
                        driver = new AndroidDriver(url, options);
                    }
                    case "iOS" -> {
                        //get the complete path of the app on local machine (it will append the root path with the property)
                        String appURL = Objects.requireNonNull(getClass().getResource(getProps().getProperty("iOSAppLocation"))).getFile();
                        XCUITestOptions options = new XCUITestOptions()
                                .setAutomationName(getProps().getProperty("iOSAutomationName"))
                                .setPlatformName(platformName)
                                .setDeviceName(deviceName)   // not needed with udid
                                .setUdid(udid) //not needed when device name and platform version are enough to locate the emulator running
                                .setBundleId(getProps().getProperty("iOSBundleId")) //not needed with appUrl
                                //.setApp(appURL) //not needed when app is pre-installed
                                .setUsePrebuiltWda(true)    //speeds up the test execution if WDA is already on the device
                                .setWdaLocalPort(Integer.parseInt(wdaLocalPort))
                                ;
                        //URL url = new URL(getProps().getProperty("appiumURL") + ":" + getProps().getProperty("iosPort"));
                        driver = new IOSDriver(url, options);
                    }
                    default -> throw new RuntimeException("Inavlid platform! - " + platformName);
                }
                setDriver(driver);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw  new RuntimeException("Failed to initialize URL for Appium Session: " + "http://127.0.0.1:4723");
            }

            String sessionID = driver.getSessionId().toString();
            testUtils.log().info("Appium Driver is initialized with session id: " + sessionID);

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
        if (getDriver()!= null) getDriver().quit();
    }

    @BeforeMethod
    public void beforeMethodBase() {
        //testUtils.log().info(".....super before method.....");
        ((CanRecordScreen) getDriver()).startRecordingScreen();
    }

    //stop video capturing and create *.mp4 file
    @AfterMethod
    public synchronized void afterMethodBase(ITestResult result) throws IOException {
        //testUtils.log().info(".....super after method.....");
        String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();

        Map<String, String> testParams = result.getTestContext().getCurrentXmlTest().getAllParameters();

        //TODO datetime will be different this time because the sceeenshot is captured in listener which executes before this  method which causes few seconds difference so separatte folder is getting created for videos
        //TODO sync the time for both screenshots and videos so both are created under single timestamp
        //TODO above issue is possibly resolved becasue of threadlocal dateTime
        String dirPath =  "media" + File.separator
                + testParams.get("platformName") + "_" + testParams.get("deviceName") + File.separator
                + "videos" + File.separator
                + getDateTime() + File.separator
                + result.getTestClass().getRealClass().getSimpleName()
                ;

        File videoDir = new File(dirPath);
        synchronized(videoDir) {
            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }
        }

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
            stream.write(Base64.decodeBase64(media));
            stream.close();
            testUtils.log().info("video path: " + videoDir + File.separator + result.getName() + ".mp4");
        } catch (Exception e) {
            testUtils.log().info("error during video capture" + e.toString());
        } finally {
            if(stream != null) {
                stream.close();
            }
        }
    }

    public void closeApp() {
        switch (getPlatform()) {
            case "Android" -> ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("androidAppPackage"));
            case "iOS" -> ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("iOSBundleId"));
        }
    }

    public void launchApp() {
        switch (getPlatform()) {
            case "Android" -> ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("androidAppPackage"));
            case "iOS" -> ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("iOSBundleId"));
        }
    }

    //TODO create separate Util class to manage waits
    //TODO overload the waitForVisibility method to accept custom waiting time
    //TODO create the wait methods for intractability, presence etc.
    public void waitForVisibility(WebElement element) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(Constants.WAIT));
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
        return switch (getPlatform()) {
            case "Android" -> getAttribute(e, "text");
            case "iOS" -> getAttribute(e, "label");
            default -> null;
        };
    }


}
