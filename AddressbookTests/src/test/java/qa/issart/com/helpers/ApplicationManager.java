package qa.issart.com.helpers;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ApplicationManager {
    private WebDriver wD;
    private String browser;
    private ContactHelper contactHelper;
    private GroupHelper groupHelper;
    private SessionHelper sessionHelper;
    private NavigationHelper navigationHelper;
    private final Properties properties;

    public ApplicationManager(String browser) {
        this.browser = browser;
        this.properties = new Properties();
    }

    public void init() throws IOException {
        String target = System.getProperty("target");
        properties.load(new FileReader(String.format("src/test/resources/%s.properties",target)));
        if(properties.getProperty("remote.server").equals("")) {
            if (browser.equals("Firefox"))
                wD = new FirefoxDriver();
            else
                wD = new ChromeDriver();
        }
        else{
            DesiredCapabilities capabilities = new DesiredCapabilities();
            if(browser.equals("Firefox"))
                capabilities.setBrowserName(BrowserType.FIREFOX);
            else
                capabilities.setBrowserName(BrowserType.CHROME);

            wD = new RemoteWebDriver(new URL(properties.getProperty("remote.server")), capabilities);
        }

        wD.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        sessionHelper = new SessionHelper(wD);
        navigationHelper = new NavigationHelper(wD);
        contactHelper = new ContactHelper(wD);
        groupHelper = new GroupHelper(wD);
        navigationHelper.navigateToPage(properties.getProperty("app.url"));
        sessionHelper.login(properties.getProperty("app.login"),properties.getProperty("app.password"));
    }

    public void stop() {
        wD.quit();
    }

    public ContactHelper getContactHelper() {
        return contactHelper;
    }

    public GroupHelper getGroupHelper() {
        return groupHelper;
    }

    public SessionHelper getSessionHelper() {
        return sessionHelper;
    }

    public NavigationHelper getNavigationHelper() {
        return navigationHelper;
    }

    public byte[] takeScreensot(){
        return ((TakesScreenshot)wD).getScreenshotAs(OutputType.BYTES);
    }
}
