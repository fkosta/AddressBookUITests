package qa.issart.com.tests;

import org.testng.annotations.*;
import qa.issart.com.models.ContactData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactModificationTest extends TestBase{
    private int iteration=0;
    int contactsNum;
    private Map<Integer,ContactData> changedContacts = new HashMap<>();
    private Map<Integer,ContactData> enteredContacts = new HashMap<>();

    @BeforeTest
    @Parameters({"dataFileName"})
    public void getAppContacts(@Optional String dataFileName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        if(dataFileName!=null)
            dataFile = dataFileName;

        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();

        if (contactsBeforeUI.size()<10){
            addContactsToAddressbook(10);
            contactsBeforeUI = appManager.getContactHelper().getContactsList();
        }

        contactsNum = contactsBeforeUI.size();
        if(useDB) {
            contactsBeforeDB = dbManager.getContactsList();
            assertThat(contactsBeforeDB, equalTo(contactsBeforeUI));
        }
        assertThat(contactsBeforeUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
    }

    @Test(dataProvider = "ContactsListFromFile")
    public void modifyContactFromList(ContactData newContact){
        iteration++;
        ContactData modifiedContact = appManager.getContactHelper().modifyContact(newContact,iteration,rand.nextInt(contactsNum));
        newContact.withId(modifiedContact.getId());
        logger.info(buildLogEntry(modifiedContact, newContact));
        if(useDB){
            contactsAfterDB = dbManager.getContactsList();
            enteredContacts.put(newContact.getId(),newContact);
            changedContacts.putIfAbsent(modifiedContact.getId(),modifiedContact);
            assertThat(contactsAfterDB, new withElementsInOut<ContactData>(contactsBeforeDB, newContact, modifiedContact));
            contactsBeforeDB = contactsAfterDB;
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
        assertThat(contactsAfterUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
    }

    private String buildLogEntry(ContactData modifiedContact, ContactData newContact) {
        StringBuilder sB = new StringBuilder("Modified contact with id: "+newContact.getId());
        if((modifiedContact.getFirstname().equals(newContact.getFirstname()))&&
            (modifiedContact.getLastname().equals(newContact.getLastname()))&&
            (modifiedContact.getAddress().equals(newContact.getAddress()))&&
            (modifiedContact.getAllEmails().equals(newContact.getAllEmails()))&&
            (modifiedContact.getAllPhones().equals(newContact.getAllPhones())))
            sB.append(". Visible attributes has not changed");
        else{
            if(!modifiedContact.getFirstname().equals(newContact.getFirstname()))
                sB.append(" new contact firstname: "+newContact.getFirstname()+" old: "+modifiedContact.getFirstname()+"\n");

            if(!modifiedContact.getLastname().equals(newContact.getLastname()))
                sB.append(" new contact lastname: "+newContact.getLastname()+" old: "+modifiedContact.getLastname()+"\n");

            if(!modifiedContact.getAddress().equals(newContact.getAddress()))
                sB.append(" new contact address: "+newContact.getAddress()+" old: "+modifiedContact.getAddress()+"\n");

            if(!modifiedContact.getAllEmails().equals(newContact.getAllEmails()))
                sB.append(" new contact emails: "+newContact.getEmail()+", "+newContact.getEmail2()+", "+
                        newContact.getEmail3()+" old: "+modifiedContact.getEmail()+", "+
                        modifiedContact.getEmail2()+", "+modifiedContact.getEmail3()+"\n");

            if(!modifiedContact.getAllPhones().equals(newContact.getAllPhones()))
                sB.append(" new contact phones: "+newContact.getHome()+", "+newContact.getMobile()+", "+
                        newContact.getWork()+", "+newContact.getPhone2()+" old: "+modifiedContact.getHome()+", "+
                        modifiedContact.getMobile()+", "+modifiedContact.getWork()+", "+modifiedContact.getPhone2()+"\n");
        }
        return sB.toString();
    }
}
