package qa.issart.com.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public class PrintContactsPageTest extends TestBase{
    @BeforeTest
    public void getContactsFromPage(){
        contactsBeforeDB = dbManager.getContactsList();
        assertTrue(appManager.getNavigationHelper().navigateToPrintAllPage());
    }

    @Test
    public void validatePrintPage(){
        Set<String> parsedContactsInfo = appManager.getContactHelper().parsePrintPage();
        Set<String> contactsHTMLFormatted = new HashSet<>();
        Iterator<ContactData> it = contactsBeforeDB.iterator();
        while (it.hasNext()){
            String formattedContact = it.next().printContactInfo(false, true, 1);
            contactsHTMLFormatted.add(formattedContact);
        }
        assertEquals(contactsHTMLFormatted,parsedContactsInfo);
    }
}
