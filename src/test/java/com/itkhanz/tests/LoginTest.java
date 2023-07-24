package com.itkhanz.tests;

import com.itkhanz.BaseTest;
import com.itkhanz.constants.Constants;
import com.itkhanz.pages.saucelabs.LoginPage;
import com.itkhanz.pages.saucelabs.ProductsPage;
import com.itkhanz.utils.TestUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.lang.reflect.Method;

public class LoginTest extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;  //This object can we keep at classes level because we are not manipulating the read-data
    JSONObject loginUsersObject;
    TestUtils testUtils = new TestUtils();

    @BeforeClass
    public void beforeClass() {
        //TODO parse test data using Google GSON library
        //TODO use Optional in java to check for null
        InputStream loginDetails = null;

        try {
            loginDetails = getClass().getClassLoader().getResourceAsStream(Constants.dataFileName);
            JSONTokener tokener = new JSONTokener(loginDetails);
            loginUsersObject = new JSONObject(tokener);

            if (loginDetails != null) {
                loginDetails.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load the test user data from " + Constants.dataFileName);
        }
    }

    @BeforeMethod
    public void beforeMethod(Method m) {
        //testUtils.log().info(".....LoginTest before method.....");
        closeApp();
        launchApp();
        testUtils.log().info("****** Starting test:" + m.getName() + " ******");
        loginPage = new LoginPage();
    }

    @AfterMethod()
    public void afterMethod(Method m){
        testUtils.log().info("****** Ending test:" + m.getName() + " ******");
    }

    @Test (description = "This tests validates the failed login attempt with invalid username")
    public void invalidUsernameTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName(loginUsersObject.getJSONObject("invalidUser").getString("username"));
        loginPage.enterPassword(loginUsersObject.getJSONObject("invalidUser").getString("password"));
        loginPage.pressLoginBtn();

        String actualErrTxt = loginPage.getErrTxt();
        String expectedErrTxt = getStringsMap().get("err_invalid_username_or_password");
        //testUtils.log().info("actual error text - " + actualErrTxt + "\n" + "expected error text - " + expectedErrTxt);
        testUtils.log().info("actual error text - " + actualErrTxt);
        testUtils.log().info("expected error text - " + expectedErrTxt);

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test (description = "This tests validates the failed login attempt with invalid password")
    public void invalidPasswordTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName(loginUsersObject.getJSONObject("invalidPassword").getString("username"));
        loginPage.enterPassword(loginUsersObject.getJSONObject("invalidPassword").getString("password"));
        loginPage.pressLoginBtn();

        String actualErrTxt = loginPage.getErrTxt();
        String expectedErrTxt = getStringsMap().get("err_invalid_username_or_password");
        testUtils.log().info("actual error text - " + actualErrTxt);
        testUtils.log().info("expected error text - " + expectedErrTxt);

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test (description = "This tests validates the successful login attempt with valid credentials")
    public void successfulLoginTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName(loginUsersObject.getJSONObject("validUser").getString("username"));
        loginPage.enterPassword(loginUsersObject.getJSONObject("validUser").getString("password"));
        productsPage = loginPage.pressLoginBtn();

        String actualProductTitle = productsPage.getTitle();
        String expectedProductTitle = getStringsMap().get("product_title");
        testUtils.log().info("actual product title - " + actualProductTitle);
        testUtils.log().info("expected product title - " + expectedProductTitle);

        Assert.assertEquals(actualProductTitle, expectedProductTitle);
    }
}
