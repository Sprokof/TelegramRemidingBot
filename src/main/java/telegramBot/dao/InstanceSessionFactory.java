package telegramBot.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import telegramBot.entity.*;

public class InstanceSessionFactory {
    private static SessionFactory instance = null;

    private InstanceSessionFactory(){}

    public static synchronized SessionFactory getInstance(){
        if(instance == null){
            instance = getSessionFactory(new Class[]{Message.class,
                    Storage.class, User.class, Remind.class, Details.class});
        }
    return instance;
    }

    private static SessionFactory getSessionFactory(Class<?>[] annotatedClass){
        Configuration configuration = new Configuration();
        for(Class<?> c: annotatedClass){ configuration.addAnnotatedClass(c); }
        return configuration.configure("hibernate.cfg.xml").buildSessionFactory();

    }
}
