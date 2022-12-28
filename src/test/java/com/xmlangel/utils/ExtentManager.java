package com.xmlangel.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.util.Date;

public class ExtentManager {
    private static ExtentReports extent = new ExtentReports();

    /**
     * Extents Report 생성
     * @return
     */
    public static ExtentReports createInstance() {
        String fileName = getReportName();
        String directory = System.getProperty("user.dir") + "/reports/";
        new File(directory).mkdirs();
        String path = directory + fileName;

        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setEncoding("utf-8");
        reporter.config().setTheme(Theme.STANDARD);
        reporter.config().setDocumentTitle("Socar Automation");
        reporter.config().setReportName("Android Test Results");

        extent.attachReporter(reporter);

        return extent;
    }

    private static String getReportName() {
        Date d = new Date();
        String fimeName = "AutomationReport" + "_" + d.toString().replace(":", "_").replace(" ", "_" + ".html");
        return fimeName;

    }

}
