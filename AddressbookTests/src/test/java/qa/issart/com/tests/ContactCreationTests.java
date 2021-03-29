package qa.issart.com.tests;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactCreationTests extends TestBase{

    private int iteration=0;
    private List<GroupData> groupsList;

    @BeforeTest
    public void getAppContacts(){
        appManager.getNavigationHelper().navigateToGroupPage();
        groupsList = appManager.getGroupHelper().getGroupsList().stream().collect(Collectors.toList());
        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();
        if(useDB) {
            contactsBeforeDB = dbManager.getContactsList();
            assertThat(contactsBeforeDB, equalTo(contactsBeforeUI));
        }
    }

    @Test(dataProvider = "ContactsListFromFile")
    public void addToContactList(ContactData newContact){
        iteration++;
        int ind = rand.nextInt(groupsList.size()+1);
        Set<ContactData> addedContacts;
        if (ind<groupsList.size())
            appManager.getContactHelper().addContact(newContact,iteration,groupsList.get(ind));
        else
            appManager.getContactHelper().addContact(newContact,iteration,null);

        if(useDB){
            contactsAfterDB = dbManager.getContactsList();
            addedContacts = contactsAfterDB.stream().filter(c->!contactsBeforeDB.contains(c)).collect(Collectors.toSet());
        }
        else{
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            addedContacts = contactsAfterUI.stream().filter(c->!contactsBeforeUI.contains(c)).collect(Collectors.toSet());
        }
        newContact.withId(addedContacts.iterator().next().getId());
        logger.info("Contacts in list: "+addedContacts.toString());
        logger.info("Added contact "+newContact.toString());
        assertThat(addedContacts, new withElementsInOut<ContactData>(newContact));
        if(useDB){
            processedContacts.add(newContact);
            contactsBeforeDB = contactsAfterDB;
        }
        else
            contactsBeforeUI = contactsAfterUI;
    }

    @AfterTest
    public void verifyContacts(){
        if(useDB){
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,processedContacts,"creation"));
        }
    }
}
