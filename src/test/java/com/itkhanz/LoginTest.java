package com.itkhanz;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class LoginTest {
    AppiumDriver driver;

    @BeforeMethod
    public void setup() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setAutomationName("UIAutomator2")
                .setPlatformName("Android")
                .setPlatformVersion("13.0")
                .setUdid("emulator-5554")
                .setAppPackage("com.swaglabsmobileapp")
                .setAppActivity("com.swaglabsmobileapp.SplashActivity")
                //.setApp("src//test//resources//apps//Android.SauceLabs.Mobile.Sample.app.2.7.1.apk") //not needed when app is pre-installed
                //.setAvd("pixel_5")  //hw device name of emulator
                ;

        try {
            URL url = new URL("http://127.0.0.1:4723");
            driver = new AppiumDriver(url, options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to initialize URL for Appium Session: " + "http://127.0.0.1:4723");
        }


        String sessionID = driver.getSessionId().toString();
        System.out.println("Appium Driver is initialized with session id: " + sessionID);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void teardown() {
        if (driver!= null) driver.quit();
    }

    @Test
    public void invalidUsernameTest() {
        WebElement usernameTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginBtn = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        usernameTxtFld.sendKeys("invalidusername");
        passwordTxtFld.sendKeys("secret_sauce");
        loginBtn.click();

        WebElement errText = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView"));

        String actualErrTxt = errText.getAttribute("text");
        String expectedErrTxt = "Username and password do not match any user in this service.";

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test
    public void invalidPasswordTest() {
        WebElement usernameTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginBtn = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        usernameTxtFld.sendKeys("standard_user");
        passwordTxtFld.sendKeys("inavlidpassword");
        loginBtn.click();

        WebElement errText = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView"));

        String actualErrTxt = errText.getAttribute("text");
        String expectedErrTxt = "Username and password do not match any user in this service.";

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test
    public void successfulLoginTest() {
        WebElement usernameTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordTxtFld = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginBtn = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        usernameTxtFld.sendKeys("standard_user");
        passwordTxtFld.sendKeys("secret_sauce");
        loginBtn.click();

        WebElement productTitleText = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='test-Cart drop zone']//android.widget.TextView"));

        String actualProductTitle = productTitleText.getAttribute("text");
        String expectedProductTitle = "PRODUCTS";

        Assert.assertEquals(actualProductTitle, expectedProductTitle);
    }
}
