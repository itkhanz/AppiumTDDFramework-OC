package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class LoginPage extends BaseTest {

    //TODO initialize the driver from driver factory in test case, and pass it to login page which extends from base page and pass the driver to base page in constructor
    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(id = "test-Username")
    private WebElement usernameTxtFld;

    @AndroidFindBy (accessibility = "test-Password")
    @iOSXCUITFindBy (id = "test-Password")
    private WebElement passwordTxtFld;

    @AndroidFindBy (accessibility = "test-LOGIN")
    @iOSXCUITFindBy (id = "test-LOGIN")
    private WebElement loginBtn;

    @AndroidFindBy (xpath = "//android.view.ViewGroup[@content-desc='test-Error message']/android.widget.TextView")
    @iOSXCUITFindBy (xpath = "//XCUIElementTypeOther[@name='test-Error message']/child::XCUIElementTypeStaticText")
    private WebElement errTxt;

    public LoginPage enterUserName(String username) {
        clear(usernameTxtFld);  //iOS may not clear the text input field automatically when entering text
        sendKeys(usernameTxtFld, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        clear(passwordTxtFld);
        sendKeys(passwordTxtFld, password);
        return this;
    }

    public ProductsPage pressLoginBtn() {
        click(loginBtn);
        return new ProductsPage();
    }

    public String getErrTxt() {
        //since text attribute does not exist for iOS, so we wrote a custom method to return text or label based on platform
        //return getAttribute(errTxt, "text");
        return getLabelText(errTxt);
    }
}
