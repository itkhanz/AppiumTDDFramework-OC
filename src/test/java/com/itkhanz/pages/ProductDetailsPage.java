package com.itkhanz.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

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
}
