package qa.issart.com.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactsRemovalFromGroupTests extends TestBase{
    int groupsNum=6;
    int contactsNum=12;
    Set<ContactData> selectedContacts = new HashSet<>();
    Set<GroupData> selectedGroups = new HashSet<>();
    GroupData selectedGroup;
    ContactData selectedContact;
    Map<ContactData, Set<GroupData>> contactGroupsBefore = new HashMap<>();
    Map<GroupData, Set<ContactData>> groupContactsBefore = new HashMap<>();
    List<GroupData> selectedGroupsList;
    int groupSelector=0;

    @BeforeTest
    public void testDataPreparation() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        //Initialization of group vs contacts structure
        appManager.getNavigationHelper().navigateToContactPage();
        if(appManager.getContactHelper().getContactsList().size()>0) {
            appManager.getContactHelper().deleteAllContacts();
//            assertThat(appManager.getContactHelper().getContactsList().size(), equalTo(0));
        }

        appManager.getNavigationHelper().navigateToGroupPage();

        if(appManager.getGroupHelper().getGroupsList().size() > 0) {
            appManager.getGroupHelper().deleteAllGroups(appManager.getGroupHelper().getGroupsList().size());
            appManager.getNavigationHelper().navigateBackToGroups();
//            assertThat(appManager.getGroupHelper().getGroupsList().size(),equalTo(0));
        }

        //adding groups and contacts
        addGroupsToAddressbook(groupsNum);
        groupsBeforeUI = appManager.getGroupHelper().getGroupsList();

        appManager.getNavigationHelper().navigateToContactPage();
        addContactsToAddressbook(contactsNum);
        contactsBeforeUI = appManager.getContactHelper().getContactsList();

        Iterator<GroupData> itGroups = groupsBeforeUI.iterator();
        //forming group - contacts structure and filling the corresponding map
        List<Integer> indicesList = Stream.iterate(0, x -> x + 1).limit(contactsNum - 2).collect(Collectors.toList());
        selectedGroupsList = new ArrayList<>();

        int idx = contactsNum -2;
        for(int j=4;j>0;j--){
            selectedContacts = appManager.getContactHelper().selectContact(indicesList.subList(0,idx));
            selectedGroup = itGroups.next();
            appManager.getContactHelper().moveSelectedContactsToGroup(selectedGroup.getId());
            groupContactsBefore.put(selectedGroup,selectedContacts);
            selectedGroupsList.add(selectedGroup);
            idx=idx-j;
            appManager.getContactHelper().getFilteredContacts("");
        }

        Set<Map.Entry<GroupData,Set<ContactData>>> mapEntries = groupContactsBefore.entrySet();
        Iterator itMapEtries = mapEntries.iterator();
        //forming contact - groups structure and filling the corresponding map
        while (itMapEtries.hasNext()){
            Map.Entry<GroupData,Set<ContactData>> mE = (Map.Entry<GroupData, Set<ContactData>>) itMapEtries.next();
            Set<ContactData> contactsMapSet = mE.getValue();
            Iterator<ContactData> itContacts = contactsMapSet.iterator();
            while (itContacts.hasNext()){
                selectedContact = itContacts.next();
                if(!contactGroupsBefore.containsKey(selectedContact)){
                    Set<GroupData> groupsMapSet= new HashSet<>();
                    groupsMapSet.add(mE.getKey());
                    contactGroupsBefore.put(selectedContact,groupsMapSet);
                }
                else {
                    Set<GroupData> groupsMapSet = contactGroupsBefore.get(selectedContact);
                    groupsMapSet.add(mE.getKey());
                    contactGroupsBefore.put(selectedContact,groupsMapSet);
                }
            }
        }
    }

    @Test(dataProvider ="listOfIndices", priority = 1)
    public void removeContactFromGroup(String[] indList){

        for (GroupData selectedGroup : selectedGroupsList) {
            if (groupContactsBefore.get(selectedGroup).size() > 3) {

                contactsBeforeUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(selectedGroup.getId()));
                contactsNum = contactsBeforeUI.size();
                List<Integer> indices = parceIndList(indList, contactsNum);

                selectedContacts = appManager.getContactHelper().selectContact(indices);
                appManager.getContactHelper().removeContactsFromGroup(selectedContacts, selectedGroup.getId());
                contactsAfterUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(selectedGroup.getId()));
                assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI, null, selectedContacts));
                correctGroupContactsMap(selectedContacts, selectedGroup);
                correctContactGroupsMap(selectedContacts, selectedGroup);
                assertThat(contactsAfterUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
            }
        }
    }

    @Test(priority = 2)
    public void removeAllContactsFromGroup(){
        for (GroupData selectedGroup : selectedGroupsList) {
            appManager.getContactHelper().setContactsFilter(String.valueOf(selectedGroup.getId()));
            contactsNum = groupContactsBefore.get(selectedGroup).size();
            if(appManager.getContactHelper().selectAllWithCheck(contactsNum,true)) {
                appManager.getContactHelper().removeContactsFromGroup(selectedContacts, selectedGroup.getId());
                contactsAfterUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(selectedGroup.getId()));
                assertThat(contactsAfterUI.size(), equalTo(0));
                correctGroupContactsMap(selectedContacts, selectedGroup);
                correctContactGroupsMap(selectedContacts, selectedGroup);
                assertThat(contactsAfterUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
            }
        }
    }

    @Test(priority = 3,invocationCount = 4)
    public void removeRandomContact(){
        selectedGroup = selectedGroupsList.get(groupSelector);
        contactsBeforeUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(selectedGroup.getId()));
        selectedContacts = appManager.getContactHelper().selectContact(Collections.singletonList(rand.nextInt(contactsBeforeUI.size())));
        appManager.getContactHelper().removeContactsFromGroup(selectedContacts,selectedGroup.getId());
        contactsAfterUI = appManager.getContactHelper().getFilteredContacts(String.valueOf(selectedGroup.getId()));
        assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,null,selectedContacts));
        correctGroupContactsMap(selectedContacts, selectedGroup);
        correctContactGroupsMap(selectedContacts, selectedGroup);
        assertThat(contactsAfterUI.size(),equalTo(appManager.getContactHelper().getContactsNumber()));
    }

    private void correctContactGroupsMap(Set<ContactData> selectedContacts, GroupData selectedGroup) {
        Iterator<ContactData> itCont = selectedContacts.iterator();
        while (itCont.hasNext()){
            selectedContact = itCont.next();
            selectedGroups = contactGroupsBefore.get(selectedContact);
            selectedGroups.remove(selectedGroup);
            contactGroupsBefore.put(selectedContact, selectedGroups);
        }
    }

    private void correctGroupContactsMap(Set<ContactData> selectedContacts, GroupData selectedGroup) {
        Set<ContactData> contacsSet = groupContactsBefore.get(selectedGroup);
        contacsSet.removeAll(selectedContacts);
        groupContactsBefore.put(selectedGroup, contacsSet);
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
