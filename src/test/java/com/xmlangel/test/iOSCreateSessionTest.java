package com.xmlangel.test;

import com.xmlangel.base.InitialProcess;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.xmlangel.utils.Listeners.TestListenersApp.setMessage;
@Log4j
public class iOSCreateSessionTest extends InitialProcess {

    @BeforeTest
    public void beforeTest(ITestContext context){
        context.setAttribute("caseName", "어플리케이션 이름 확인 iOS");
    }
    @AfterSuite
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testCreateSession (ITestContext context) {
        // Check that the XCUIElementTypeApplication was what we expect it to be
        WebElement applicationElement = driver.findElement(By.className("XCUIElementTypeApplication"));
        String applicationName = applicationElement.getAttribute("name");
        setMessage("어플리케이션 이름확인");
        Assert.assertEquals(applicationName, "dev");
    }



}
