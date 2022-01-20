package telegramBot.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import telegramBot.entity.Notice;


@NoArgsConstructor
public class NoticeDAOImpl implements NoticeDAO {
    @Getter
    private final SessionFactory sessionFactory = new Configuration().
            configure("hibernate.cfg.xml").addAnnotatedClass(Notice.class).buildSessionFactory();

    @Override
    public boolean deleteByID(int id) {
        Session session;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            if(session.get(Notice.class, id)!=null){
                session.delete(session.get(Notice.class, id));}
            session.getTransaction().commit();}
        catch (Exception e){
            e.printStackTrace();
        return false;}
        finally {
            this.sessionFactory.close();}
    return true;}

    @Override
    public boolean save(Notice notice) {
        Session session;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(notice);
            session.getTransaction().commit();}
        catch (Exception e){
            e.printStackTrace();}
        finally {
            this.sessionFactory.close();}
        return true;}

    @Override
    public boolean update(Notice notice) {
        Session session;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(notice);
            session.getTransaction().commit();}
        catch (Exception e){
            e.printStackTrace();}
        finally {
            this.sessionFactory.close();}
        return true;}


    @Override
    public Notice getObjectByID(int id) {
            Session session;
            Notice notice = null;
            try{
                session = this.sessionFactory.getCurrentSession();
                session.beginTransaction();
                notice = session.get(Notice.class,id);
                session.getTransaction().commit();}
            catch (Exception e){
                System.out.println("error in get-method");;}
           finally {
            this.sessionFactory.close();}
            return notice;
    }
}
