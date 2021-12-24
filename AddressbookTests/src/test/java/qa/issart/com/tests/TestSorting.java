package qa.issart.com.tests;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class TestSorting extends TestBase{

    List<ContactData> contactsList0;
    List<ContactData> contactsList1;

    @BeforeTest
    public void getContactsFromUI(){
        appManager.getNavigationHelper().navigateToContactPage();
        contactsList0 = appManager.getContactHelper().getContactsList0();
    }

    @Test
    public void testSortingContacts(){

        String[] sortTypes = {"Last name","Last name","First name","First name","Address",
                "Address","All e-mail","All e-mail","All phones","All phones"};
        Integer k0=1;
        for(String sType:sortTypes){
            appManager.getContactHelper().sortContactsList(sType);
            contactsList1 = appManager.getContactHelper().getContactsList0();
            Integer finalK = k0;
            contactsList0.sort(new Comparator<ContactData>() {
                @Override
                public int compare(ContactData o1, ContactData o2) {
                    if(sType.equals("First name"))
                        return finalK *o1.getFirstname().compareToIgnoreCase(o2.getFirstname());
                    else if(sType.equals("Last name"))
                        return finalK *o1.getLastname().compareToIgnoreCase(o2.getLastname());
                    else if(sType.equals("Address"))
                        return finalK *o1.getAddress().compareToIgnoreCase(o2.getAddress());
                    else if(sType.equals("All e-mail"))
                        return finalK *o1.getAllEmails().compareToIgnoreCase(o2.getAllEmails());
                    else if(sType.equals("All phones"))
                        return finalK *o1.getAllPhones().compareToIgnoreCase(o2.getAllPhones());
                    else
                        return 0;
                }
            });
            k0*=-1;
            assertEquals(contactsList0,contactsList1);
        }
    }
    @AfterTest
    public void navigateToHomePage(){
        appManager.getNavigationHelper().navigateToPage(appManager.getTestsProperties().getProperty("app.url"));
    }
}
