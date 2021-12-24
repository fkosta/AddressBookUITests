package qa.issart.com.tests;

import org.testng.annotations.*;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactsModificationTestsFiltered extends TestBase{
    private GroupData groupFilter;
    int contactsNum;

    @BeforeTest
    @Parameters({"dataFileName"})
    public void getFilteredAppContacts(@Optional String dataFileName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        if (dataFileName != null)
            dataFile = dataFileName;

        List<GroupData> groupsList = appManager.getContactHelper().getGroupsFromDropDown(1);
        if (groupsList.size() < 3) {
            appManager.getNavigationHelper().navigateToGroupPage();
            addGroupsToAddressbook(3- groupsList.size());
            appManager.getNavigationHelper().navigateToContactPage();
            groupsList = appManager.getContactHelper().getGroupsFromDropDown(1);
        }

        contactsBeforeUI = appManager.getContactHelper().getContactsList();

        if(contactsBeforeUI.size() < 8){
            addContactsToAddressbook(8-contactsBeforeUI.size());
        }

        groupFilter = groupsList.get(0);
        for(int j=0;j<6;j++){
            appManager.getContactHelper().setContactSelection(j);
        }

        appManager.getContactHelper().moveSelectedContactsToGroup(groupFilter.getId());
        contactsBeforeUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(groupFilter.getId()));
        contactsNum=contactsBeforeUI.size();
        assertThat(contactsNum,equalTo(appManager.getContactHelper().getContactsNumber()));
    }

    @Test(dataProvider = "ContactsListFromFile")
    public void modifyContactInFilteredList(ContactData newContact){
        ContactData modifiedContact = appManager.getContactHelper().modifyContact(newContact,0,rand.nextInt(contactsNum));
        newContact.withId(modifiedContact.getId());

        contactsAfterUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(groupFilter.getId()));
        assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,newContact,modifiedContact));
        contactsBeforeUI = contactsAfterUI;
    }

    @AfterTest
    public void verifyContactsNumber(){
        assertThat(contactsAfterUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
    }
}
