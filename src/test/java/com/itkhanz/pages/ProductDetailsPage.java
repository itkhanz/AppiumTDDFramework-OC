package com.itkhanz.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.HashMap;

public class ProductDetailsPage extends MenuPage{
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='test-Description']/android.widget.TextView[1]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name='test-Description']/child::XCUIElementTypeStaticText[1]")
    private WebElement SLBTitle;

    @AndroidFindBy (xpath = "//android.view.ViewGroup[@content-desc='test-Description']/android.widget.TextView[2]")
    @iOSXCUITFindBy (xpath = "//XCUIElementTypeOther[@name='test-Description']/child::XCUIElementTypeStaticText[2]")
    private WebElement SLBTxt;

    @AndroidFindBy (accessibility = "test-BACK TO PRODUCTS")
    @iOSXCUITFindBy (id = "test-BACK TO PRODUCTS")
    private WebElement backToProductsBtn;

    @iOSXCUITFindBy(accessibility = "test-Price")
    private WebElement SLBPrice;

    /*
    This will scroll to the price using UiScrollable and return WebElement
     */
    private WebElement AndroidScrollToSLBPrice() {
        return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))." +
                        "scrollIntoView(" +
                            "new UiSelector().description(\"test-Price\")" +
                        ");")
        );
    }

    private void iOSScrollToSLBPrice() {

        String elementID = ((RemoteWebElement)SLBPrice).getId();
        HashMap<String, Object> scrollMap = new HashMap<>();
        scrollMap.put("element", elementID);

        //First way
        //use either mobile: scroll scrolls the element or the whole screen. Use either 'name', 'direction', 'predicateString' or 'toVisible'
        //In case of name, and predicateString strategies, make sure to provide the elementID of a parent element unlike the same element
        /*scrollMap.put("toVisible", true);
        driver.executeScript("mobile: scroll", scrollMap);*/

        //Second way
        //or use mobile: scrollToElement which scrolls the current viewport to the given element.
        driver.executeScript("mobile: scrollToElement", scrollMap);
    }

    public String getSLBTitle() {
        String title = getLabelText(SLBTitle);
        System.out.println("SLB title on DetailsPage is: " + title);
        return title;
    }

    public String getSLBTxt() {
        String txt = getLabelText(SLBTxt);
        System.out.println("SLB text on DetailsPage is: " + txt);
        return txt;
    }

    public ProductsPage pressBackToProductsBtn() {
        click(backToProductsBtn);
        System.out.println("Navigating back to products page");
        return new ProductsPage();
    }

    public String scrollToSLBPriceAndGetSLBPrice() {
        //TODO use W3C Actions to implement platform independent scrolling
        switch (platform) {
            case "Android" -> {
                return getLabelText(AndroidScrollToSLBPrice());
            }
            case "iOS" -> {
                iOSScrollToSLBPrice();
                return getLabelText(SLBPrice);
            }
            default -> throw new RuntimeException("Inavlid platform! - " + platform);
        }
    }
}
