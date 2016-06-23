package com.sharedmeals.selenium.rules;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.sharedmeals.selenium.DriverBase;

public class ScreenshotOnExceptionRule implements MethodRule {
	private final static String SCREENSHOT_DIRECTORY = "target/failsafe-reports";

	@Override
	public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					statement.evaluate();
				} catch (Throwable t) {
					captureScreenshot(frameworkMethod.getName());
					throw t;	// rethrow the exception so JUnit can report it as an error
				}
			}
			
			public void captureScreenshot(String fileName) {
				try {
					new File(SCREENSHOT_DIRECTORY + "/").mkdirs();	// make sure the directory exists
					FileOutputStream out = new FileOutputStream(SCREENSHOT_DIRECTORY + "/screenshot-" + fileName + ".png");
					out.write(((TakesScreenshot) DriverBase.getDriver()).getScreenshotAs(OutputType.BYTES));
					out.close();
				} catch (Exception e) {
					// Ignore - No need to crash the test if the screenshot fails
				}
			}
		};
	}

}
