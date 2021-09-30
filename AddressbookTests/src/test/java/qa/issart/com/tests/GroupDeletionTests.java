package qa.issart.com.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qa.issart.com.models.GroupData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GroupDeletionTests extends TestBase{
    private int groupsNum;
    private int iteration=0;

    @BeforeTest
    public void getGroupsInApp() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        appManager.getNavigationHelper().navigateToGroupPage();
        groupsBeforeUI = appManager.getGroupHelper().getGroupsList();

        if(groupsBeforeUI.size()<10){
            addGroupsToAddressbook(10-groupsBeforeUI.size());
            groupsBeforeUI = appManager.getGroupHelper().getGroupsList();
        }

        if(useDB){
            groupsBeforeDB = dbManager.getGroupsList();
            Assert.assertEquals(groupsBeforeDB,groupsBeforeUI);
        }
        groupsNum = groupsBeforeUI.size();
    }

    @Test(dataProvider = "listOfIndices", testName = "deleteByIndices")
    public void deleteGroupsByIndices(String[] indList){
        iteration++;
        List<Integer> indices = parceIndList(indList, groupsNum);
        Set<GroupData> deletedGroups = appManager.getGroupHelper().deleteGroups(indices,iteration);
        appManager.getNavigationHelper().navigateBackToGroups();
        logger.info("Indices for groups are "+indices.toString());
        logger.info("Deleted groups are "+deletedGroups.toString());

        if(useDB){
            groupsAfterDB = dbManager.getGroupsList();
            groupsNum = groupsAfterDB.size();
            processedGroups.addAll(deletedGroups);
            assertThat(groupsAfterDB, new withElementsInOut(groupsBeforeDB,null,deletedGroups));
        }
        else{
            groupsAfterUI = appManager.getGroupHelper().getGroupsList();
            groupsNum = groupsAfterUI.size();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI, null, deletedGroups));
        }
    }

    @Test(testName = "deleteAllGroups")
    public void deleteAllGroups(){
        appManager.getGroupHelper().deleteAllGroups(groupsNum);
        appManager.getNavigationHelper().navigateBackToGroups();
        if(useDB) {
            assertThat(dbManager.getGroupsList().size(), equalTo(0));
            processedGroups = groupsBeforeUI;
        }
        else
            assertThat(appManager.getGroupHelper().getGroupsList().size(), equalTo(0));
    }

    @AfterMethod
    public void verifyGroupsInUI(Method method){
        if((useDB)&&(method.getName().equals("deleteGroupsByIndices"))){
            groupsAfterUI=appManager.getGroupHelper().getGroupsList();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI, null, processedGroups));
        }
    }

    private List<Integer> parceIndList(String[] indList, int groupsNum) {
        List<Integer> indices = new ArrayList<>();
        for(String key:indList){
            if(key.equals("F"))
                indices.add(1);
            else if(key.equals("L"))
                indices.add(groupsNum);
            else if(key.equals("R"))
                indices.add(rand.nextInt(groupsNum-2)+2);
            else
                break;
        }
        return indices;
    }
}
