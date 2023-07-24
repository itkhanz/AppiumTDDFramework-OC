package com.itkhanz.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.itkhanz.BaseTest;
import com.itkhanz.reports.ExtentManager;
import com.itkhanz.utils.TestUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestListener implements ITestListener {
    TestUtils testUtils = new TestUtils();

    @Override
    public void onStart(ITestContext context) {
        ITestListener.super.onStart(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getReporter().flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        //This method is called from the BaseTest @BeforeMethod now
        //ITestListener.super.onTestStart(result);
        /*BaseTest base = new BaseTest();
        ExtentManager.startTest(result.getName(),result.getMethod().getDescription())
                .assignCategory(base.getPlatform() + "_" + base.getDevice())
                .assignAuthor("itkhanz")
        ;*/
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.getTest().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        //This will help to get the error stack trace to testng results to read the error cause
        //Check if there is an exception thrown by test result
        if (result.getThrowable() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);  //prints the entire stacktrace to testng results
            testUtils.log().info(sw.toString());  //prints the stacktrace to console
        }

        //Screenshot capture and save to formatted imagePath
        Map<String, String> testParams = new HashMap<String, String>();
        testParams = result.getTestContext().getCurrentXmlTest().getAllParameters();

        BaseTest base = new BaseTest();
        //If set here, then this will only be set for failed test cases
        //base.setDateTime(TestUtils.getFormattedDateTime());

        String imagePath =  "media" + File.separator
                            + testParams.get("platformName") + "_" + testParams.get("deviceName") + File.separator
                            + "screenshots" + File.separator
                            + base.getDateTime() + File.separator
                            + result.getTestClass().getRealClass().getSimpleName() + File.separator
                            + result.getName() + ".png";


        //String completeImagePath = System.getProperty("user.dir") + File.separator + imagePath;
        String completeImagePath = Paths.get(imagePath).toAbsolutePath().toString();

        File file = base.getDriver().getScreenshotAs(OutputType.FILE);

        //save the file to disk
        try {
            FileUtils.copyFile(file, new File(imagePath));
            Reporter.log("Attaching the captured screenshot to HTML Report");
            Reporter.log("<a href='"+ completeImagePath + "'> <img src='"+ completeImagePath + "' th:src='"+ completeImagePath + "' height='400' width='150'/> </a>");
        } catch (IOException e) {
            e.printStackTrace();
            testUtils.log().info("Failed to save the screenshot");
        }

        //convert the file to base64 string and embed in report
        byte[] encodedImage = null;
        try {
            encodedImage = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
            ExtentManager.getTest().fail("Test Failed",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(
                                    new String(encodedImage, StandardCharsets.UTF_8),
                                    result.getName())
                            .build()
            );
        } catch (IOException e1) {
            System.out.println("Failed to convert the file to base64");
            e1.printStackTrace();
        }
        //ExtentManager.getTest().log(Status.FAIL, "Test Failed");
        ExtentManager.getTest().fail(result.getThrowable());    //prints the stack trace to report
    }


    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
    }
}
