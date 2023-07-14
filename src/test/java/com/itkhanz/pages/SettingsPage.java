package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class SettingsPage extends BaseTest {
    @AndroidFindBy(accessibility="test-LOGOUT")
    @iOSXCUITFindBy(id = "test-LOGOUT")
    private WebElement logoutBtn;

    public LoginPage pressLogoutBtn() {
        System.out.println("press logout button");
        click(logoutBtn);
        return new LoginPage();
    }
}
