package com.xmlangel.base;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class TestClassUsingListeners {
    public WebDriver driver;
    //Properties
    public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static final String WEB_DRIVER_PATH = "./chromedriver";


    @Test
    public void f7() {
        System.out.println("성공");
    }

    @Test
    public void f8() {
        Assert.fail("Executing Failed Test Method ");
    }

    @Test
    public void f9() {
        throw new SkipException("Executing Skipped Test Method");
    }

    @BeforeClass
    public void beforeClass() {
        ChromeOptions options = new ChromeOptions();
        //페이지가 로드될 때까지 대기
        //Normal: 로드 이벤트 실행이 반환 될 때 까지 기다린다.
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        //System Property SetUp
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        driver = new ChromeDriver(options);
        driver.get("https://xmlangel.github.io");
    }

    @AfterClass
    public void afterTest() {
        driver.quit();
    }
}
