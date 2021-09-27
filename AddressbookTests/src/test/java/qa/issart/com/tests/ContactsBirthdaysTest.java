package qa.issart.com.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.ContactData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Calendar.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ContactsBirthdaysTest extends TestBase{
    Map<String, Set<ContactData>> BDMap= new HashMap();
    Set<ContactData> contactsSet;
    ContactData nextContact;
    SimpleDateFormat sdf = new SimpleDateFormat("d-MMMM-yyyy",Locale.ENGLISH);
    String bmonth;
    Date currentDate = new Date();
    Calendar calendar0 = new GregorianCalendar();
    Calendar calendar1 = new GregorianCalendar();
    String str0;

    @BeforeTest
    public void getContactsSortedByBdays() throws ParseException {
        contactsBeforeDB = dbManager.getContactsList();
        calendar0.setTime(currentDate);
        Iterator it = contactsBeforeDB.iterator();
        while (it.hasNext()){
            nextContact = (ContactData) it.next();
            calendar1.setTime(sdf.parse(nextContact.getBirthday()));
            bmonth = nextContact.getBmonth();

            if((calendar1.get(MONTH) < calendar0.get(MONTH))||
                    ((calendar1.get(MONTH)==calendar0.get(MONTH))&&(calendar1.get(DAY_OF_MONTH)<calendar0.get(DAY_OF_MONTH))))
                bmonth = bmonth+" "+ (calendar0.get(YEAR) + 1);

            if(BDMap.containsKey(bmonth)){
                contactsSet = BDMap.get(bmonth);
            }
            else {
                contactsSet = new HashSet<>();
            }
            contactsSet.add(nextContact);
            BDMap.put(bmonth, contactsSet);
        }

        assertTrue(appManager.getNavigationHelper().navigateToBirthdaysList());
    }

    @Test
    public void birthdayPageTest() throws ParseException {
        Map<String, Set<String>> BPMap = appManager.getContactHelper().getContactsMap();
        assertEquals(BDMap.size(),BPMap.size());
        for(Map.Entry<String, Set<String>> cB:BPMap.entrySet()){
            contactsSet = BDMap.get(cB.getKey());
            Iterator<ContactData> itContacts = contactsSet.iterator();
            Set<String> contactAttributesBD = new HashSet<>();
            while(itContacts.hasNext()){
                nextContact = itContacts.next();
                str0 = ";"+nextContact.getBday()+".;"+nextContact.getMiddlename()+" "+nextContact.getLastname()+
                        ";"+nextContact.getFirstname()+";"+getCurrentAge(nextContact.getBirthday())+";"+
                        nextContact.getEmail()+";"+nextContact.getHome();
                contactAttributesBD.add(str0);
            }
            assertEquals(cB.getValue(),contactAttributesBD);
        }
    }

    private String getCurrentAge(String bDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("d-MMMM-yyyy",Locale.ENGLISH);
        Date date1 = sdf.parse(bDate);
        calendar1.setTime(date1);
        int year0 = calendar0.get(YEAR);
        int year1 = calendar1.get(YEAR);
        int yeardiff = year0 - year1;
        if(calendar1.get(MONTH)>calendar0.get(MONTH))
            yeardiff = yeardiff - 1;
        else if((calendar0.get(MONTH)==calendar1.get(MONTH))&&(calendar0.get(DAY_OF_MONTH)<calendar1.get(DAY_OF_MONTH)))
            yeardiff = yeardiff - 1;

        return String.valueOf(yeardiff);
    }
}
