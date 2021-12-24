package qa.issart.com.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactPageTests extends TestBase{
    int contactsNum;
    GroupData groupFilter;

    @BeforeTest
    public void prepareGroupsAndContacts() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {

        List<GroupData> groupsList = appManager.getContactHelper().getGroupsFromDropDown(1);
        if (groupsList.size() < 3) {
            appManager.getNavigationHelper().navigateToGroupPage();
            addGroupsToAddressbook(3- groupsList.size());
            appManager.getNavigationHelper().navigateToContactPage();
        }

        contactsBeforeUI = appManager.getContactHelper().getContactsList();

        if (contactsBeforeUI.size() < 10) {
            addContactsToAddressbook(10 - contactsBeforeUI.size());
            contactsBeforeUI = appManager.getContactHelper().getContactsList();
        }
        contactsNum = contactsBeforeUI.size();
        groupFilter = groupsList.get(0);
        appManager.getContactHelper().selectContact(Arrays.asList(0));
        appManager.getContactHelper().moveSelectedContactsToGroup(groupFilter.getId());
        appManager.getContactHelper().setContactsFilter("");
    }

    @Test
    public void checkButtonsAndCheckBoxes(){
        assertThat(appManager.getContactHelper().selectAllWithCheck(contactsNum,true),equalTo(true));
        assertThat(appManager.getContactHelper().selectAllWithCheck(contactsNum,false),equalTo(false));
        appManager.getContactHelper().setContactSelection(rand.nextInt(contactsNum));
        assertThat(appManager.getContactHelper().selectAllWithCheck(contactsNum,true),equalTo(true));
        appManager.getContactHelper().setContactSelection(rand.nextInt(contactsNum));
        assertThat(appManager.getContactHelper().selectAllWithCheck(contactsNum,true),equalTo(false));
    }

    @Test
    public void checkHomeIcon(){
        int pnt = rand.nextInt(contactsNum);
        ContactData selectedContact = appManager.getContactHelper().getContactDataUI(pnt, false);
        selectedContact.withHomepage("");
        appManager.getNavigationHelper().navigateToContactPage();
        appManager.getContactHelper().modifyContact(selectedContact,0,pnt);
        assertThat(appManager.getContactHelper().checkHomeIcon(pnt),equalTo(false));
        pnt = rand.nextInt(contactsNum);
        selectedContact = appManager.getContactHelper().getContactDataUI(pnt, false);
        appManager.getNavigationHelper().navigateToContactPage();
        if(selectedContact.getHomepage().equals("")){
            selectedContact.withHomepage("www.yandex.ru");
            appManager.getContactHelper().modifyContact(selectedContact,0,pnt);
        }
        assertThat(appManager.getContactHelper().checkHomeIcon(pnt),equalTo(true));
    }

    @Test
    public void attemptDeleteOrMove(){
        assertThat(appManager.getContactHelper().clickOnDelete(),equalTo(true));
        assertThat(appManager.getContactHelper().clickOnMove(),equalTo(true));
        appManager.getNavigationHelper().navigateToContactPage();
        appManager.getContactHelper().setContactsFilter(String.valueOf(groupFilter.getId()));
        assertThat(appManager.getContactHelper().clickOnDelete(),equalTo(true));
        assertThat(appManager.getContactHelper().clickOnMove(),equalTo(true));
        appManager.getNavigationHelper().navigateToContactPage();
        assertThat(appManager.getContactHelper().clickOnRemove(),equalTo(true));
    }

}
