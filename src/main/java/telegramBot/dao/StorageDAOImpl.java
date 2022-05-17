package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import telegramBot.entity.Storage;


@Component
public class StorageDAOImpl implements StorageDAO{

    private static final SessionFactory sessionFactory = InstanceSessionFactory.getInstance();



    @Override
    public void saveStorage(Storage storage) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(storage);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void updateStorage(Storage storage) {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(storage);
            session.getTransaction().commit();
        }
        catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Storage getStorage() {
        Session session = null;
        Storage storage = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            storage = session.get(Storage.class, 1);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return storage;
    }
}
