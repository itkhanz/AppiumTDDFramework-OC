package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ProductsPage {
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='test-Cart drop zone']//android.widget.TextView")
    private WebElement productTitleTxt;

    BaseTest base;
    public ProductsPage() {
        base = new BaseTest();
        PageFactory.initElements(new AppiumFieldDecorator(base.getDriver()),this);
    }

    public String getTitle() {
        return base.getAttribute(productTitleTxt, "text");
    }
}
