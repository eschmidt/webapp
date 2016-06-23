package com.sharedmeals.selenium;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;
//import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

import com.sharedmeals.selenium.config.DriverFactory;
import com.sharedmeals.selenium.rules.ScreenshotOnExceptionRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriverBase {
	
    private static List<DriverFactory> webDriverThreadPool = Collections.synchronizedList(new ArrayList<DriverFactory>());
    private static ThreadLocal<DriverFactory> driverFactory;

    @Rule
    public ScreenshotOnExceptionRule screenshotRule = new ScreenshotOnExceptionRule();
    
    @BeforeClass
    public static void instantiateDriverObject() {
    	driverFactory = new ThreadLocal<DriverFactory>() {
            @Override
            protected DriverFactory initialValue() {
                DriverFactory driverFactory = new DriverFactory();
                webDriverThreadPool.add(driverFactory);
                return driverFactory;
            }
        };
    }

    public static WebDriver getDriver() throws Exception {
    	return driverFactory.get().getDriver();
    }

    @After
    public void clearCookies() throws Exception {
        getDriver().manage().deleteAllCookies();
    }

    @AfterClass
    public static void closeDriverObjects() {
        for (DriverFactory driverFactory : webDriverThreadPool) {
            driverFactory.quitDriver();
        }
    }
}