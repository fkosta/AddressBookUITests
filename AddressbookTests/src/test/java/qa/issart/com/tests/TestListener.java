package qa.issart.com.tests;

import io.qameta.allure.Attachment;
import org.testng.ITestListener;
import org.testng.ITestResult;
import qa.issart.com.helpers.ApplicationManager;

public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult testResult){
        ApplicationManager app = (ApplicationManager) testResult.getTestContext().getAttribute("app");
        app.takeScreensot();
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshot(byte[] screenShot){
        return screenShot;
    }
}
