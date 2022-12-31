package com.xmlangel.base;

import com.xmlangel.utils.Listeners.TestListenersApp;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static com.xmlangel.utils.ExtentManager.getPath;

@Log4j
@Listeners(TestListenersApp.class)
public abstract class InitialProcess {

    private static AppiumDriverLocalService service;
    public static AppiumDriver driver;

    public static String getPlatformName() {
        return platformName;
    }

    public static void setPlatformName(String platformName) {
        InitialProcess.platformName = platformName;
    }

    public static String platformName;

    @BeforeSuite
    @Parameters("platformName")
    public void globalSetup(@Optional("platformName") String platformName) throws IOException {
        setPlatformName(platformName);
        appiumServiceStart();
        if (platformName.equalsIgnoreCase("Android")) {
            driver = startAppium_Android();
        } else {
            driver = startAppium_IOS();
        }
    }

    /**
     * appiumLocalServer 시작
     * 멀티쓰레드는 지원안함.(2.0 버전에서는 지원 예정이라고함)
     * 멀티쓰레드를 사용하려면 appium을 별도로 띄워서 하는 방법을 이용해야함.\n
     * 기본적으로 '/ '로 되어있는데 1.0버전에는 appium 주소가 '/wd/hub/' 로되어있어서 주소 변경함.
     */
    private static void appiumServiceStart() {
        service = AppiumDriverLocalService.buildDefaultService().withBasePath("/wd/hub/");
        service.start();
    }

    /*
     * Appiunm iOS DesiredCapabilities 설정
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

    /*
     * Appiunm Androd  DesiredCapabilities 설정
     */
    public static AndroidDriver startAppium_Android() throws IOException {
        File appDir = getAppDir();
        DesiredCapabilities cap = new DesiredCapabilities();
        File app = new File(appDir.getCanonicalPath(), "test.apk");
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, "12");
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, "R3CR70W9QCL");
        cap.setCapability("app", app.getAbsolutePath());
        cap.setCapability("appPackage", "com.google.samples.apps.nowinandroid"); //Android
        cap.setCapability("appActivity", "com.google.samples.apps.nowinandroid.MainActivity"); //Android
        cap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 500); //Android
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2); //Android

        driver = new AndroidDriver(getServiceUrl(), cap);
        //Returning the instance of the driver to the parent method
        return (AndroidDriver) driver;
    }

    /**
     * apps 디렉토리 경로
     *
     * @return
     */
    private static File getAppDir() {
        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "./apps");
        return appDir;
    }

    @AfterSuite
    @Parameters("reportOpen")
    public void globalTearDown(@Optional("reportOpen") boolean reportOpen) {
        driver.quit();
        if (service != null) {
            service.stop();
        }
        openReport(reportOpen);
    }

    /**
     * 디버그를 하거나 Local에서 실행시 Extent Reoprt 를 테스트가 끝나고 자동으로 열어서 확인 할수 있게함
     * 파일이 시간순으ㄹ확인하려는 용도로 만듬..
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

    public static URL getServiceUrl() {
        return service.getUrl();
    }

}
