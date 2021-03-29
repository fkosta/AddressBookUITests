package qa.issart.com.tests;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.GroupData;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GroupModificationTests extends TestBase{
    private int iteration=0;
    private int groupsNum;
    private Set<GroupData> changedGroups = new HashSet<>();

    @BeforeTest
    public void getGroupsInApp(){
        appManager.getNavigationHelper().navigateToGroupPage();
        groupsBeforeUI = appManager.getGroupHelper().getGroupsList();

        if(useDB) {
            groupsBeforeDB = dbManager.getGroupsList();
            assertThat(groupsBeforeDB, equalTo(groupsBeforeUI));
        }
        groupsNum = groupsBeforeUI.size();
    }

    @Test(dataProvider = "GroupsListFromFile")
    public void modifyGroupFromList(GroupData newGroup){
        iteration++;
        GroupData modifyedGroup = appManager.getGroupHelper().modifyGroup(newGroup,iteration,rand.nextInt(groupsNum));
        newGroup.withId(modifyedGroup.getId());
        logger.info("The modified group is "+modifyedGroup.toString());
        appManager.getNavigationHelper().navigateBackToGroups();
        if(useDB){
            groupsAfterDB = dbManager.getGroupsList();
            assertThat(groupsAfterDB, new withElementsInOut(groupsBeforeDB,newGroup,modifyedGroup));
            groupsBeforeDB.add(newGroup);
            groupsBeforeDB.remove(modifyedGroup);
            processedGroups.add(newGroup);
            changedGroups.add(modifyedGroup);
        }
        else{
            groupsAfterUI = appManager.getGroupHelper().getGroupsList();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI,newGroup,modifyedGroup));
            groupsBeforeUI.remove(modifyedGroup);
            groupsBeforeUI.add(newGroup);
        }
    }

    @AfterTest
    public void verifyGroupsInUI(){
        if(useDB){
            groupsAfterUI = appManager.getGroupHelper().getGroupsList();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI, processedGroups, changedGroups));
        }
    }
}
