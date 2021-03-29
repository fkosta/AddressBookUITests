package qa.issart.com.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactHelper extends BaseHelper{
    public ContactHelper(WebDriver wD) {
        super(wD);
    }

    public Set<ContactData> getContactsList() {
        Set<ContactData> contactsList = new HashSet<>();
        int ind=2;
        int elementID;

        String xpathPattern0 = "/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[position()>1 and position()<7]";
        String xpathPattern1 = "/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[1]/input";
        List<WebElement> weList = wD.findElements(By.xpath(String.format(xpathPattern0,ind)));
        while (weList.size()>0){
            elementID = Integer.parseInt(wD.findElement(By.xpath(String.format(xpathPattern1,ind))).getAttribute("id"));
            contactsList.add(new ContactData(weList.get(1).getText(), weList.get(0).getText(), weList.get(2).getText(),
                    weList.get(3).getText(), weList.get(4).getText()).withId(elementID));
            ind++;
            weList = wD.findElements(By.xpath(String.format(xpathPattern0,ind)));
        }
        return contactsList;
    }

    public void fillContactForm(ContactData contactInfo, boolean isNew, GroupData contactGroup){
        type(By.name("firstname"),contactInfo.getFirstname());
        type(By.name("middlename"),contactInfo.getMiddlename());
        type(By.name("lastname"),contactInfo.getLastname());
        type(By.name("nickname"),contactInfo.getNickname());
        append(By.name("photo"),contactInfo.getPhoto());
        type(By.name("company"),contactInfo.getCompany());
        type(By.name("title"),contactInfo.getTitle());
        type(By.name("address"),contactInfo.getAddress());
        type(By.name("home"),contactInfo.getHome());
        type(By.name("mobile"),contactInfo.getMobile());
        type(By.name("work"),contactInfo.getWork());
        type(By.name("fax"),contactInfo.getFax());
        type(By.name("email"),contactInfo.getEmail());
        type(By.name("email2"),contactInfo.getEmail2());
        type(By.name("email3"),contactInfo.getEmail3());
        type(By.name("homepage"),contactInfo.getHomepage());
        type(By.name("address2"),contactInfo.getAddress2());
        type(By.name("phone2"),contactInfo.getPhone2());
        type(By.name("notes"),contactInfo.getNotes());
        if(isNew){
            if(contactGroup==null)
                new Select(wD.findElement(By.name("new_group"))).selectByIndex(1);
            else
                new Select(wD.findElement(By.name("new_group"))).selectByValue(String.valueOf(contactGroup.getId()));
        }
        else
            Assert.assertFalse(isElementPresent(By.name("new_group")));

        if (contactInfo.getBday()!=0)
            new Select(wD.findElement(By.name("bday"))).selectByValue(String.valueOf(contactInfo.getBday()));

        if (!contactInfo.getBmonth().equals(""))
            new Select(wD.findElement(By.name("bmonth"))).selectByValue(contactInfo.getBmonth());

            type(By.name("byear"), contactInfo.getByear());

        if (contactInfo.getAday()!=0)
            new Select(wD.findElement(By.name("aday"))).selectByVisibleText(String.valueOf(contactInfo.getAday()));

        if (!contactInfo.getAmonth().equals(""))
            new Select(wD.findElement(By.name("amonth"))).selectByVisibleText(contactInfo.getAmonth());

        type(By.name("ayear"), contactInfo.getAyear());
    }

    public void addContact(ContactData newContact, int iteration, GroupData contactGroup){
        click(By.linkText("add new"));
        fillContactForm(newContact, true, contactGroup);

        if(iteration%2==0)
            click(By.xpath("/html/body/div/div[4]/form/input[1]"));
        else
            click(By.xpath("/html/body/div/div[4]/form/input[21]"));
    }

    private ContactData getContactDataFromUI(int pnt, int clickEditIcon){
        String xpathBase = String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/",pnt+2);
        List<WebElement> contAttr = wD.findElements(By.xpath(xpathBase+"td[position()>1 and position()<7]"));

        int contId = Integer.parseInt(wD.findElement(By.xpath(xpathBase+"td[1]/input")).getAttribute("id"));
        ContactData selectedContact = new ContactData(contAttr.get(1).getText(), contAttr.get(0).getText(),
                contAttr.get(2).getText(),contAttr.get(3).getText(), contAttr.get(4).getText()).withId(contId);

        if(clickEditIcon==1)
            click(By.xpath(xpathBase+"td[8]/a"));
        else
            click(By.xpath(xpathBase+"td[1]/input"));

        return selectedContact;
    }

    public ContactData modifyContact(ContactData newContact, int iteration, int pnt) {
        String xpathBase = String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/",pnt+2);
        List<WebElement> contAttr = wD.findElements(By.xpath(xpathBase+"td[position()>1 and position()<7]"));

        int contId = Integer.parseInt(wD.findElement(By.xpath(xpathBase+"td[1]/input")).getAttribute("id"));

        ContactData updatedContact = new ContactData(contAttr.get(1).getText(), contAttr.get(0).getText(),
                contAttr.get(2).getText(), contAttr.get(3).getText(), contAttr.get(4).getText()).withId(contId);

        click(By.xpath(xpathBase+"td[8]/a"));
        fillContactForm(newContact,false,null);

        if(iteration%2==0)
            click(By.xpath("/html/body/div/div[4]/form[1]/input[1]"));
        else
            click(By.xpath("/html/body/div/div[4]/form[1]/input[22]"));

        return updatedContact;
    }

    public Set<ContactData> deleteContacts(List<Integer> indices) {
        Set<ContactData> deletedContacts = new HashSet<>();
        int deleteFromEditForm=(indices.size()== 1) ? (indices.get(0)%2) : 0;
        for(int pnt:indices){
            deletedContacts.add(getContactDataFromUI(pnt,deleteFromEditForm));
        }

        if (deleteFromEditForm==1)
            click(By.xpath("/html/body/div/div[4]/form[2]/input[2]"));
        else{
            click(By.xpath("/html/body/div/div[4]/form[2]/div[2]/input"));
            iaAlertPresent();
            isElementPresent(By.id("to_group"));
        }
        return deletedContacts;
    }

    public void setContactsFilter(String id){
        if(id.equals(""))
            new Select(wD.findElement(By.name("group"))).selectByVisibleText("[all]");
        else
            new Select(wD.findElement(By.name("group"))).selectByValue(id);
    }

    public Set<ContactData> getFilteredContacts(String id){
        setContactsFilter(id);
        return getContactsList();
    }
}
