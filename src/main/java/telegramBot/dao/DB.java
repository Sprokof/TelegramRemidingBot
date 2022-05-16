package telegramBot.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DB {
    private static DB instance = null;

    public static DB getInstance(){
        if(instance == null){
            instance = new DB();
        }
    return instance;
    }

    protected SessionFactory getSessionFactory(Class<?>[] annotatedClass){
        Configuration configuration = new Configuration();
        for(Class<?> c: annotatedClass){ configuration.addAnnotatedClass(c); }
        return configuration.configure("hibernate.cfg.xml").buildSessionFactory();

    }
}
