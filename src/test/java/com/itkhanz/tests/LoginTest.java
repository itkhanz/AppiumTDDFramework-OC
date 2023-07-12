package com.itkhanz.tests;

import com.itkhanz.BaseTest;
import com.itkhanz.pages.LoginPage;
import com.itkhanz.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class LoginTest extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;

    @BeforeMethod
    public void beforeMethod(Method m) {
        System.out.println("\n" + "****** starting test:" + m.getName() + "******" + "\n");
        loginPage = new LoginPage();
    }

    @Test
    public void invalidUsernameTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName("invalidusername");
        loginPage.enterPassword("secret_sauce");
        loginPage.pressLoginBtn();

        String actualErrTxt = loginPage.getErrTxt();
        String expectedErrTxt = "Username and password do not match any user in this service.";
        System.out.println("actual error text - " + actualErrTxt + "\n" + "expected error text - " + expectedErrTxt);

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test
    public void invalidPasswordTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName("standard_user");
        loginPage.enterPassword("invalidpassword");
        loginPage.pressLoginBtn();

        String actualErrTxt = loginPage.getErrTxt();
        String expectedErrTxt = "Username and password do not match any user in this service.";
        System.out.println("actual error text - " + actualErrTxt + "\n" + "expected error text - " + expectedErrTxt);

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test
    public void successfulLoginTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName("standard_user");
        loginPage.enterPassword("secret_sauce");
        productsPage = loginPage.pressLoginBtn();

        String actualProductTitle = productsPage.getTitle();
        String expectedProductTitle = "PRODUCTS";
        System.out.println("actual product title - " + actualProductTitle + "\n" + "expected title - " + expectedProductTitle);

        Assert.assertEquals(actualProductTitle, expectedProductTitle);
    }
}
