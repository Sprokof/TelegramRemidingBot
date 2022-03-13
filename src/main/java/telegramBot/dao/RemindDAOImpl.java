package telegramBot.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;

@NoArgsConstructor
@Component
public class RemindDAOImpl implements RemindDAO {
    @Getter
    private final SessionFactory sessionFactory = new Configuration().
            configure("hibernate.cfg.xml").addAnnotatedClass(Remind.class).
            addAnnotatedClass(Details.class).buildSessionFactory();

    @Override
    public boolean deleteByID(int id) {
        Session session;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            if(session.get(Remind.class, id)!=null){
                session.delete(session.get(Remind.class, id));}
            session.getTransaction().commit();}
        catch (Exception e){
            e.printStackTrace();
        return false;}
        finally {
            this.sessionFactory.close();}
    return true;}

    @Override
    public boolean save(Remind remind, Details details) {
        remind.setDetails(details);
        Session session;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save((Remind) remind);
            session.getTransaction().commit();}
        catch (Exception e){
            e.printStackTrace();}
        finally {
            this.sessionFactory.close();}
        return true;}

    @Override
    public boolean update(Remind remind) {
        Session session;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(remind);
            session.getTransaction().commit();}
        catch (Exception e){
            e.printStackTrace();}
        finally {
            this.sessionFactory.close();}
        return true;}


    @Override
    public Remind getObjectByID(int id) {
            Session session;
            Remind remind = null;
            try{
                session = this.sessionFactory.getCurrentSession();
                session.beginTransaction();
                remind = session.get(Remind.class,id);
                session.getTransaction().commit();}
            catch (Exception e){
                System.out.println("error in get-method");;}
           finally {
            this.sessionFactory.close();}
            return remind;
    }
}
