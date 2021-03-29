package qa.issart.com.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactsListTests extends TestBase{
    private int contactsNum;
    Set<ContactData> filteredContacts;

    @DataProvider(name = "groupsList")
    private Iterator<Object[]> groupsList(){
        return groupsBeforeDB.stream().map(g->new Object[]{g}).collect(Collectors.toList()).iterator();
    }

    @BeforeTest
    public void getAppContacts(){
        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();
        contactsNum = contactsBeforeUI.size();
        if(useDB) {
            groupsBeforeDB = dbManager.getGroupsList();
            contactsBeforeDB = dbManager.getContactsList();
            assertThat(contactsBeforeDB, equalTo(contactsBeforeUI));
        }
    }

    @Test(dataProvider = "groupsList")
    public void verifyContactsInGroups(GroupData group){
        if(useDB){
            filteredContacts = appManager.getContactHelper().getFilteredContacts(String.valueOf(group.getId()));
            assertThat(filteredContacts, equalTo(group.getGroupContacts()));
        }
    }
}
