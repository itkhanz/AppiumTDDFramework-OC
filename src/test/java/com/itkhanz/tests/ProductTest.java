package com.itkhanz.tests;

import com.itkhanz.BaseTest;
import com.itkhanz.constants.Constants;
import com.itkhanz.pages.saucelabs.LoginPage;
import com.itkhanz.pages.saucelabs.ProductDetailsPage;
import com.itkhanz.pages.saucelabs.ProductsPage;
import com.itkhanz.pages.saucelabs.SettingsPage;
import com.itkhanz.utils.TestUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.InputStream;
import java.lang.reflect.Method;

public class ProductTest extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;
    SettingsPage settingsPage;
    ProductDetailsPage productDetailsPage;
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
        //testUtils.log().info(".....ProductTest before method.....");
        //closeApp();
        //launchApp();
        testUtils.log().info("****** Starting test:" + m.getName() + " ******");
        openAppWith("swaglabs://swag-overview/0,5");
        productsPage = new ProductsPage();
        /*loginPage = new LoginPage();
        productsPage = loginPage.login(
                loginUsersObject.getJSONObject("validUser").getString("username"),
                loginUsersObject.getJSONObject("validUser").getString("password")
        );*/
    }

    @AfterMethod
    public void afterMethod(Method m) {
        //settingsPage = productsPage.pressSettingsBtn(); //open the sidebar
        //loginPage = settingsPage.pressLogoutBtn();  //click on logout button in sidebar
        testUtils.log().info("****** Ending test:" + m.getName() + " ******");
    }

    @Test (description = "Validate the Sauce Labs Backpack title and price on HomePage")
    public void validateProductOnProductsPage() {
        String SLBTitle = productsPage.getSLBTitle();
        String SLBPrice = productsPage.getSLBPrice();

        SoftAssert sa = new SoftAssert();
        sa.assertEquals(SLBTitle, getStringsMap().get("products_page_slb_title"));
        sa.assertEquals(SLBPrice, getStringsMap().get("products_page_slb_price"));

        sa.assertAll();
    }

    @Test (description = "Validate the Sauce Labs Backpack title, text and price on product details page")
    public void validateProductOnProductDetailsPage() {
        productDetailsPage = productsPage.pressSLBTitle();

        String SLBTitle = productDetailsPage.getSLBTitle();
        String SLBText = productDetailsPage.getSLBTxt();
        String SLBPrice = productDetailsPage.scrollToSLBPriceAndGetSLBPrice();

        SoftAssert sa = new SoftAssert();
        sa.assertEquals(SLBTitle, getStringsMap().get("product_details_page_slb_title"));
        sa.assertEquals(SLBText, getStringsMap().get("product_details_page_slb_txt"));
        sa.assertEquals(SLBPrice, getStringsMap().get("product_details_page_slb_price"));

        productsPage = productDetailsPage.pressBackToProductsBtn(); //navigate back to products list
        sa.assertAll();
    }
}
