package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import com.itkhanz.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class ProductsPage extends MenuPage {
    TestUtils testUtils = new TestUtils();
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='test-Cart drop zone']//android.widget.TextView")
    @iOSXCUITFindBy(xpath ="//XCUIElementTypeOther[@name='test-Toggle']/parent::*[1]/preceding-sibling::*[1]")
    private WebElement productTitleTxt;

    @AndroidFindBy (xpath = "(//android.widget.TextView[@content-desc='test-Item title'])[1]")
    @iOSXCUITFindBy (xpath = "(//XCUIElementTypeStaticText[@name='test-Item title'])[1]")
    private WebElement SLBTitle;

    @AndroidFindBy (xpath = "(//android.widget.TextView[@content-desc='test-Price'])[1]")
    @iOSXCUITFindBy (xpath = "(//XCUIElementTypeStaticText[@name='test-Price'])[1]")
    private WebElement SLBPrice;

    public String getTitle() {
        //return getAttribute(productTitleTxt, "text");
        String title =  getLabelText(productTitleTxt);
        testUtils.log("product page title is - " + title);
        return title;
    }

    public String getSLBTitle() {
        String title = getLabelText(SLBTitle);
        testUtils.log("SLB title is - " + title);
        return title;
    }

    public String getSLBPrice() {
        String price = getLabelText(SLBPrice);
        testUtils.log("SLB price is - " + price);
        return price;
    }

    public ProductDetailsPage pressSLBTitle() {
        click(SLBTitle);
        testUtils.log("Navigating to SLB Product Details page");
        return new ProductDetailsPage();
    }
}
