package qa.issart.com.tests;

import org.hamcrest.MatcherAssert;
import org.testng.annotations.*;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactCreationTests extends TestBase{

    private int iteration=0;
    private List<GroupData> groupsList;

    @BeforeTest
    @Parameters({"dataFileName"})
    public void getAppContacts(@Optional String dataFileName){
        if(dataFileName!=null)
            dataFile = dataFileName;

        appManager.getNavigationHelper().navigateToGroupPage();
        groupsList = new ArrayList<>(appManager.getGroupHelper().getGroupsList());
        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();
        if(useDB) {
            contactsBeforeDB = dbManager.getContactsList();
            assertThat(contactsBeforeDB, equalTo(contactsBeforeUI));
        }
        assertThat(contactsBeforeUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
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
        logger.info("Added new contact id:"+newContact.getId()+" first name: "+newContact.getFirstname()+
                " last name: "+newContact.getLastname()+" address: "+newContact.getAddress()+" phones: "+
                newContact.getHome()+", "+newContact.getMobile()+", "+newContact.getWork()+", "+newContact.getPhone2()+
                " emails: "+newContact.getEmail()+", "+newContact.getEmail2()+", "+newContact.getEmail3());

        if(useDB){
            processedContacts.add(newContact);
            contactsBeforeDB = contactsAfterDB;
        }
        else
            contactsBeforeUI = contactsAfterUI;

        assertThat(addedContacts, new HasTheOnlyElement<ContactData>(newContact));
    }

    @AfterTest
    public void verifyContacts(){
        if(useDB){
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,processedContacts,null));
        }
        assertThat(contactsAfterUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
    }
}
