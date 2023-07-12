package com.itkhanz.pages;

import com.itkhanz.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class ProductsPage extends BaseTest {
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='test-Cart drop zone']//android.widget.TextView")
    private WebElement productTitleTxt;

    public String getTitle() {
        return getAttribute(productTitleTxt, "text");
    }
}
