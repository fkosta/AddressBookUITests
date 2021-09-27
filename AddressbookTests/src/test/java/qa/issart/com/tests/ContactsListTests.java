package qa.issart.com.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

public class ContactsListTests extends TestBase {
    private int contactsNum;
    Set<ContactData> filteredContacts;
    List<GroupData> groupsInList;
    Set<ContactData> selectedContactsUI = new HashSet<>();
    ContactData contactInfo;

    @DataProvider(name = "groupsList")
    private Iterator<Object[]> groupsList() {
        return groupsBeforeDB.stream().map(g -> new Object[]{g}).collect(Collectors.toList()).iterator();
    }

    @DataProvider(name = "contactsList")
    private Iterator<Object[]> contactsList() {
        return contactsBeforeDB.stream().map(c -> new Object[]{c}).collect(Collectors.toList()).iterator();
    }

    @BeforeTest
    public void getAppContacts() {
        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();
        contactsNum = contactsBeforeUI.size();
        groupsInList = appManager.getContactHelper().getGroupsFromDropDown(1);
        if (useDB) {
            groupsBeforeDB = dbManager.getGroupsList();
            contactsBeforeDB = dbManager.getContactsList();
            assertThat(contactsBeforeDB, equalTo(contactsBeforeUI));
            assertThat(groupsInList.stream().collect(Collectors.toSet()), equalTo(groupsBeforeDB));
        }
    }

    @Test(dataProvider = "groupsList")
    public void verifyContactsInGroups(GroupData group) {
        if (useDB) {
            filteredContacts = appManager.getContactHelper().getFilteredContacts(String.valueOf(group.getId()));
            assertThat(filteredContacts, equalTo(group.getGroupContacts()));
        }
    }

    @Test(dataProvider = "contactsList")
    public void verifyContactsInfo(ContactData contactFromDB) {
        int id = contactFromDB.getId();
        contactInfo = appManager.getContactHelper().getContactDataUI(id, true);
        contactInfo.withId(id);
        assertThat(contactFromDB, equalTo(contactInfo));
        contactInfo.setContactGroups(contactFromDB.getContactGroups());
        appManager.getNavigationHelper().navigateToContactPage();
        String s1 = appManager.getContactHelper().getContactInfo(id);
        String s2 = contactInfo.printContactInfo();
        appManager.getNavigationHelper().navigateToContactPage();
        assertThat(s2, equalTo(s1));
    }

    @Test
    public void verifyGroupsInView(){
        List<Integer> cint = Collections.singletonList(rand.nextInt(contactsNum));
        selectedContactsUI = appManager.getContactHelper().selectContact(cint);
        GroupData selectedGroupFromList = groupsInList.get(rand.nextInt(groupsBeforeDB.size()));
        int contactId = selectedContactsUI.iterator().next().getId();
        Set<GroupData >contactGroupsBefore = appManager.getContactHelper().getContactsGroupFromInfo(contactId);
        appManager.getNavigationHelper().navigateToContactPage();
        appManager.getContactHelper().selectContact(cint);
        appManager.getContactHelper().moveSelectedContactsToGroup(selectedGroupFromList.getId());
        appManager.getContactHelper().setContactsFilter("");
        Set<GroupData> contactGroupsAfter = appManager.getContactHelper().getContactsGroupFromInfo(contactId);
        assertTrue(contactGroupsAfter.containsAll(contactGroupsBefore));
        assertTrue(contactGroupsAfter.contains(selectedGroupFromList));
    }

    @Test(dataProvider = "listOfIndices")
    public void addContactToGroup(String[] indList) {
        List<Integer> indices = parceIndList(indList, contactsNum);

        selectedContactsUI = appManager.getContactHelper().selectContact(indices);

        GroupData selectedGroupFromList = groupsInList.get(rand.nextInt(groupsInList.size()));
        appManager.getContactHelper().moveSelectedContactsToGroup(selectedGroupFromList.getId());
        assertTrue(appManager.getContactHelper().verifyGroupNameInList(selectedGroupFromList.getName()));

        filteredContacts = appManager.getContactHelper().getContactsList();
        assertTrue(filteredContacts.removeAll(selectedContactsUI));
        contactsAfterUI = appManager.getContactHelper().getFilteredContacts("");
        assertThat(contactsAfterUI, equalTo(contactsBeforeUI));
    }

    private List<Integer> parceIndList(String[] indList, int contactsNum) {
        List<Integer> indices = new ArrayList<>();
        for(String key:indList){
            if(key.equals("F"))
                indices.add(0);
            else if(key.equals("L"))
                indices.add(contactsNum-1);
            else if(key.equals("R"))
                indices.add(rand.nextInt(contactsNum-2)+1);
            else
                break;
        }
        return indices;
    }
}

