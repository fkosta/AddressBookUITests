package qa.issart.com.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContactDeletionTests extends TestBase{
    private int contactsNum;

    @BeforeTest
    public void getContactsInApp() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();
        if(contactsBeforeUI.size()==0){
            addContactsToAddressbook(10);
            contactsBeforeUI = appManager.getContactHelper().getContactsList();
        }

        contactsNum = contactsBeforeUI.size();
        if(useDB){
            contactsBeforeDB = dbManager.getContactsList();
            Assert.assertEquals(contactsBeforeDB,contactsBeforeUI);
        }
    }

    @Test(dataProvider = "listOfIndices", testName = "deleteContactsByIndices")
    public void deleteContactsByIndices(String[] indList){
        List<Integer> indices = parceIndList(indList, contactsNum);
        Set<ContactData> deletedContacts = appManager.getContactHelper().deleteContacts(indices);
        logger.info("Indices for contacts are "+indices.toString());
        logger.info("Deleted contacts are "+returnIdAndNamesOfContacts(deletedContacts));
        if(useDB){
            contactsAfterDB = dbManager.getContactsList();
            contactsNum = contactsAfterDB.size();
            processedContacts.addAll(deletedContacts);
            assertThat(contactsAfterDB, new withElementsInOut<ContactData>(contactsBeforeDB,null,deletedContacts));
        }
        else {
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            contactsNum = contactsAfterUI.size();
            assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,null,deletedContacts));
        }
    }

    @Test(testName = "deleteAllContacts")
    public void deleteAllContacts(){
        appManager.getContactHelper().deleteAllContacts();
        appManager.getNavigationHelper().navigateToContactPage();
        if(useDB){
            assertThat(dbManager.getContactsList().size(),equalTo(0));
            processedContacts = contactsBeforeUI;
        }
        else
            assertThat(appManager.getContactHelper().getContactsList(),equalTo(0));
    }

    @AfterMethod
    public void verifyContactsInUI(Method method){
        if((useDB)&&(method.getName().equals("deleteContactsByIndices"))){
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,null,processedContacts));
        }
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
