package com.itkhanz.pages.saucelabs;

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
        click(logoutBtn, "press logout button");
        return new LoginPage();
    }
}
