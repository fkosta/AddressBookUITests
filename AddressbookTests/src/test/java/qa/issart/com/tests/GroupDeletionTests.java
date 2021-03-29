package qa.issart.com.tests;

import org.testng.Assert;
import org.testng.annotations.*;
import qa.issart.com.models.GroupData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

public class GroupDeletionTests extends TestBase{
    private int groupsNum;
    private int iteration=0;

    @DataProvider(name = "listOfIndices")
    public Iterator<Object[]> listOfIndexes(){
        List<Object[]> retList = new ArrayList<>();
        retList.add(new Object[]{new String[]{"F"}});
        retList.add(new Object[]{new String[]{"L"}});
        retList.add(new Object[]{new String[]{"F","L"}});
        retList.add(new Object[]{new String[]{"F","L","R"}});
        return retList.iterator();
    }

    @BeforeTest
    public void getGroupsInApp(){
        appManager.getNavigationHelper().navigateToGroupPage();
        groupsBeforeUI = appManager.getGroupHelper().getGroupsList();
        if(useDB){
            groupsBeforeDB = dbManager.getGroupsList();
            Assert.assertEquals(groupsBeforeDB,groupsBeforeUI);
        }
        groupsNum = groupsBeforeUI.size();
    }

    @Test(dataProvider = "listOfIndices")
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
            assertThat(groupsAfterDB, new withElementsInOut(groupsBeforeDB,deletedGroups,"deletion"));
        }
        else{
            groupsAfterUI = appManager.getGroupHelper().getGroupsList();
            groupsNum = groupsAfterUI.size();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI,deletedGroups,"deletion"));
        }
    }

    @AfterTest
    public void verifyGroupsInUI(){
        if(useDB){
            groupsAfterUI=appManager.getGroupHelper().getGroupsList();
            assertThat(groupsAfterUI, new withElementsInOut(groupsBeforeUI,processedGroups,"deletion"));
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
