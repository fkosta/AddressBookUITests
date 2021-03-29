package qa.issart.com.tests;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.GroupData;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GroupCreationTests extends TestBase{

    private int iteration=0;

    @BeforeTest
    public void getAppGroups(){
        appManager.getNavigationHelper().navigateToGroupPage();
        groupsBeforeUI = appManager.getGroupHelper().getGroupsList();

        if(useDB) {
            groupsBeforeDB = dbManager.getGroupsList();
            assertThat(groupsBeforeDB, equalTo(groupsBeforeUI));
        }
    }

    @Test(dataProvider = "GroupsListFromFile")
    public void addToGroupList(GroupData newGroup){
        iteration++;
        appManager.getGroupHelper().addGroup(newGroup,iteration);
        Set<GroupData> addedGroup;
        appManager.getNavigationHelper().navigateBackToGroups();
        if(useDB) {
            groupsAfterDB = dbManager.getGroupsList();
            addedGroup = groupsAfterDB.stream().filter(g->!groupsBeforeDB.contains(g)).collect(Collectors.toSet());
        }
        else{
            groupsAfterUI = appManager.getGroupHelper().getGroupsList();
            addedGroup = groupsAfterUI.stream().filter((g)->(!groupsBeforeUI.contains(g))).collect(Collectors.toSet());
        }

        newGroup = newGroup.withId(addedGroup.iterator().next().getId());
        assertThat(addedGroup, new withElementsInOut<GroupData>(newGroup));

        if(useDB) {
            processedGroups.add(newGroup);
            groupsBeforeDB.add(newGroup);
        }
        else
            groupsBeforeUI.add(newGroup);
    }

    @AfterTest
    public void verifyGroupsInUI(){
        if(useDB){
            groupsAfterUI=appManager.getGroupHelper().getGroupsList();
            assertThat(groupsAfterUI, new withElementsInOut<GroupData>(groupsBeforeUI, processedGroups,"creation"));
        }
    }
}
