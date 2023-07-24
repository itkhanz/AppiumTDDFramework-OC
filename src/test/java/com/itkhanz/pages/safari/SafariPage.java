package com.itkhanz.pages.safari;

import com.aventstack.extentreports.Status;
import com.itkhanz.BaseTest;
import com.itkhanz.reports.ExtentManager;
import com.itkhanz.utils.TestUtils;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;


public class SafariPage extends BaseTest {
    TestUtils testUtils = new TestUtils();

    @iOSXCUITFindBy(accessibility = "CapsuleNavigationBar?isSelected=true")
    private WebElement urlBtn;

    @iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeButton' && (name CONTAINS 'Öffnen' || name CONTAINS 'Open')")
    private WebElement openBtn;

    public void openAppWithDeepLink(String url) {
        testUtils.log().info("opening Safari browser");
        ExtentManager.getTest().log(Status.INFO, "opening Safari browser");
        ((InteractsWithApps) getDriver()).activateApp("com.apple.mobilesafari");
        click(urlBtn, "Click on Url field inside browser");
        sendKeys(urlBtn, "" + url + "\uE007", "typing into url field: " + "" + url + "\uE007");
        click(openBtn, "click on the popup to open the app");
    }

}
