package qa.issart.com.helpers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import qa.issart.com.models.ContactData;
import qa.issart.com.models.GroupData;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DBManager {
    private SessionFactory sessionFactory;
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

    public void init() {
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }
        catch(Exception e){
            e.printStackTrace();
            close();
        }
    }

    public DBManager() {
    }

    public Set<GroupData> getGroupsList(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<GroupData> groupsFromDB = session.createQuery("from GroupData").list();
        session.getTransaction().commit();
        session.close();
        return groupsFromDB.stream().map(g -> g.setFullCMP(true)).collect(Collectors.toSet());
    }

    public Set<ContactData> getContactsList(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<ContactData> contactsFromDB = session.createQuery("from ContactData where deprecated = ''").list();
        session.getTransaction().commit();
        session.close();
        return contactsFromDB.stream().map(c->c.composePhonesAndEmails().setFullCMP(true)).collect(Collectors.toSet());
    }

    public void close(){
        StandardServiceRegistryBuilder.destroy(registry);
    }
}
