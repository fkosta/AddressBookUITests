package qa.issart.com.tests;

import org.testng.Assert;
import org.testng.annotations.*;
import qa.issart.com.models.ContactData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

public class ContactDeletionTests extends TestBase{
    private int contactsNum;

    @DataProvider(name = "listOfIndices")
    public Iterator<Object[]> listOfIndexes(){
        List<Object[]> retList = new ArrayList<>();
        retList.add(new Object[]{new String[]{"F"}});
        retList.add(new Object[]{new String[]{"L"}});
        retList.add(new Object[]{new String[]{"R"}});
        retList.add(new Object[]{new String[]{"F","L"}});
        retList.add(new Object[]{new String[]{"F","L","R"}});
        return retList.iterator();
    }

    @BeforeTest
    public void getContactsInApp(){
        appManager.getNavigationHelper().navigateToContactPage();
        contactsBeforeUI = appManager.getContactHelper().getContactsList();
        contactsNum = contactsBeforeUI.size();
        if(useDB){
            contactsBeforeDB = dbManager.getContactsList();
            Assert.assertEquals(contactsBeforeDB,contactsBeforeUI);
        }
    }

    @Test(dataProvider = "listOfIndices")
    public void deleteContactsByIndices(String[] indList){
        List<Integer> indices = parceIndList(indList, contactsNum);
        Set<ContactData> deletedContacts = appManager.getContactHelper().deleteContacts(indices);
        logger.info("Indices for contacts are "+indices.toString());
        logger.info("Deleted contacts are "+deletedContacts.toString());
        if(useDB){
            contactsAfterDB = dbManager.getContactsList();
            contactsNum = contactsAfterDB.size();
            processedContacts.addAll(deletedContacts);
            assertThat(contactsAfterDB, new withElementsInOut<ContactData>(contactsBeforeDB,deletedContacts,"deletion"));
        }
        else {
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            contactsNum = contactsAfterUI.size();
            assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,deletedContacts,"deletion"));
        }
    }

    @AfterTest
    public void verifyContactsInUI(){
        if(useDB){
            contactsAfterUI = appManager.getContactHelper().getContactsList();
            assertThat(contactsAfterUI, new withElementsInOut<ContactData>(contactsBeforeUI,processedContacts,"deletion"));
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

}
