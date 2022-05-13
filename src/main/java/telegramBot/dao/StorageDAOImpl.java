package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import telegramBot.entity.Storage;


@Component
public class StorageDAOImpl implements StorageDAO{

    @Getter
    private final SessionFactory sessionFactory =
            new Configuration().configure("hibernate.cfg.xml").
                    addAnnotatedClass(Storage.class).buildSessionFactory();


    @Override
    public void saveStorage(Storage storage) {
        Session session = null;
     try{
         session = this.sessionFactory.getCurrentSession();
         session.beginTransaction();
         session.save(storage);
         session.getTransaction().commit();
     }
     catch (Exception e){e.printStackTrace();}
     finally {
         this.sessionFactory.close();
     }
    }

    @Override
    public void updateStorage(Storage storage) {
        Session session = null;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(storage);
            session.getTransaction().commit();
        }
        catch (Exception e){e.printStackTrace();}
        finally {
            this.sessionFactory.close();
        }
    }

    @Override
    public Storage getStorage() {
        Session session = null;
        Storage storage = null;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            storage = session.get(Storage.class, 1);
            session.getTransaction().commit();
        }
        catch (Exception e){e.printStackTrace();}
        finally {
            this.sessionFactory.close();
        }
    return storage;
    }
}
