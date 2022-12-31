package com.xmlangel.test;

import com.xmlangel.base.InitialProcess;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
@Log4j
public class AndroidCreateSessionTest extends InitialProcess {

    @BeforeClass
    public void beforeClass(ITestContext context){
        context.setAttribute("caseName", "액티비티 확인 Android1 ");
    }

    @Test()
    public void testCreateSession(ITestContext context) {
        String activity = ((AndroidDriver) driver).currentActivity();
        String pkg = ((AndroidDriver) driver).getCurrentPackage();
        Assert.assertEquals(activity, ".MainActivity");
        Assert.assertEquals(pkg, "com.google.samples.apps.nowinandroid");
    }
}
