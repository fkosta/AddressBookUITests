package qa.issart.com.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.util.*;
import java.util.stream.Collectors;

public class ContactHelper extends BaseHelper {
    public ContactHelper(WebDriver wD) {
        super(wD);
    }

    public Set<ContactData> getContactsList() {
        Set<ContactData> contactsList = new HashSet<>();
        int ind = 2;
        int elementID;

        String xpathPattern0 = "/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[position()>1 and position()<7]";
        String xpathPattern1 = "/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[1]/input";
        List<WebElement> weList = wD.findElements(By.xpath(String.format(xpathPattern0, ind)));
        while (weList.size() > 0) {
            elementID = Integer.parseInt(wD.findElement(By.xpath(String.format(xpathPattern1, ind))).getAttribute("id"));
            contactsList.add(new ContactData(weList.get(1).getText(), weList.get(0).getText(), weList.get(2).getText(),
                    weList.get(3).getText(), weList.get(4).getText()).withId(elementID));
            ind++;
            weList = wD.findElements(By.xpath(String.format(xpathPattern0, ind)));
        }
        return contactsList;
    }

    public void fillContactForm(ContactData contactInfo, boolean isNew, GroupData contactGroup) {
        type(By.name("firstname"), contactInfo.getFirstname());
        type(By.name("middlename"), contactInfo.getMiddlename());
        type(By.name("lastname"), contactInfo.getLastname());
        type(By.name("nickname"), contactInfo.getNickname());

        if(!contactInfo.getPhoto().equals(""))
            append(By.name("photo"), contactInfo.getPhoto());

        type(By.name("company"), contactInfo.getCompany());
        type(By.name("title"), contactInfo.getTitle());
        type(By.name("address"), contactInfo.getAddress());
        type(By.name("home"), contactInfo.getHome());
        type(By.name("mobile"), contactInfo.getMobile());
        type(By.name("work"), contactInfo.getWork());
        type(By.name("fax"), contactInfo.getFax());
        type(By.name("email"), contactInfo.getEmail());
        type(By.name("email2"), contactInfo.getEmail2());
        type(By.name("email3"), contactInfo.getEmail3());
        type(By.name("homepage"), contactInfo.getHomepage());
        type(By.name("address2"), contactInfo.getAddress2());
        type(By.name("phone2"), contactInfo.getPhone2());
        type(By.name("notes"), contactInfo.getNotes());
        if (isNew) {
            if (contactGroup == null){}
//                new Select(wD.findElement(By.name("new_group"))).selectByIndex(1);
            else
                new Select(wD.findElement(By.name("new_group"))).selectByValue(String.valueOf(contactGroup.getId()));
        } else
            Assert.assertFalse(isElementPresent(By.name("new_group")));

        if (contactInfo.getBday() != 0)
            new Select(wD.findElement(By.name("bday"))).selectByValue(String.valueOf(contactInfo.getBday()));

        if (!contactInfo.getBmonth().equals(""))
            new Select(wD.findElement(By.name("bmonth"))).selectByValue(contactInfo.getBmonth());

        type(By.name("byear"), contactInfo.getByear());

        if (contactInfo.getAday() != 0)
            new Select(wD.findElement(By.name("aday"))).selectByVisibleText(String.valueOf(contactInfo.getAday()));

        if (!contactInfo.getAmonth().equals(""))
            new Select(wD.findElement(By.name("amonth"))).selectByVisibleText(contactInfo.getAmonth());

        type(By.name("ayear"), contactInfo.getAyear());
    }

    public void addContact(ContactData newContact, int iteration, GroupData contactGroup) {
        click(By.linkText("add new"));
        fillContactForm(newContact, true, contactGroup);

        if (iteration % 2 == 0)
            click(By.xpath("/html/body/div/div[4]/form/input[1]"));
        else
            click(By.xpath("/html/body/div/div[4]/form/input[21]"));
    }

    private ContactData getContactDataFromUI(int pnt, int clickEditIcon) {
        String xpathBase = String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/", pnt + 2);
        List<WebElement> contAttr = wD.findElements(By.xpath(xpathBase + "td[position()>1 and position()<7]"));

        int contId = Integer.parseInt(wD.findElement(By.xpath(xpathBase + "td[1]/input")).getAttribute("id"));
        ContactData selectedContact = new ContactData(contAttr.get(1).getText(), contAttr.get(0).getText(),
                contAttr.get(2).getText(), contAttr.get(3).getText(), contAttr.get(4).getText()).withId(contId);

        if (clickEditIcon == 1)
            click(By.xpath(xpathBase + "td[8]/a"));
        else
            click(By.xpath(xpathBase + "td[1]/input"));

        return selectedContact;
    }

    public ContactData modifyContact(ContactData newContact, int iteration, int pnt) {
        String xpathBase = String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/", pnt + 2);
        List<WebElement> contAttr = wD.findElements(By.xpath(xpathBase + "td[position()>1 and position()<7]"));

        int contId = Integer.parseInt(wD.findElement(By.xpath(xpathBase + "td[1]/input")).getAttribute("id"));

        ContactData updatedContact = new ContactData(contAttr.get(1).getText(), contAttr.get(0).getText(),
                contAttr.get(2).getText(), contAttr.get(3).getText(), contAttr.get(4).getText()).withId(contId);

        click(By.xpath(xpathBase + "td[8]/a"));
        fillContactForm(newContact, false, null);

        if (iteration % 2 == 0)
            click(By.xpath("/html/body/div/div[4]/form[1]/input[1]"));
        else
            click(By.xpath("/html/body/div/div[4]/form[1]/input[22]"));

        return updatedContact;
    }

    public Set<ContactData> deleteContacts(List<Integer> indices) {
        Set<ContactData> deletedContacts = new HashSet<>();

        int deleteFromEditForm = (indices.size() == 1) ? (indices.get(0) % 2) : 0;
        for (int pnt : indices) {
            deletedContacts.add(getContactDataFromUI(pnt, deleteFromEditForm));
        }

        if (deleteFromEditForm == 1)
            click(By.xpath("/html/body/div/div[4]/form[2]/input[2]"));
        else {
            click(By.xpath("/html/body/div/div[4]/form[2]/div[2]/input"));
            iaAlertPresent();
            isElementPresent(By.id("to_group"));
        }

        return deletedContacts;
    }

    public void setContactsFilter(String id) {
        if (id.equals(""))
            new Select(wD.findElement(By.name("group"))).selectByVisibleText("[all]");
        else
            new Select(wD.findElement(By.name("group"))).selectByValue(id);
    }

    public Set<ContactData> getFilteredContacts(String id) {
        setContactsFilter(id);
        return getContactsList();
    }

    public ContactData getContactDataUI(int id, boolean isContactId) {
        if(isContactId)
            click(By.xpath(String.format("//a[@href=\"edit.php?id=%s\"]", id)));
        else
            click(By.xpath(String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[8]/a", id+2)));

        ContactData contactDataUI = new ContactData(getAttribute(By.name("firstname"), "value"),
                getAttribute(By.name("middlename"), "value"), getAttribute(By.name("lastname"), "value"),
                getAttribute(By.name("nickname"), "value"), "", getAttribute(By.name("company"), "value"),
                getAttribute(By.name("title"), "value"), getAttribute(By.name("address"), ""),
                getAttribute(By.name("home"), "value"), getAttribute(By.name("mobile"), "value"),
                getAttribute(By.name("work"), "value"), getAttribute(By.name("fax"), "value"),
                getAttribute(By.name("email"), "value"), getAttribute(By.name("email2"), "value"),
                getAttribute(By.name("email3"), "value"), getAttribute(By.name("homepage"), "value"),
                getAttribute(By.name("bday"), "value"), getAttribute(By.name("bmonth"), "value"),
                getAttribute(By.name("byear"), "value"), getAttribute(By.name("aday"), "value"),
                getAttribute(By.name("amonth"), "value"), getAttribute(By.name("ayear"), "value"),
                getAttribute(By.name("phone2"), "value"), getAttribute(By.name("address2"), ""),
                getAttribute(By.name("notes"), ""));
        contactDataUI.composePhonesAndEmails();
        return contactDataUI;
    }

    public String getContactInfo(int ind) {
        click(By.xpath(String.format("//a[@href=\"view.php?id=%s\"]", ind)));
        String contactHTML = wD.findElement(By.xpath("//*[@id=\"content\"]")).getText();
        String[] contactData = contactHTML.split("\n\n");
        StringBuilder contactInfo = new StringBuilder();
        for (String contactField : contactData) {
            if (contactField.startsWith("H:") || contactField.startsWith("M:") || contactField.startsWith("W:") || contactField.startsWith("F:")) {
                String[] contactPhones = contactField.split("\\w:\\s");
                for (int inx = 1; inx < contactPhones.length; inx++) {
                    contactInfo.append(contactPhones[inx]);
                }
                contactInfo.append("\n");
            } else if (contactField.contains("Homepage")) {
                String[] emailWWW = contactField.split("Homepage:\n");
                contactInfo.append(emailWWW[0]).append(emailWWW[1]);
                contactInfo.append("\n");
            } else if (contactField.contains("Birthday") || (contactField.contains("Anniversary"))) {
                contactField = contactField.replaceAll("Birthday\\s|Anniversary\\s|\\.|\\s\\(.+\\)", "");
                String[] ba = contactField.split("\n");
                contactInfo.append(ba[0].replaceAll("\\s", "-").toLowerCase()).append("\n");
                contactInfo.append(ba[1].replaceAll("\\s", "-").toLowerCase()).append("\n");
            } else if (contactField.startsWith("P:")) {
                String[] contactPhones = contactField.split("P:\\s");
                contactInfo.append(contactPhones[1]).append("\n");
            } else if (contactField.startsWith("\nMember")) {
                contactInfo.append(contactField.replaceAll("\nMember\\sof:\\s", ""));
            } else {
                contactInfo.append(contactField).append("\n");
            }
        }
        return contactInfo.toString();
    }

    public List<GroupData> getGroupsFromDropDown(int list) {
        String xpathexp;
        if (list == 0)
            xpathexp = "/html/body/div/div[4]/form[1]/select";
        else
            xpathexp = "/html/body/div/div[4]/form[2]/div[4]/select/option";

        List<WebElement> groupsInList = wD.findElements(By.xpath(xpathexp));
        return groupsInList.stream().map(wG -> new GroupData().withId(Integer.parseInt(wG.getAttribute("value")))
                .withName(wG.getText())).collect(Collectors.toList());
    }

    public Set<ContactData> selectContact(List<Integer> indices) {
        Set<ContactData> selectedContacts = new HashSet<>();
        for(int ind:indices) {
            List<WebElement> contactsUI = wD.findElements(By.xpath(String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[position()>1 and position()<7]", (ind + 2))));
            int elementID = Integer.parseInt(wD.findElement(By.xpath(String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[1]/input", (ind + 2)))).getAttribute("id"));
            selectedContacts.add(new ContactData(contactsUI.get(1).getText(), contactsUI.get(0).getText(),
                    contactsUI.get(2).getText(), contactsUI.get(3).getText(), contactsUI.get(4).getText()).withId(elementID));
            click(By.xpath(String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[1]/input", (ind + 2))));
        }
        return selectedContacts;
    }

    public void setContactSelection(int ind){
        click(By.xpath(String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[1]/input", (ind + 2))));
    }

    public void moveSelectedContactsToGroup(int selectedGroupId) {
        new Select(wD.findElement(By.name("to_group"))).selectByValue(String.valueOf(selectedGroupId));
        click(By.name("add"));
        click(By.xpath("/html/body/div/div[4]/div/i/a"));
    }

    public boolean verifyGroupNameInList(String name) {
        return name.equals(getAttribute(By.xpath("/html/body/div/div[4]/form[1]/select/option[1]"),""));
    }

    public Set<GroupData> getContactsGroupFromInfo(int id){
        click(By.xpath(String.format("//a[@href=\"view.php?id=%d\"]",id)));
        List<WebElement> groupLinks = wD.findElements(By.xpath("/html/body/div/div[4]/i/a"));
        Set<GroupData> contactGroups = new HashSet<>();
        for (WebElement groupLink:groupLinks){
            String[] idLink = groupLink.getAttribute("href").split("group=");
            contactGroups.add(new GroupData().withId(Integer.parseInt(idLink[1])).withName(groupLink.getText()));
        }
        return contactGroups;
    }
    public void clickOnGroupLink(GroupData selectedGroup) {
        click(By.xpath(String.format("/html/body/div/div[4]/i/a[@href=\"./index.php?group=%d\"]",selectedGroup.getId())));
    }

    public void removeContactsFromGroup(Set<ContactData> selectedContacts, int groupId) {
//        for(ContactData contact:selectedContacts){
//            click(By.xpath(String.format("//*[@id=\"%d\"]",contact.getId())));
//        }
        click(By.xpath("/html/body/div/div[4]/form[2]/div[3]/input"));
        click(By.xpath(String.format("/html/body/div/div[4]/div/i/a[@href=\"./?group=%d\"]",groupId)));
    }

    public void deleteAllContacts() {
        click(By.id("MassCB"));
        click(By.xpath("/html/body/div/div[4]/form[2]/div[2]/input"));

        iaAlertPresent();
    }

    public boolean selectAllWithCheck(int contactsNum, boolean state) {
        click(By.id("MassCB"));
        boolean isSet=true;
        for(int j=0;j<contactsNum;j++){
            isSet=isCheckBoxSet(By.xpath(String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[1]/input", (j+ 2))));
            if (isSet!=state)
                break;
        }

        return isSet;
    }

    public boolean checkHomeIcon(int pnt){
        String xpath = String.format("/html/body/div/div[4]/form[2]/table/tbody/tr[%d]/td[10]/a/img", pnt+2);
        List<WebElement> wE = wD.findElements(By.xpath(xpath));
        if(wE.size()==0)
            return false;
        else{
            return wE.get(0).getAttribute("src").contains("icons/house.png");
        }
    }

    public boolean clickOnDelete() {
        click(By.xpath("/html/body/div/div[4]/form[2]/div[2]/input"));
        return iaAlertPresent();
    }

    public boolean clickOnMove() {
        click(By.name("add"));
        return getAttribute(By.xpath("/html/body/div/div[4]/div/i"),"").contains("No users selected");
    }

    public Map<String, Set<String>> getContactsMap() {
        Map<String, Set<String>> BPMap = new HashMap<>();
        List<WebElement> bdInfo = new ArrayList<>();
        List<WebElement> wEList = wD.findElements(By.xpath("/html/body/div/div[4]/table/tbody/tr"));
        Set<String> contactAttributes = new HashSet<>();
        String monthName="";
        String contactData="";
        for (WebElement wE:wEList){
            if(wE.getAttribute("class").equals("")){
                monthName = wE.findElement(By.tagName("th")).getText();
                bdInfo.clear();
                contactAttributes = new HashSet<>();
            }
            else if(wE.getAttribute("class").equals("tablespace"))
                    continue;
            else{
                contactData="";
                bdInfo = wE.findElements(By.tagName("td"));
                for(int j=0;j<bdInfo.size();j++){
                    if((j<3)||(j==5))
                        contactData = contactData+";"+bdInfo.get(j).getText();
                    else if(j==3){
                        String age = bdInfo.get(j).getText().replace("(","").replace(")","");
                        contactData = contactData+";"+age;
                    }
                    else if(j==4)
                        contactData = contactData+";"+bdInfo.get(j).findElement(By.tagName("a")).getText();
                    else
                        break;
                }
                contactAttributes.add(contactData);
                BPMap.put(monthName,contactAttributes);
            }
        }
        return BPMap;
    }
}