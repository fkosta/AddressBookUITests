package qa.issart.com.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import qa.issart.com.models.GroupData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupHelper extends BaseHelper{
    public GroupHelper(WebDriver wD) {
        super(wD);
    }

    public Set<GroupData> getGroupsList() {
        Set<GroupData> groupsList = new HashSet<>();
        List<WebElement> weList = wD.findElements(By.className("group"));
        String groupName;
        int groupId;
        for (WebElement webElement : weList) {
            groupName = webElement.getText();
            groupId = Integer.parseInt(webElement.findElement(By.tagName("input")).getAttribute("value"));
            groupsList.add(new GroupData(groupName, "", "").withId(groupId));
        }
        return groupsList;
    }

    public void fillGroupFields(GroupData newGroup){
        type(By.name("group_name"), newGroup.getName());
        type(By.name("group_header"),newGroup.getHeader());
        type(By.name("group_footer"),newGroup.getFooter());
    }

    public void addGroup(GroupData newGroup,int iteration) {
        int ind=4;
        if(iteration%2==1)
            ind=1;

        click(By.xpath(String.format("/html/body/div/div[4]/form/input[%d]",ind)));
        fillGroupFields(newGroup);
        click(By.name("submit"));
    }

    public GroupData modifyGroup(GroupData newGroup, int iteration, int pnt){
        int ind=6;
        if(iteration%2==1)
            ind=3;

        String groupId = wD.findElement(By.xpath(String.format("/html/body/div/div[4]/form/span[%d]/input",pnt+1))).getAttribute("value");
        click(By.xpath(String.format("/html/body/div/div[4]/form/span[%d]/input",pnt+1)));
        click(By.xpath(String.format("/html/body/div/div[4]/form/input[%d]",ind)));
        String groupName = wD.findElement(By.name("group_name")).getAttribute("value");
        String groupHeader = wD.findElement(By.name("group_header")).getAttribute("value");
        String groupFooter = wD.findElement(By.name("group_footer")).getAttribute("value");
        fillGroupFields(newGroup);
        click(By.name("update"));
        return new GroupData(groupName,groupHeader,groupFooter).withId(Integer.parseInt(groupId));
    }

    public Set<GroupData> deleteGroups(List<Integer> indices, int iteration) {
        int ind=5;
        if(iteration%2==1)
            ind=2;

        Set<GroupData> deletedGroups = new HashSet<>();
        String groupId;
        String groupName;

        for(int id:indices){
            groupId = wD.findElement(By.xpath(String.format("/html/body/div/div[4]/form/span[%d]/input",id))).getAttribute("value");
            groupName = wD.findElement(By.xpath(String.format("/html/body/div/div[4]/form/span[%d]",id))).getText();
            click(By.xpath(String.format("/html/body/div/div[4]/form/span[%d]/input",id)));
            deletedGroups.add(new GroupData(groupName,"","").withId(Integer.parseInt(groupId)));
        }
        click(By.xpath(String.format("/html/body/div/div[4]/form/input[%d]",ind)));
        return deletedGroups;
    }

    public void deleteAllGroups(int groupsNum) {
        for (int j=1;j<groupsNum+1;j++)
            click(By.xpath(String.format("/html/body/div/div[4]/form/span[%d]/input",j)));

        click(By.xpath("/html/body/div/div[4]/form/input[2]"));
    }
}
