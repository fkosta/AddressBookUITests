package qa.issart.com.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;

public class ContactsCreationTestsFiltered extends TestBase {
    private GroupData groupFilter;

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

        groupFilter = groupsList.get(0);
        contactsBeforeUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(groupFilter.getId()));
    }

    @Test(dataProvider = "ContactsListFromFile")
    public void addToFilteredContactList(ContactData newContact){
        Set<ContactData> addedContacts;
        appManager.getContactHelper().addContact(newContact,0,groupFilter);
        contactsAfterUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(groupFilter.getId()));
        addedContacts = contactsAfterUI.stream().filter(c->!contactsBeforeUI.contains(c)).collect(Collectors.toSet());
        newContact.withId(addedContacts.iterator().next().getId());
        logger.info("Added new contact id:"+newContact.getId()+" first name: "+newContact.getFirstname()+
                " last name: "+newContact.getLastname()+" address: "+newContact.getAddress()+" phones: "+
                newContact.getHome()+", "+newContact.getMobile()+", "+newContact.getWork()+", "+newContact.getPhone2()+
                " emails: "+newContact.getEmail()+", "+newContact.getEmail2()+", "+newContact.getEmail3());

        contactsBeforeUI = contactsAfterUI;
        assertThat(addedContacts, new HasTheOnlyElement<ContactData>(newContact));
    }
}
