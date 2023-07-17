package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import com.itkhanz.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class SettingsPage extends BaseTest {
    TestUtils testUtils = new TestUtils();
    @AndroidFindBy(accessibility="test-LOGOUT")
    @iOSXCUITFindBy(id = "test-LOGOUT")
    private WebElement logoutBtn;

    public LoginPage pressLogoutBtn() {
        testUtils.log().info("press logout button");
        click(logoutBtn);
        return new LoginPage();
    }
}
