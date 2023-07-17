package com.itkhanz.tests;

import com.itkhanz.BaseTest;
import com.itkhanz.constants.Constants;
import com.itkhanz.pages.LoginPage;
import com.itkhanz.pages.ProductDetailsPage;
import com.itkhanz.pages.ProductsPage;
import com.itkhanz.pages.SettingsPage;
import com.itkhanz.utils.TestUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import javax.swing.plaf.PanelUI;
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
        closeApp();
        launchApp();
        testUtils.log().info("****** starting test:" + m.getName() + "******");
        loginPage = new LoginPage();
        productsPage = loginPage.login(
                loginUsersObject.getJSONObject("validUser").getString("username"),
                loginUsersObject.getJSONObject("validUser").getString("password")
        );
    }

    @AfterMethod
    public void afterMethod() {
        testUtils.log().info(".....ProductTest after method.....");
        settingsPage = productsPage.pressSettingsBtn(); //open the sidebar
        loginPage = settingsPage.pressLogoutBtn();  //click on logout button in sidebar
    }

    @Test
    public void validateProductOnProductsPage() {
        String SLBTitle = productsPage.getSLBTitle();
        String SLBPrice = productsPage.getSLBPrice();

        SoftAssert sa = new SoftAssert();
        sa.assertEquals(SLBTitle, getStringsMap().get("products_page_slb_title"));
        sa.assertEquals(SLBPrice, getStringsMap().get("products_page_slb_price"));

        sa.assertAll();
    }

    @Test
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
