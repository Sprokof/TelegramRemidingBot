package telegramBot.hidenPackage.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import telegramBot.hidenPackage.entity.RemindDPer;

@Component
public class RemindDaoImpl implements Dao {

    @Getter
    private final SessionFactory sessionFactory = new Configuration().
            configure("hibernate.cfg.xml").addAnnotatedClass(RemindDPer.class).buildSessionFactory();

    @Override
    public RemindDPer getRemindById(int id) {
        Session session;
        RemindDPer remindDPer = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        remindDPer = session.get(RemindDPer.class, id);
        session.getTransaction().commit();}
    catch (Exception e){ e.printStackTrace(); }
    finally { this.sessionFactory.close(); }
    return remindDPer;

    }

    @Override
    public boolean updateRemind(RemindDPer remindDPer) {
        Session session;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(remindDPer);
            session.getTransaction().commit();}
        catch (Exception e){ return false;}
        finally { this.sessionFactory.close(); }
        return true;

    }
}
