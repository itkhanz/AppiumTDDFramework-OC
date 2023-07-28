package com.itkhanz.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ExtentManager {
    static ExtentReports extentReports;
    final static String reportFilePath = "test-report" + File.separator + "extent" + File.separator + "extent.html";
    static Map<Long, ExtentTest> extentTestMap = new HashMap<>();

    public synchronized static ExtentReports getReporter() {
        if (extentReports == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
            spark.config().setDocumentTitle("Appium TDD Framework");
            spark.config().setReportName("Sauce Labs Demo App");
            spark.config().setTheme(Theme.DARK);
            extentReports = new ExtentReports();
            extentReports.attachReporter(spark);
        }
        return extentReports;
    }

    public static ExtentTest getTest() {
        return extentTestMap.get(Thread.currentThread().getId());
    }

    public static synchronized ExtentTest startTest(String testName, String testDesc) {
        ExtentTest extentTest = getReporter().createTest(testName, testDesc);
        extentTestMap.put(Thread.currentThread().getId(), extentTest);
        return extentTest;
    }

}
