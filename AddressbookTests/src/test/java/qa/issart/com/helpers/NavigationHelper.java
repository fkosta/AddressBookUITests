package qa.issart.com.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NavigationHelper extends BaseHelper{

    public NavigationHelper(WebDriver wD) {
        super(wD);
    }

    public void navigateToPage(String pageURL) {
        wD.get(pageURL);
    }

    public void navigateToGroupPage() {
        click(By.linkText("groups"));
    }

    public void navigateBackToGroups(){
        click(By.linkText("group page"));
    }

    public boolean navigateToContactPage(){
        click(By.linkText("home"));
        return isElementPresent(By.id("maintable"));
    }

    public boolean navigateToBirthdaysList(){
        click(By.linkText("next birthdays"));
        return isElementPresent(By.xpath("/html/body/div/div[4]/h1"));
    }

    public boolean navigateToPrintAllPage() {
        click(By.linkText("print all"));
        return isElementPresent(By.xpath("/html/body/div/div[1]/h1"));
    }

    public boolean navigateToPrintPhonesPage() {
        click(By.linkText("print phones"));
        return isElementPresent(By.xpath("/html/body/div/div[1]/h1"));
    }
}
