package telegramBot.dao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import telegramBot.entity.Message;
import telegramBot.entity.User;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOImpl implements MessageDAO{

    private static final SessionFactory sessionFactory = InstanceSessionFactory.getInstance();

    @Override
    public void save(Message message) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(message);
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
    public void deleteMessageByMessageId(Integer messageId) {
        Session session = null;
    try{
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.createSQLQuery("DELETE FROM MESSAGES WHERE MESSAGE_ID=:id").
                setParameter("id", messageId).executeUpdate();
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
    public void deleteAllMessages() {
        Session session = null;
    try{
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.createSQLQuery("DELETE FROM MESSAGES").executeUpdate();
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
    @SuppressWarnings("unchecked")
    public List<Message> getAllRemindMessages() {
        Session session = null;
        List<Message> messages = new ArrayList<>();
    try{
        session = sessionFactory.openSession();
        session.beginTransaction();
        messages = session.createSQLQuery("SELECT * FROM MESSAGES " +
                        "WHERE IS_REMIND_MESSAGE is true").
                addEntity(Message.class).list();
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
    return messages;
    }

    @SuppressWarnings("unchecked")
    public Message getMessageByChatAndRemindId(String chatId, String remindId) {
        Session session = null;
        Message message = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            message = (Message) session.createSQLQuery("SELECT * FROM MESSAGES " +
                            "WHERE CHAT_ID=:cId AND REMIND_ID=:rId ").
                    addEntity(Message.class).setParameter("cId", chatId).
                    setParameter("rId", remindId).list().get(0);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    if (e instanceof IndexOutOfBoundsException) return null;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return message;
    }

    @Override
    public void updateMessage(Message message) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(message);
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
    public boolean isSentMessage(Message message) {
        Session session = null;
        Message sentMessage = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            sentMessage = (Message) session.createSQLQuery("SELECT * FROM MESSAGES " +
                            "WHERE REMIND_ID=:remindId " +
                            "AND CHAT_ID=:chatId AND IS_REMIND_MESSAGE is true").
                    addEntity(Message.class).setParameter("remindId", message.getRemindId()).
                    setParameter("chatId", message.getChatId()).getSingleResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    if (e instanceof NoResultException) return false;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return (sentMessage != null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> getAllNotRemindMessage(User user) {
        Session session = null;
        List<Message> messages = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
        messages = session.createSQLQuery("SELECT * FROM MESSAGES WHERE CHAT_ID=:id AND " +
                    "IS_REMIND_MESSAGE is false").
                    addEntity(Message.class).setParameter("id", user.getChatId()).list();
            session.getTransaction().commit();
        }
        catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    e.printStackTrace();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return messages;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> getRemindMessagesByChatId(String chatId) {
        Session session = null;
        List<Message> messages = new ArrayList<>();
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            messages = (ArrayList<Message>) session.
                    createSQLQuery("SELECT * FROM MESSAGES WHERE CHAT_ID=:id " +
                            "AND IS_REMIND_MESSAGE is true").
                    addEntity(Message.class).setParameter("id", chatId).list();
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
        return messages;
    }


    @Override
    public void deleteAllNotRemindMessage(User user) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.createSQLQuery("DELETE FROM MESSAGES WHERE CHAT_ID=:id AND " +
                    "IS_REMIND_MESSAGE is false").setParameter("id",
                    user.getChatId()).executeUpdate();
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
    public Message deleteLastSendMessage(User user) {
        Integer messageId; Message message = null;
        if((messageId = getLastMessageId(user)) == null) return null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.createSQLQuery("DELETE FROM MESSAGES " +
                    "WHERE MESSAGE_ID=:messageId").setParameter("id", messageId).executeUpdate();
            message =  new Message(user.getChatId(), messageId);
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
    return message;
    }

    private Integer getLastMessageId(User user) {
        Session session = null;
        int id = 0;
    try{
        session = sessionFactory.openSession();
        session.beginTransaction();
        id = (int) session.createSQLQuery("SELECT MESSAGE_ID FROM MESSAGES " +
                "WHERE CHAT_ID=:id").
                setParameter("id",
                        user.getChatId()).getSingleResult();
        session.getTransaction().commit();
    } catch (Exception e) {
        if (session != null) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                if(e instanceof NoResultException) return null;
            }
        }
    } finally {
        if (session != null) {
            session.close();
        }
    }
    return id;

    }
}
