package qa.issart.com.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.openqa.selenium.remote.BrowserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.*;
import qa.issart.com.generators.DataGenerator;
import qa.issart.com.helpers.ApplicationManager;
import qa.issart.com.helpers.DBManager;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Listeners(TestListener.class)
public class TestBase {
    static final ApplicationManager appManager = new ApplicationManager(System.getProperty("browser", BrowserType.FIREFOX));
    static final DBManager dbManager = new DBManager();
    protected String dataFilePath="src/test/resources/";
    protected String dataFile;
    public static boolean useDB = true;
    public Set<GroupData> groupsAfterDB;
    public Set<GroupData> groupsBeforeUI;
    public Set<GroupData> groupsAfterUI;
    public Set<GroupData> groupsBeforeDB;
    public Set<GroupData> processedGroups = new HashSet<>();
    public Set<ContactData> contactsBeforeUI;
    public Set<ContactData> contactsAfterUI;
    public Set<ContactData> contactsBeforeDB;
    public Set<ContactData> contactsAfterDB;
    public Set<ContactData> processedContacts = new HashSet<>();

    protected final Random rand = new Random();
    static Logger logger = LoggerFactory.getLogger(TestBase.class);

    @BeforeSuite
    public void setUp(ITestContext context) throws IOException {
        appManager.init();
        context.setAttribute("app",appManager);
        if(System.getProperty("useDB").equals("false"))
            useDB = false;
        else
            dbManager.init();
    }

    @BeforeMethod
    public void logTestStart(Method m){
        logger.info("Started test "+m.getName());
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown(){
        appManager.stop();
        if (useDB)
            dbManager.close();
    }

    @DataProvider(name = "GroupsListFromFile")
    protected Iterator<Object[]> getGroupsFromFile() throws IOException {
        List<GroupData> testGroups = new ArrayList<>();
        if(dataFile==null)
            dataFile = System.getProperty("testData","");

        File testData = new File(dataFilePath+dataFile);
        if(testData.exists()&&testData.isFile()){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(testData));
            String fileType = detectFileExtension(dataFile);
            String dataLine;
            if(fileType.equals("csv")){
                String[] groupsData;
                while((dataLine=bufferedReader.readLine())!=null){
                    groupsData = dataLine.split(",");
                    testGroups.add(new GroupData(groupsData[0],groupsData[1],groupsData[2],true));
                }
            }
            else if(fileType.equals("xml")||fileType.equals("json")){
                StringBuilder groupsData = new StringBuilder();
                while ((dataLine = bufferedReader.readLine())!=null){
                    groupsData.append(dataLine);
                }
                if(fileType.equals("xml")){
                    XStream xStream = new XStream();
                    xStream.processAnnotations(GroupData.class);
                    xStream.addPermission(AnyTypePermission.ANY);
                    testGroups = (ArrayList<GroupData>)xStream.fromXML(groupsData.toString());
                }
                else{
                    Gson gson = new Gson();
                    testGroups = gson.fromJson(String.valueOf(groupsData),new TypeToken<List<GroupData>>(){}.getType());
                }
            }
            else
                System.err.println("Unsupported file format");
        }
        return testGroups.stream().map(g->new Object[]{g}).collect(Collectors.toList()).iterator();
    }

    @DataProvider(name = "ContactsListFromFile")
    protected Iterator<Object[]> getContactsFromFile() throws IOException {
        List<ContactData> testContacts = new ArrayList<>();
        if(dataFile==null)
            dataFile = System.getProperty("testData","");

        File testData = new File(dataFilePath+dataFile);
        if(testData.exists()&&testData.isFile()){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(testData));
            String fileType = detectFileExtension(dataFile);
            String dataLine;
            if(fileType.equals("csv")){
                String[] contactsData, bdata, adata;
                while((dataLine=bufferedReader.readLine())!=null){
                    contactsData = dataLine.split(",");
                    bdata = contactsData[16].split("-");
                    adata = contactsData[17].split("-");
                    testContacts.add(new ContactData(contactsData[0],contactsData[1],contactsData[2],contactsData[3],contactsData[4]
                            ,contactsData[5],contactsData[6],contactsData[7],contactsData[8],contactsData[9],contactsData[10]
                            ,contactsData[11],contactsData[12],contactsData[13],contactsData[14],contactsData[15],bdata[0],bdata[1],bdata[2]
                            ,adata[0],adata[1],adata[2],contactsData[18],contactsData[19],contactsData[20]));
                }
            }
            else if(fileType.equals("xml")||fileType.equals("json")){
                StringBuilder contactsData = new StringBuilder();
                while ((dataLine = bufferedReader.readLine())!=null){
                    contactsData.append(dataLine);
                }
                if(fileType.equals("xml")){
                    XStream xStream = new XStream();
                    xStream.processAnnotations(ContactData.class);
                    xStream.addPermission(AnyTypePermission.ANY);
                    testContacts = (ArrayList<ContactData>)xStream.fromXML(contactsData.toString());
                }
                else{
                    Gson gson = new Gson();
                    testContacts = gson.fromJson(String.valueOf(contactsData),new TypeToken<List<ContactData>>(){}.getType());
                }
            }
            else
                System.err.println("Unsupported file format");
        }
        return testContacts.stream().map(c->new Object[]{c.composePhonesAndEmails().setFullCMP(true)}).collect(Collectors.toList()).iterator();
    }

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

    private String detectFileExtension(String dataFile) {
        String[] fileNameParts = dataFile.split("\\.");
        return fileNameParts[fileNameParts.length-1];
    }

    protected List<ContactData> generateABContactList(int recordsNum) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        DataGenerator dataGenerator = new DataGenerator("groups","r",recordsNum,"generator.properties");
        dataGenerator.initParameters();
        DataGenerator.Generator generator = dataGenerator.new Generator(GroupData.class);
        ((DataGenerator.Generator<?>) generator).generateDataList();
        return generator.dataList;
    }

    protected void addGroupsToAddressbook(int recordsNum) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        DataGenerator dataGenerator = new DataGenerator("groups","r",recordsNum,"generator.properties");
        dataGenerator.initParameters();
        DataGenerator.Generator generator = dataGenerator.new Generator(GroupData.class);
        ((DataGenerator.Generator<?>) generator).generateDataList();

        for(int j = 0; j< ((DataGenerator.Generator<?>) generator).dataList.size(); j++){
            GroupData dataListEntry = (GroupData) generator.dataList.get(j);
            appManager.getGroupHelper().addGroup(dataListEntry,0);
            appManager.getNavigationHelper().navigateBackToGroups();
        }
    }

    protected void addContactsToAddressbook(int recordsNum) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        DataGenerator dataGenerator = new DataGenerator("contacts","r",recordsNum,"generator.properties");
        dataGenerator.initParameters();
        DataGenerator.Generator generator = dataGenerator.new Generator(ContactData.class);
        ((DataGenerator.Generator<?>) generator).generateDataList();

        for(int j = 0; j< ((DataGenerator.Generator<?>) generator).dataList.size(); j++){
            ContactData dataListEntry = (ContactData) generator.dataList.get(j);
            appManager.getContactHelper().addContact(dataListEntry,0,null);
        }
    }

}
