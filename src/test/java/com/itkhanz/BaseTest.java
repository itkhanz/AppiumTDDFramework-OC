package com.itkhanz;

import com.aventstack.extentreports.Status;
import com.itkhanz.constants.Constants;
import com.itkhanz.pages.safari.SafariPage;
import com.itkhanz.reports.ExtentManager;
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
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.MDC;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
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
    private static AppiumDriverLocalService appiumService;

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

    @BeforeSuite
    protected void beforeSuite() {
        String folderName = "logs" + File.separator + "server" + File.separator + TestUtils.getFormattedDateTime();
        File logFolder = new File(folderName);
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }
        //route logs to separate file for each thread
        //ThreadContext.put("ROUTINGKEY", strFile); //LOG4J2
        MDC.put("ROUTINGKEY", folderName); //SLF4J

        //starts the appium server
        appiumService = getAppiumService(folderName);
        if (!checkIfAppiumServerIsRunnning(4723)) {
            appiumService.start();
            appiumService.clearOutPutStreams(); // -> Comment this if you want to see server logs in the console
            testUtils.log().info("*********** Appium Server Started ***********");
        } else {
            testUtils.log().info("*********** Appium Server Already Running ***********");
        }

        //sets the System/Environment info for extent report
        ExtentManager.getReporter().setSystemInfo("OS", System.getProperty("os.name"));
        Properties projectProps = new Properties();
        try {
            projectProps.load(getClass().getClassLoader().getResourceAsStream("project.properties"));
            ExtentManager.getReporter().setSystemInfo("JDK", projectProps.getProperty("java-version"));
            ExtentManager.getReporter().setSystemInfo("Appium Server", projectProps.getProperty("appium-server"));
            ExtentManager.getReporter().setSystemInfo("Appium Client", projectProps.getProperty("appium-version"));
            ExtentManager.getReporter().setSystemInfo("TestNG", projectProps.getProperty("testng-version"));
            ExtentManager.getReporter().setSystemInfo("ExtentReport", projectProps.getProperty("extentreports-version"));
            ExtentManager.getReporter().setSystemInfo("Owner", projectProps.getProperty("owner"));
        } catch (IOException e) {
            testUtils.log().info("failed to load project.properties");
        }
    }

    @AfterSuite (alwaysRun = true)
    protected void afterSuite() {
        if (appiumService.isRunning()) {
            appiumService.stop();
            testUtils.log().info("*********** Appium Server Stopped ***********");
        }
    }

    protected boolean checkIfAppiumServerIsRunnning(int port) {
        boolean isAppiumServerRunning = false;
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
            socket.close();
        } catch (IOException e) {
            isAppiumServerRunning = true;
        } finally {
            socket = null;
        }
        return isAppiumServerRunning;
    }

    protected AppiumDriverLocalService getAppiumService(String appiumLogsFolder) {

        //comment this line if you are running tests through maven surefire plugin
        HashMap<String, String> environment = getEnvironmentMapForAppiumServer();

        return AppiumDriverLocalService.buildService(
          new AppiumServiceBuilder()
                  .usingDriverExecutable(new File("/Users/ibkh/.nvm/versions/node/v18.16.0/bin/node"))
                  .withAppiumJS(new File("/Users/ibkh/.nvm/versions/node/v18.16.0/lib/node_modules/appium/index.js"))
                  //.withIPAddress("http://127.0.0.1")
                  .usingPort(4723)
                  .withArgument(GeneralServerFlag.USE_DRIVERS, "uiautomator2,xcuitest")
                  .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                  .withLogFile(new File(appiumLogsFolder + "/server.log"))
                  .withTimeout(Duration.ofSeconds(30))
                  .withEnvironment(environment) //only needed when running tests with IntelliJ and not from maven cmd
        );
    }

    protected HashMap<String, String> getEnvironmentMapForAppiumServer() {
        //IntelliJ does not have access to path and environment variables which are necessary to run appium server like JDK, Android SDK, cmdline-tools etc
        //so we need to provide it in the code by ourselves
        HashMap<String, String> environment = new HashMap<String, String>();
        //RUN echo $PATH
        //Add the PATH for Node, JDK, Maven, ANDROID_HOME, Android platform-tools, cmdline-tools
        final String NODE = "/Users/ibkh/.nvm/versions/node/v18.16.0/bin";
        final String MAVEN = "/usr/local/bin:/Library/Java/JavaVirtualMachines/jdk-17.0.2.jdk/Contents/Home/bin";
        final String JAVA = "/Library/Maven/apache-maven-3.9.2/bin";
        final String ANDROID_CMD_TOOLS = "/Users/ibkh/Library/Android/sdk/cmdline-tools";
        final String ANDROID_PLATFORM_TOOLS = "/Users/ibkh/Library/Android/sdk/platform-tools";

        final String zshrcPath = NODE + ":" + MAVEN + ":" + JAVA + ":" + ANDROID_CMD_TOOLS + ":" + ANDROID_PLATFORM_TOOLS;
        //RUN where xcode-select will give result as /usr/bin/xcode-select
        //Append to the PATH
        final String xcodeSelect = ":/usr/bin/";

        environment.put("PATH", zshrcPath + xcodeSelect);

        //ANDROID_HOME can be found by echo $ANDROID_HOME or opening SDK Manager in Android Studio
        final String ANDROID_HOME = "/Users/ibkh/Library/Android/sdk";
        environment.put("ANDROID_HOME", ANDROID_HOME);

        return environment;
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
        //ThreadContext.put("ROUTINGKEY", strFile); //LOG4J2
        MDC.put("ROUTINGKEY", strFile); //SLF4J
        testUtils.log().info("log path: " + strFile);

        setPlatform(platformName); //we declared platform as protected class variable because we need this info in test cases
        setDevice(deviceName);
        setDateTime(TestUtils.getFormattedDateTime());
        ;

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
                                .setAppWaitDuration(Duration.ofSeconds(30))
                                .setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(60))
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
            testUtils.log().info("************ Appium Driver is initialized with session id: " + sessionID);

        } catch (IOException e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to load the config.properties");
        } finally {
            if (configStream != null) configStream.close();
            if (stringsStream != null) stringsStream.close();
        }

    }

    @AfterTest (alwaysRun = true)
    public void teardown() {
        if (getDriver()!= null)  {
            getDriver().quit();
            testUtils.log().info("****** Appium Driver Session closed ************");
        }
    }

    @BeforeMethod
    public void beforeMethodBase(Method method) {
        //If the listener executes first then you may get null exception because platform and device is null at the time of method call
        //To work around, we can call the ExtentManager.startTest() in the BaseTest Class.
        ExtentManager.startTest(method.getName(), method.getAnnotation(Test.class).description())
                .assignCategory(getPlatform() + "_" + getDevice())
                .assignAuthor("itkhanz")
                ;

        //testUtils.log().info(".....super before method.....");
        ((CanRecordScreen) getDriver()).startRecordingScreen();
    }

    //stop video capturing and create *.mp4 file
    @AfterMethod
    public synchronized void afterMethodBase(ITestResult result) throws IOException {
        //testUtils.log().info(".....super after method.....");
        String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();

        Map<String, String> testParams = result.getTestContext().getCurrentXmlTest().getAllParameters();

        //TODO datetime will be different this time because the screenshot is captured in listener which executes before this  method which causes few seconds difference so separatte folder is getting created for videos
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
    public void click(WebElement e, String msg) {
        waitForVisibility(e);
        testUtils.log().info(msg);
        ExtentManager.getTest().log(Status.INFO, msg);
        e.click();
    }

    public void clear(WebElement element) {
        waitForVisibility(element);
        element.clear();
    }

    public void sendKeys(WebElement element, String text) {
        waitForVisibility(element);
        element.sendKeys(text);
    }
    public void sendKeys(WebElement e, String txt, String msg) {
        waitForVisibility(e);
        testUtils.log().info(msg);
        ExtentManager.getTest().log(Status.INFO, msg);
        e.sendKeys(txt);
    }

    public String getAttribute(WebElement element, String attribute) {
        waitForVisibility(element);
        String attr =  element.getAttribute(attribute);
        ExtentManager.getTest().log(Status.INFO, attribute + " attribute value is: " + attr);
        return attr;
    }
    public String getAttribute(WebElement element, String attribute, String msg) {
        testUtils.log().info(msg);
        ExtentManager.getTest().log(Status.INFO, msg);
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
    public String getLabelText(WebElement e, String msg) {
        testUtils.log().info(msg);
        ExtentManager.getTest().log(Status.INFO, msg);
        String txt = null;

        switch (getPlatform()) {
            case "Android" -> {
                txt = getAttribute(e, "text");
            }
            case "iOS" -> {
                txt = getAttribute(e, "label");
            }
            default -> {
                return txt;
            }
        };
        return txt;
    }

    //TODO Move Deeplink related methods to separate class
    public void openAppWith(String url) {
        //String platform = getDriver().getCapabilities().getCapability("platformName").toString().toLowerCase();
        testUtils.log().info("Navigating to Products Overview through deep link: " + url);
        ExtentManager.getTest().log(Status.INFO, "Navigating to Products Overview through deep link: " + url);
        switch (getPlatform().toLowerCase()) {
            case "android" -> {
                HashMap<String, String> deepUrl = new HashMap<>();
                deepUrl.put("url", url);
                deepUrl.put("package", getProps().getProperty("androidAppPackage"));
                testUtils.log().info("Executing command mobile: deepLink with " + url);
                getDriver().executeScript("mobile: deepLink", deepUrl);
            }
            case "ios" -> {
                new SafariPage().openAppWithDeepLink(url);
            }
            default -> throw new RuntimeException("Inavalid Platform: " + platform);
        }

    }


}
