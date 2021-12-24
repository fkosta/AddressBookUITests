package qa.issart.com.models;

import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@XStreamAlias("group")
@Entity
@Table(name = "group_list")
public class GroupData {
    @XStreamOmitField
    @Id
    private int group_id;
    @Expose
    private String group_name;
    @Expose
    @Type(type = "text")
    private String group_header;
    @Expose
    @Type(type = "text")
    private String group_footer;
    @XStreamOmitField
    @Transient
    private boolean fullCMP;
    @XStreamOmitField
    @ManyToMany(mappedBy = "contactGroups",fetch = FetchType.EAGER)
    private Set<ContactData> groupContacts = new HashSet<>();

    public GroupData(){
        fullCMP = false;
    }

    public GroupData(String group_name, String group_header, String group_footer) {
        this.group_name = group_name;
        this.group_header = group_header;
        this.group_footer = group_footer;
        this.fullCMP = false;
    }

    public GroupData(String group_name, String group_header, String group_footer, boolean fullCMP){
        this.group_name = group_name;
        this.group_header = group_header;
        this.group_footer = group_footer;
        this.fullCMP = fullCMP;
    }

    public int getId() {
        return group_id;
    }

    public String getName() {
        return group_name;
    }

    public String getHeader() {
        return group_header;
    }

    public String getFooter() {
        return group_footer;
    }

    public Set<ContactData> getGroupContacts(){
        return groupContacts.stream().map(c->c.composePhonesAndEmails()).collect(Collectors.toSet());
    }

    public GroupData withId(int group_id) {
        this.group_id = group_id;
        return this;
    }

    public GroupData withName(String group_name) {
        this.group_name = group_name;
        return this;
    }

    public GroupData withHeader(String group_header) {
        this.group_header = group_header;
        return this;
    }

    public GroupData withFooter(String group_footer) {
        this.group_footer = group_footer;
        return this;
    }

    public GroupData withGroupContacts(Set<ContactData> groupContacts){
        this.groupContacts = groupContacts;
        return this;
    }

    public GroupData setFullCMP(Boolean fullCMP){
        this.fullCMP=fullCMP;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupData that = (GroupData) o;
        if((!this.fullCMP)||(!that.fullCMP))
            return (group_id == that.group_id)&&Objects.equals(group_name, that.group_name);
        else
            return (group_id == that.group_id)&&Objects.equals(group_name, that.group_name)&&
                    Objects.equals(group_header, that.group_header)&&Objects.equals(group_footer, that.group_footer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group_id, group_name);
    }

    @Override
    public String toString() {
        return "GroupData{" +
                "group_id=" + group_id +
                ", group_name='" + group_name + '\'' +
                '}';
    }
}
