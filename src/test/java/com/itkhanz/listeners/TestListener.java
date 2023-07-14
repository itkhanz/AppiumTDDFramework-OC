package com.itkhanz.listeners;

import com.itkhanz.BaseTest;
import com.itkhanz.utils.TestUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        ITestListener.super.onTestStart(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ITestListener.super.onTestSuccess(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        //This will help to get the error stack trace to testng results to read the error cause
        //Check if there is an exception thrown by test result
        if (result.getThrowable() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);  //prints the entire stacktrace to testng results
            System.out.println(sw.toString());  //prints the stacktrace to console
        }

        //Screenshot capture and save to formatted imagePath
        Map<String, String> testParams = new HashMap<String, String>();
        testParams = result.getTestContext().getCurrentXmlTest().getAllParameters();

        String imagePath =  "media" + File.separator
                            + testParams.get("platformName") + "_" + testParams.get("deviceName") + File.separator
                            + "screenshots" + File.separator
                            + TestUtils.getFormattedDateTime() + File.separator
                            + result.getTestClass().getRealClass().getSimpleName() + File.separator
                            + result.getName() + ".png";


        //String completeImagePath = System.getProperty("user.dir") + File.separator + imagePath;
        String completeImagePath = Paths.get(imagePath).toAbsolutePath().toString();

        BaseTest base = new BaseTest();
        File file = base.getDriver().getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(file, new File(imagePath));
            Reporter.log("Attaching the captured screenshot to HTML Report");
            Reporter.log("<a href='"+ completeImagePath + "'> <img src='"+ completeImagePath + "' th:src='"+ completeImagePath + "' height='400' width='150'/> </a>");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ITestListener.super.onTestSkipped(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
    }

    @Override
    public void onStart(ITestContext context) {
        ITestListener.super.onStart(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        ITestListener.super.onFinish(context);
    }
}
