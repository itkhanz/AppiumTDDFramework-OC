package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class ProductsPage extends BaseTest {
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='test-Cart drop zone']//android.widget.TextView")
    @iOSXCUITFindBy(xpath ="//XCUIElementTypeOther[@name='test-Toggle']/parent::*[1]/preceding-sibling::*[1]")
    private WebElement productTitleTxt;

    public String getTitle() {
        //return getAttribute(productTitleTxt, "text");
        return getLabelText(productTitleTxt);
    }
}
