package qa.issart.com.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class BaseHelper {
    protected WebDriver wD;
    BaseHelper(WebDriver wD){
        this.wD = wD;
    }

    protected void type(By locator,String text){
        wD.findElement(locator).click();
        wD.findElement(locator).clear();
        wD.findElement(locator).sendKeys(text);
    }

    protected void append(By locator, String pathToPhoto){
        wD.findElement(locator).sendKeys(pathToPhoto);
    }

    protected void click(By locator){
        wD.findElement(locator).click();
    }

    protected boolean isElementPresent(By locator){
        try {
            wD.findElement(locator);
            return true;
        }
        catch (NoSuchElementException nEE){
            return false;
        }
    }

    protected boolean iaAlertPresent(){
        try{
            wD.switchTo().alert().accept();
            return true;
        }
        catch (NoAlertPresentException nAE){
            return false;
        }
    }
}
