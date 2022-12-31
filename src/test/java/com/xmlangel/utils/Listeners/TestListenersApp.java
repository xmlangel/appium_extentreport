package com.xmlangel.utils.Listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.xmlangel.base.InitialProcess;
import com.xmlangel.utils.ExtentManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.w3c.dom.Element;

import javax.lang.model.util.Elements;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
@Log4j
public class TestListenersApp implements ITestListener {
    private static ExtentReports extent ;
    public static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();//멀티쓰레드를 위해 사용함 패러럴로 실행할때 쓰래드를 분리해서 사용. 리포트파일도 여러 쓰레드에서 생성됨. extentTest.get().pass 와같이 호출해야함.

    @Override
    public void onTestStart(ITestResult result) {
        log.info("시작");
        extent = ExtentManager.createInstance();
        String testcase = (String) result.getTestContext().getAttribute("caseName");
        ExtentTest test = extent.createTest(result.getMethod().getMethodName() + "::" + testcase);
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        AppiumDriver driver = ((InitialProcess) result.getInstance()).driver;
        String path = getPath(methodName, driver);

        try {
            extentTest.get().pass("<b><font color=green>" + "Screenshot of Success" + "</font></b></summary>", MediaEntityBuilder.createScreenCaptureFromBase64String(path).build());
        } catch (Exception e) {
            extentTest.get().pass("Test SuccessFailed, cannot attach screenshot");
        }
        String longText = "<b>Test Method " + methodName + " Sucessful</b>";
        Markup m = MarkupHelper.createLabel(longText, ExtentColor.GREEN);
        extentTest.get().log(Status.PASS, m);

    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String platformName = (String) result.getTestContext().getAttribute("platformName");
        String exceptionMessage = Arrays.toString(result.getThrowable().getStackTrace());
        String exceptionText = Arrays.toString((result.getThrowable().getMessage()).toCharArray());

        AppiumDriver driver = ((InitialProcess) result.getInstance()).driver;

        extentTest.get().fail("<details><summary><b><font color=red>Exception Occured, click to see details :  "
                + exceptionText.replace(",", "").replace("[", "").replace("]", "")
                + "</font></b></summary>" + exceptionMessage.replaceAll(",", "<br>") + "</details> \n");
        String path = getPath(methodName, driver);
        try {
            extentTest.get().fail("<b><font color=red>" + "Screenshot of failure" + "</font></b></summary>", MediaEntityBuilder.createScreenCaptureFromBase64String(path).build());
        } catch (Exception e) {
            extentTest.get().fail("Test Failed, cannot attach screenshot");
        }
        String longText = "<b>Test Method " + methodName + " Failed</b>";
        Markup m = MarkupHelper.createLabel(longText, ExtentColor.RED);
        extentTest.get().log(Status.FAIL, m);

    }


    private String getPath(String methodName,  AppiumDriver driver) {
        String path = takeScreenshotToFile(driver, methodName, "base64");
        return path;
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String longText = "<b>Test Method " + methodName + "Skipped</b>";
        Markup m = MarkupHelper.createLabel(longText, ExtentColor.YELLOW);
        extentTest.get().log(Status.SKIP, m);

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }

    }

    /**
     * 스크린샷을 찍어준다.
     *
     * @param driver
     * @param methodName
     * @param type       file",base64
     * @return
     */
    public String takeScreenshotToFile(AppiumDriver driver, String methodName, String type) {
        switch (type) {
            case ("file"):
                String fimeName = getScreenshotName(methodName);
                String directory = System.getProperty("user.dir") + "/screenshots/";
                new File(directory).mkdirs();
                String path = directory + fimeName;
                try {
                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(screenshot, new File(path));
                    System.out.println("*****************************");
                    System.out.println("Screenshot" + path);
                    System.out.println("*****************************");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return path;
            case ("base64"):
                return takeScreenshotToBase64(driver);
            default:
                return takeScreenshotToBase64(driver);
        }

    }



    public String takeScreenshotToBase64(AppiumDriver driver) {
        String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        return screenshot;
    }

    public static String getScreenshotName(String methodName) {
        Date d = new Date();
        String fimeName = methodName + "_" + d.toString().replace(":", "_").replace(" ", "_") + ".png";
        return fimeName;
    }

    public static void setMessage(String message) {
        extentTest.get().log(Status.INFO,message);
    }

}
