package qa.issart.com.helpers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        properties.load(new FileReader(new File(String.format("src/test/resources/%s.properties",target))));
        if(browser.equals("Firefox"))
            wD = new FirefoxDriver();
        else
            wD = new ChromeDriver();

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
}
