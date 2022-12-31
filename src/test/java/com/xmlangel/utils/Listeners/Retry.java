package com.xmlangel.utils.Listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.xmlangel.base.InitialProcess;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import static com.xmlangel.utils.Listeners.TestListenersApp.extentTest;

public class Retry implements IRetryAnalyzer {
    private int count = 0;
    private static int maxTry = 2; //Run the failed test 2 times

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {                     //Check if test not succeed
            if (count < maxTry) {                           //Check if maxTry count is reached
                count++;                                    //Increase the maxTry count by 1
                iTestResult.setStatus(ITestResult.FAILURE); //Mark test as failed and take base64Screenshot
                extendReportsFailOperations(iTestResult);   //ExtentReports fail operations
                return true;                                //Tells TestNG to re-run the test
            }
        } else {
            iTestResult.setStatus(ITestResult.SUCCESS);     //If test passes, TestNG marks it as passed
        }
        return false;
    }

    public void extendReportsFailOperations(ITestResult result) {
        Object testClass = result.getInstance();
        AppiumDriver driver = ((InitialProcess) result.getInstance()).driver;
        extentTest.get().fail("<b><font color=red>" + "Screenshot of failure" + "</font></b></summary>",
                MediaEntityBuilder.createScreenCaptureFromBase64String(getScreenshotAs((TakesScreenshot) driver)).build());
    }

    private static String getScreenshotAs(TakesScreenshot driver) {
        return driver.getScreenshotAs(OutputType.BASE64);
    }
}