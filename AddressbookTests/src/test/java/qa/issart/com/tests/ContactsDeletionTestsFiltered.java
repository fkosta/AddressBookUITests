package qa.issart.com.tests;

import org.testng.annotations.*;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactsDeletionTestsFiltered extends TestBase {
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
            addGroupsToAddressbook(3 - groupsList.size());
            appManager.getNavigationHelper().navigateToContactPage();
            groupsList = appManager.getContactHelper().getGroupsFromDropDown(1);
        }

        contactsBeforeUI = appManager.getContactHelper().getContactsList();

        if (contactsBeforeUI.size() < 12) {
            addContactsToAddressbook(12 - contactsBeforeUI.size());
        }

        groupFilter = groupsList.get(0);
        for (int j = 0; j < 10; j++) {
            appManager.getContactHelper().setContactSelection(j);
        }

        appManager.getContactHelper().moveSelectedContactsToGroup(groupFilter.getId());
        contactsBeforeUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(groupFilter.getId()));
        contactsNum = contactsBeforeUI.size();
        assertThat(contactsNum,equalTo(appManager.getContactHelper().getContactsNumber()));
    }

    @Test(dataProvider = "listOfIndices", testName = "deleteContactsByIndices")
    public void deleteContactsByIndices(String[] indList) {
        List<Integer> indices = parceIndList(indList, contactsNum);
        Set<ContactData> deletedContacts = appManager.getContactHelper().deleteContacts(indices);
        logger.info("Indices for contacts are " + indices.toString());
        logger.info("Deleted contacts are " + returnIdAndNamesOfContacts(deletedContacts));
        contactsAfterUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(groupFilter.getId()));
        contactsNum = contactsAfterUI.size();
        assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI, null, deletedContacts));
    }

    @Test(testName = "deleteAllContacts")
    public void deleteAllContacts(){
        appManager.getContactHelper().deleteAllContacts();
        appManager.getNavigationHelper().navigateToContactPage();
        contactsAfterUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(groupFilter.getId()));
        assertThat(contactsAfterUI.size(),equalTo(0));
    }

    @AfterTest
    public void verifyContactsNumber(){
        assertThat(contactsAfterUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
    }

    private List<Integer> parceIndList(String[] indList, int contactsNum) {
        List<Integer> indices = new ArrayList<>();
        for (String key : indList) {
            if (key.equals("F"))
                indices.add(0);
            else if (key.equals("L"))
                indices.add(contactsNum - 1);
            else if (key.equals("R"))
                indices.add(rand.nextInt(contactsNum - 2) + 1);
            else
                break;
        }
        return indices;
    }
    private String returnIdAndNamesOfContacts(Set<ContactData> deletedContacts) {
        StringBuilder sB = new StringBuilder();
        Iterator<ContactData> iterator = deletedContacts.iterator();
        ContactData nextContact;
        while (iterator.hasNext()) {
            nextContact = iterator.next();
            sB.append(" id: " + nextContact.getId() + " first name: " + nextContact.getFirstname() + " last name " +
                    nextContact.getLastname()+"\n");
        }
        return sB.toString();
    }
}