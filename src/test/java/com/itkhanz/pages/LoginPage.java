package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.commons.logging.Log;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class LoginPage{

    //TODO initialize the driver from driver factory in test case, and pass it to login page which extends from base page and pass the driver to base page in constructor
    @AndroidFindBy(accessibility = "test-Username")
    private WebElement usernameTxtFld;

    @AndroidFindBy (accessibility = "test-Password")
    private WebElement passwordTxtFld;

    @AndroidFindBy (accessibility = "test-LOGIN")
    private WebElement loginBtn;

    @AndroidFindBy (xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView")
    private WebElement errTxt;

    BaseTest base;
    public LoginPage() {
        base = new BaseTest();
        PageFactory.initElements(new AppiumFieldDecorator(base.getDriver()),this);
    }

    public LoginPage enterUserName(String username) {
        base.clear(usernameTxtFld);
        base.sendKeys(usernameTxtFld, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        base.clear(passwordTxtFld);
        base.sendKeys(passwordTxtFld, password);
        return this;
    }

    public ProductsPage pressLoginBtn() {
        base.click(loginBtn);
        return new ProductsPage();
    }

    public String getErrTxt() {
        return base.getAttribute(errTxt, "text");
    }
}
