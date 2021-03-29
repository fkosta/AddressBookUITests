package qa.issart.com.tests;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactModificationTest extends TestBase{
    private int iteration=0;
    int contactsNum;
    private Map<Integer,ContactData> changedContacts = new HashMap<>();
    private Map<Integer,ContactData> enteredContacts = new HashMap<>();

    @BeforeTest
    public void getAppContacts(){
        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();
        contactsNum = contactsBeforeUI.size();
        if(useDB) {
            contactsBeforeDB = dbManager.getContactsList();
            assertThat(contactsBeforeDB, equalTo(contactsBeforeUI));
        }
    }

    @Test(dataProvider = "ContactsListFromFile")
    public void modifyContactFromList(ContactData newContact){
        iteration++;
        ContactData modifiedContact = appManager.getContactHelper().modifyContact(newContact,iteration,rand.nextInt(contactsNum));
        logger.info("The modified contact is "+modifiedContact.toString());
        newContact.withId(modifiedContact.getId());
        if(useDB){
            contactsAfterDB = dbManager.getContactsList();
            assertThat(contactsAfterDB, new withElementsInOut<ContactData>(contactsBeforeDB, newContact, modifiedContact));
            contactsBeforeDB = contactsAfterDB;
            enteredContacts.put(newContact.getId(),newContact);
            changedContacts.putIfAbsent(modifiedContact.getId(),modifiedContact);
        }
        else{
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,newContact,modifiedContact));
            contactsBeforeUI = contactsAfterUI;
        }
    }
    @AfterTest
    public void verifyContactsInUI(){
        if(useDB){
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            assertThat(contactsAfterUI,
                    new withElementsInOut<ContactData>(contactsBeforeUI,enteredContacts.values(),changedContacts.values()));
        }
    }

}
