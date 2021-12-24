package qa.issart.com.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PrintPhonesPageTest extends TestBase{
    @BeforeTest
    public void getContactsFromDB(){
        contactsBeforeDB = dbManager.getContactsList();
        assertTrue(appManager.getNavigationHelper().navigateToPrintPhonesPage());
    }
    @Test
    public void validatePhonesPage(){
        Set<String> parsedContactsInfo = appManager.getContactHelper().parsePrintPage();
        Set<String> contactsHTMLFormatted = new HashSet<>();
        Iterator<ContactData> it = contactsBeforeDB.iterator();
        while (it.hasNext()){
            String formattedContact = it.next().printContactInfo(false, false,0);
            contactsHTMLFormatted.add(formattedContact);
        }
        assertEquals(contactsHTMLFormatted,parsedContactsInfo);
    }

}
