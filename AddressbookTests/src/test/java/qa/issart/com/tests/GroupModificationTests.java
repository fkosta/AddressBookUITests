package qa.issart.com.tests;

import org.testng.annotations.*;
import qa.issart.com.models.GroupData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GroupModificationTests extends TestBase{
    private int iteration=0;
    private int groupsNum;
    private Map<Integer, GroupData> changedGroups = new HashMap<>();
    private Map<Integer, GroupData> enteredGroups = new HashMap<>();

    @BeforeTest
    @Parameters({"dataFileName"})
    public void getGroupsInApp(@Optional String dataFileName) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(dataFileName!=null)
            dataFile = dataFileName;

        appManager.getNavigationHelper().navigateToGroupPage();
        groupsBeforeUI = appManager.getGroupHelper().getGroupsList();

        if(groupsBeforeUI.size()<10){
            addGroupsToAddressbook(10-groupsBeforeUI.size());
            groupsBeforeUI = appManager.getGroupHelper().getGroupsList();
        }

        if(useDB) {
            groupsBeforeDB = dbManager.getGroupsList();
            assertThat(groupsBeforeDB, equalTo(groupsBeforeUI));
        }
        groupsNum = groupsBeforeUI.size();
    }

    @Test(dataProvider = "GroupsListFromFile")
    public void modifyGroupFromList(GroupData newGroup){
        iteration++;
        GroupData modifiedGroup = appManager.getGroupHelper().modifyGroup(newGroup,iteration,rand.nextInt(groupsNum));
        newGroup.withId(modifiedGroup.getId());
        logger.info(buildLogEntry(modifiedGroup, newGroup));
        appManager.getNavigationHelper().navigateBackToGroups();
        if(useDB){
            enteredGroups.put(newGroup.getId(),newGroup);
            changedGroups.putIfAbsent(modifiedGroup.getId(),modifiedGroup);
            groupsAfterDB = dbManager.getGroupsList();
            assertThat(groupsAfterDB, new withElementsInOut(groupsBeforeDB,newGroup,modifiedGroup));
            groupsBeforeDB.add(newGroup);
            groupsBeforeDB.remove(modifiedGroup);
        }
        else{
            groupsAfterUI = appManager.getGroupHelper().getGroupsList();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI,newGroup,modifiedGroup));
            groupsBeforeUI.remove(modifiedGroup);
            groupsBeforeUI.add(newGroup);
        }
    }

    @AfterTest
    public void verifyGroupsInUI(){
        if(useDB){
            groupsAfterUI = appManager.getGroupHelper().getGroupsList();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI, enteredGroups.values(), changedGroups.values()));
        }
    }

    private String buildLogEntry(GroupData modifiedGroup, GroupData newGroup) {
        StringBuilder sB = new StringBuilder("Modified group with id: "+newGroup.getId());
        if(modifiedGroup.getName().equals(newGroup.getName()))
            sB.append(". Visible attributes has not changed");
        else
            sB.append(" new group name: "+newGroup.getName()+" old: "+modifiedGroup.getName());

        return sB.toString();
    }
}
