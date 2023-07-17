package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import com.itkhanz.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class MenuPage extends BaseTest {
    TestUtils testUtils = new TestUtils();
    @AndroidFindBy(xpath="//android.view.ViewGroup[@content-desc='test-Menu']/android.view.ViewGroup/android.widget.ImageView")
    @iOSXCUITFindBy(xpath="//XCUIElementTypeOther[@name='test-Menu']/XCUIElementTypeOther")
    private WebElement settingsBtn;

    public SettingsPage pressSettingsBtn() {
        testUtils.log().info("press Settings button");
        click(settingsBtn);
        return new SettingsPage();
    }
}
