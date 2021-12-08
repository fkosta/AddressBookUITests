package qa.issart.com.models;

import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.*;
import static java.util.Calendar.DAY_OF_MONTH;

@XStreamAlias("contact")
@Entity
@Table(name = "addressbook")
public class ContactData {
    @XStreamOmitField
    @Id
    private int id;
    @Expose
    private String firstname;
    @Expose
    private String middlename;
    @Expose
    private String lastname;
    @Expose
    private String nickname;
    @Expose
    @Type(type = "text")
    private String photo;
    @Expose
    private String title;
    @Expose
    private String company;
    @Expose
    @Type(type="text")
    private String address;
    @Expose
    @Type(type="text")
    private String home;
    @Expose
    @Type(type="text")
    private String mobile;
    @Expose
    @Type(type="text")
    private String work;
    @Expose
    @Type(type="text")
    private String fax;
    @Expose
    @Type(type="text")
    private String email;
    @Expose
    @Type(type="text")
    private String email2;
    @Expose
    @Type(type="text")
    private String email3;
    @Expose
    @Type(type="text")
    private String homepage;
    @Expose
    @Type(type = "byte")
    private byte bday;
    @Expose
    private String bmonth;
    @Expose
    private String byear;
    @Expose
    @Type(type = "byte")
    private byte aday;
    @Expose
    private String amonth;
    @Expose
    private String ayear;
    @Expose
    @Type(type="text")
    private String address2;
    @Expose
    @Type(type="text")
    private String phone2;
    @Expose
    @Type(type="text")
    private String notes;
    @XStreamOmitField
    @Transient
    private String allEmails;
    @XStreamOmitField
    @Transient
    private String allPhones;
    @XStreamOmitField
    @Transient
    private boolean fullCMP = false;
    @XStreamOmitField
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="address_in_groups", joinColumns=@JoinColumn(name="id"), inverseJoinColumns=@JoinColumn(name="group_id"))
    private Set<GroupData> contactGroups = new HashSet<>();

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("d-MMMM-yyyy",Locale.ENGLISH);
    @Transient
    private Calendar calendar0 = new GregorianCalendar();
    @Transient
    private Calendar calendar1 = new GregorianCalendar();

    public ContactData(){
        fullCMP=false;
    }

    public ContactData(String firstname, String lastname, String address, String allEmails, String allPhones){
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.allEmails = allEmails;
        this.allPhones = allPhones;
    }

    public ContactData(String firstname, String middlename, String lastname, String nickname, String photo, String company,
                       String title, String address, String home, String mobile, String work, String fax, String email,
                       String email2, String email3, String homepage, String bday, String bmonth, String byear, String aday,
                       String amonth, String ayear, String phone2, String address2, String notes) {
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.nickname = nickname;
        this.photo = photo;
        this.company = company;
        this.title = title;
        this.address = address;
        this.home = home;
        this.mobile = mobile;
        this.work = work;
        this.fax = fax;
        this.email = email;
        this.email2 = email2;
        this.email3 = email3;
        this.homepage = homepage;
        this.bday = (byte)Integer.parseInt(bday);
        this.bmonth = bmonth;
        this.byear = byear;
        this.aday = (byte)Integer.parseInt(aday);
        this.amonth = amonth;
        this.ayear = ayear;
        this.phone2 = phone2;
        this.address2 = address2;
        this.notes = notes;
        this.fullCMP=true;
    }

    public int getId() {
        return id;
    }

    public String getBirthday(int dateFormat) {
        if(dateFormat==0)
            return String.valueOf(bday)+"-"+bmonth+"-"+byear;
        else {
            if(bmonth.length()>1)
                bmonth = bmonth.substring(0,1).toUpperCase()+bmonth.substring(1).toLowerCase();

            return String.valueOf(bday) + ". " + bmonth + " " + byear;
        }
    }

    public String getAnniversary(int dateFormat) {
        if(dateFormat==0)
            return String.valueOf(aday)+"-"+amonth+"-"+ayear;
        else {
            if(amonth.length()>1)
                amonth = amonth.substring(0,1).toUpperCase()+amonth.substring(1).toLowerCase();

            return String.valueOf(aday) + ". " + amonth + " " + ayear;
        }
    }

    public String getAllEmails(){
        return allEmails;
    }

    public String getAllPhones(){
        return allPhones;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getAddress() {
        return address;
    }

    public String getHome() {
        return home;
    }

    public String getMobile() {
        return mobile;
    }

    public String getWork() {
        return work;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getEmail2() {
        return email2;
    }

    public String getEmail3() {
        return email3;
    }

    public String getHomepage() {
        return homepage;
    }

    public Set<GroupData> getContactGroups(){
        return contactGroups;
    }

    public int getBday() {
        return bday;
    }

    public String getBmonth() {
        return bmonth;
    }

    public String getByear() {
        return byear;
    }

    public int getAday() {
        return aday;
    }

    public String getAmonth() {
        return amonth;
    }

    public String getAyear() {
        return ayear;
    }

    public String getAddress2() {
        return address2;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getNotes() {
        return notes;
    }

    public ContactData withId(int id) {
        this.id = id;
        return this;
    }

    public ContactData withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public ContactData withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public ContactData withMiddlename(String middlename) {
        this.middlename = middlename;
        return this;
    }

    public ContactData withNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public ContactData withPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    public ContactData withTitle(String title) {
        this.title = title;
        return this;
    }

    public ContactData withCompany(String company) {
        this.company = company;
        return this;
    }

    public ContactData withAddress(String address) {
        this.address = address;
        return this;
    }

    public ContactData withHome(String home) {
        this.home = home;
        return this;
    }

    public ContactData withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public ContactData withWork(String work) {
        this.work = work;
        return this;
    }

    public ContactData withFax(String fax) {
        this.fax = fax;
        return this;
    }

    public ContactData withEmail(String email) {
        this.email = email;
        return this;
    }

    public ContactData withEmail2(String email2) {
        this.email2 = email2;
        return this;
    }

    public ContactData withEmail3(String email3) {
        this.email3 = email3;
        return this;
    }

    public ContactData withHomepage(String homepage) {
        this.homepage = homepage;
        return this;
    }

    public ContactData withAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public ContactData withPhone2(String phone2) {
        this.phone2 = phone2;
        return this;
    }

    public ContactData withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public ContactData withBirthday(String birthday){
        String[] dayMonthYear = birthday.split("-");
        this.bday = (byte)Integer.parseInt(dayMonthYear[0]);
        this.bmonth = dayMonthYear[1];
        this.byear = dayMonthYear[2];
        return this;
    }

    public ContactData withAnniversary(String anniversary){
        String[] dayMonthYear = anniversary.split("-");
        this.aday = (byte)Integer.parseInt(dayMonthYear[0]);
        this.amonth = dayMonthYear[1];
        this.ayear = dayMonthYear[2];
        return this;
    }

    private String transform(String phone){
        String transformedPhone = phone.trim();
        transformedPhone = transformedPhone.replace(" ","").replace("-","");
        if (transformedPhone.startsWith("00"))
            transformedPhone="+"+transformedPhone.substring(2);

        return transformedPhone;
    }

    public ContactData composePhonesAndEmails(){
        StringBuilder sB = new StringBuilder();
        if(this.home.length()!=0)
            sB.append(transform(this.home));

        if(this.mobile.length()!=0) {
            if(sB.length()>0)
                sB.append("\n");

            sB.append(transform(this.mobile));
        }

        if(this.work.length()!=0) {
            if(sB.length()>0)
                sB.append("\n");

            sB.append(transform(this.work));
        }

        if(this.phone2.length()!=0){
            if(sB.length()>0)
                sB.append("\n");

            sB.append(transform(this.phone2));
        }

        this.allPhones = sB.toString();

        sB = new StringBuilder();
        if(this.email.length()>0)
            sB.append(this.email);

        if (this.email2.length()>0) {
            if (sB.length() > 0)
                sB.append("\n");

            sB.append(this.email2);
        }

        if (this.email3.length()>0) {
            if (sB.length() > 0)
                sB.append("\n");

            sB.append(this.email3);
        }

        this.allEmails = sB.toString();
        return this;
    }

    public ContactData setFullCMP(Boolean cmp){
        this.fullCMP = cmp;
        return this;
    }

    public void setContactGroups(Set<GroupData> contactGroups){
        this.contactGroups = contactGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactData that = (ContactData) o;
        if((this.fullCMP==false)||(that.fullCMP==false)) {
            return id == that.id &&
                    Objects.equals(firstname, that.firstname) &&
                    Objects.equals(lastname, that.lastname) &&
                    Objects.equals(address, that.address) &&
                    Objects.equals(allEmails, that.allEmails) &&
                    Objects.equals(allPhones, that.allPhones);
        }
        else{
            return id == that.id &&
                    Objects.equals(firstname, that.firstname) &&
                    Objects.equals(middlename, that.middlename) &&
                    Objects.equals(lastname, that.lastname) &&
                    Objects.equals(nickname, that.nickname) &&
                    Objects.equals(company, that.company) &&
                    Objects.equals(title, that.title) &&
                    Objects.equals(address, that.address) &&
                    Objects.equals(home, that.home) &&
                    Objects.equals(mobile, that.mobile) &&
                    Objects.equals(work, that.work) &&
                    Objects.equals(fax, that.fax) &&
                    Objects.equals(email, that.email) &&
                    Objects.equals(email2, that.email2) &&
                    Objects.equals(email3, that.email3)&&
                    Objects.equals(bday, that.bday)&&
                    Objects.equals(bmonth.toLowerCase(), that.bmonth.toLowerCase())&&
                    Objects.equals(byear, that.byear)&&
                    Objects.equals(aday, that.aday)&&
                    Objects.equals(amonth.toLowerCase(), that.amonth.toLowerCase())&&
                    Objects.equals(ayear, that.ayear)&&
                    Objects.equals(homepage, that.homepage) &&
                    Objects.equals(phone2, that.phone2) &&
                    Objects.equals(address2, that.address2) &&
                    Objects.equals(notes, that.notes);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, address, allEmails, allPhones);
    }

    @Override
    public String toString() {
        return "ContactData{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", address='" + address + '\'' +
                ", allEmails='" + allEmails + '\'' +
                ", allPhones='" + allPhones + '\'' +
                '}';
    }
    public String printContactInfo(boolean withGroups, boolean withLabels, int dateFormat){
        calendar0.setTime(new Date());
        StringBuilder contactInfo = new StringBuilder();
        contactInfo.append(firstname).append(" ").append(middlename).append(" ").append(lastname).append("\n").append(nickname);
        contactInfo.append("\n").append(title).append("\n").append(company).append("\n").append(address).append("\n");
        if(withLabels){
            contactInfo.append("H: ").append(home).append("\n").append("M: ").append(mobile).append("\n");
            contactInfo.append("W: ").append(work).append("\n").append("F: ").append(fax).append("\n");
        }
        else {
            contactInfo.append(home).append("\n").append(mobile).append("\n");
            contactInfo.append(work).append("\n").append(fax).append("\n");
        }

        contactInfo.append(allEmails).append("\n");

        String bAge = String.format(" (%s)",getAge(getBirthday(0)));
        String aAge = String.format(" (%s)",getAge(getAnniversary(0)));

        if(withLabels) {
            contactInfo.append("Homepage:\n").append(homepage).append("\n");
            contactInfo.append("Birthday ").append(getBirthday(dateFormat)).append(bAge).append("\n");
            contactInfo.append("Anniversary ").append(getAnniversary(dateFormat)).append(aAge).append("\n");
        }
        else {
            contactInfo.append(homepage).append("\n").append(getBirthday(dateFormat).toLowerCase()).append("\n");
            contactInfo.append(getAnniversary(dateFormat).toLowerCase()).append("\n");
        }

        contactInfo.append(address2).append("\n");

        if(withLabels)
            contactInfo.append("P: ").append(phone2).append("\n").append(notes);
        else
            contactInfo.append(phone2).append("\n").append(notes);

        if (withGroups) {
            List<GroupData> groupsList = contactGroups.stream().collect(Collectors.toList());
            groupsList.sort(Comparator.comparing((GroupData gD) -> gD.getId()));

            if(groupsList.size()>0) {
                contactInfo.append("\n").append("Member of: ");
                contactInfo.append(groupsList.stream().map(g -> g.getName()).collect(Collectors.joining(", ")));
            }
        }
        return contactInfo.toString();
    }

    private String getAge(String abDate){
        int yeardiff = 0;
        try {
            calendar1.setTime(sdf.parse(abDate));
            yeardiff = calendar0.get(YEAR) - calendar1.get(YEAR);
            if(calendar1.get(MONTH)>calendar0.get(MONTH))
                yeardiff = yeardiff - 1;
            else if((calendar0.get(MONTH)==calendar1.get(MONTH))&&(calendar0.get(DAY_OF_MONTH)<calendar1.get(DAY_OF_MONTH)))
                yeardiff = yeardiff - 1;
        }
        catch (ParseException pE){
            pE.printStackTrace();
        }

        return String.valueOf(yeardiff);
    }
}

