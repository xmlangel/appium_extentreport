package com.xmlangel.base;

import com.xmlangel.utils.Listeners.TestListenersApp;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.screenrecording.BaseScreenRecordingOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.xmlangel.utils.AppiumRunner.*;
import static com.xmlangel.utils.ExtentManager.getPath;

@Log4j
@Listeners(TestListenersApp.class)
public abstract class InitialProcess  extends BaseScreenRecordingOptions{

    public static AppiumDriver driver;

    public static String getPlatformName() {
        return platformName;
    }

    public static void setPlatformName(String platformName) {
        InitialProcess.platformName = platformName;
    }

    public static String platformName;

    public static int appiumPort=4723;

    public static AppiumDriverLocalService service;
    private static final String LOG_FILE_PATH = "/logs/appiumNode.log";

    @BeforeSuite
    @Parameters("platformName")
    public void globalSetup(@Optional("platformName") String platformName) throws IOException, InterruptedException {
        setPlatformName(platformName);
        startAppiumService(appiumPort);

        if (platformName.equalsIgnoreCase("Android")) {
            driver = startAppium_Android();
        } else {
            driver = startAppium_IOS();
        }
    }

    /**
     * Appiunm iOS DesiredCapabilities 설정
     *
     * Setting DesiredCapabilities for Appium iOS
     */
    public static IOSDriver startAppium_IOS() throws IOException {
        File appDir = getAppDir();
        File app = new File(appDir.getCanonicalPath(), "test.ipa");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "15.5");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 12 Mini");
        capabilities.setCapability(MobileCapabilityType.UDID, "00008101-000968121EF0001E");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("bundleId", "");
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.IOS_XCUI_TEST);
        driver = new IOSDriver(getServiceUrl(), capabilities);


        return (IOSDriver) driver;
    }

    /**
     * Appiunm Androd  DesiredCapabilities 설정
     *
     * Setting DesiredCapabilities for Appium Android
     */
    public static AndroidDriver startAppium_Android() throws IOException {
        File appDir = getAppDir();
        DesiredCapabilities cap = new DesiredCapabilities();
        File app = new File(appDir.getCanonicalPath(), "test.apk");
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, "13");
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, "R3CN10ACSJH");
        cap.setCapability("app", app.getAbsolutePath());
        cap.setCapability("appPackage", "com.google.samples.apps.nowinandroid"); //Android
        cap.setCapability("appActivity", "com.google.samples.apps.nowinandroid.MainActivity"); //Android
        cap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 500); //Android
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2); //Android
        log.info("AppiumURL : " + getServiceUrl());
        driver = new AndroidDriver(getServiceUrl(), cap);
        //Returning the instance of the driver to the parent method
        return (AndroidDriver) driver;
    }

    /**
     * apps 디렉토리 경로
     *
     * the directory path for 'apps'
     * @return
     */
    private static File getAppDir() {
        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "./apps");
        return appDir;
    }

    @AfterSuite
    @Parameters("reportOpen")
    public void globalTearDown(@Optional("reportOpen") boolean reportOpen) throws IOException, InterruptedException {
        log.info("After-Suite");
        driver.quit();
        stopAppiumService(appiumPort);
        openReport(reportOpen);
    }

    /**
     * 디버그를 하거나 Local에서 실행시 Extent Reoprt 를 테스트가 끝나고 자동으로 열어서 확인 할수 있게함
     * 파일이 시간순으로 확인 하려는 용도로 만듬..
     *
     * When debugging or running locally, automatically open Extent Report to check it after the test is finished.
     * Created for the purpose of checking files in chronological order.
     *
     * @param reportOpen
     */
    private static void openReport(boolean reportOpen) {
        log.info("reportOpen = " + reportOpen);
        if (reportOpen) {
            String[] cmd = {"open", getPath()};
            ProcessBuilder probuilder = new ProcessBuilder(cmd);
            Process p = null;
            try {
                p = probuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String readline;
                int i = 0;
                while ((readline = reader.readLine()) != null) {
                    log.info(++i + " " + readline);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Appium을 시작해준다.
     * 만약 실행되고 있으면 종료시켜주고 실행한다.
     *
     * @throws IOException
     */
    public static void startAppiumService(int appiumPort) throws IOException, InterruptedException {
        File logFile = new File(System.getProperty("user.dir") + LOG_FILE_PATH);
        PrintStream fileOutputStream = new PrintStream(logFile);
        log.info(fileOutputStream);

        service = new AppiumServiceBuilder()
                .usingPort(appiumPort)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "debug")
                .build();

        service.start();
        log.info("Appium Server started " + service.getUrl());
    }

    public static URL getServiceUrl() {
        return service.getUrl();
    }

    /**
     * Appium 을 종료시켜준다.
     * 실행 중인 Appium 서버 프로세스 ID를 찾아서 Appium 서버 프로세스를 종료합니다.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void stopAppiumService(int appiumPort) throws IOException, InterruptedException {
        if (service != null) {
            service.stop();
        }
    }
}
