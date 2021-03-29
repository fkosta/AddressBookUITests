package qa.issart.com.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SessionHelper extends BaseHelper{
    public SessionHelper(WebDriver wD) {
        super(wD);
    }

    public void login(String login, String pass) {
        type(By.name("user"),login);
        type(By.name("pass"),pass);
        click(By.xpath("/html/body/div/div[4]/form/input[3]"));
    }
}
