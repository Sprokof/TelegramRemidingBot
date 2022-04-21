package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.UserDAOImpl;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import static telegramBot.service.RemindServiceImpl.*;


import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDAOImpl userDAO;

    @Autowired
    public UserServiceImpl(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void saveUser(User user) {
        this.userDAO.saveUser(user);
    }

    @Override
    public User getUserByChatId(String chatId) {
        return this.userDAO.getUserByChatId(chatId);
    }

    @Override
    public void updateUser(User user) {
        this.userDAO.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        this.userDAO.deleteUser(user);
    }

    public static UserServiceImpl userService() {
        return new UserServiceImpl(new UserDAOImpl());
    }

    @Override
    public List<User> getAllUsers() {
        return this.userDAO.getAllActiveUser();
    }


    private void saveOrUpdateUser(User user) {
        if (this.getUserByChatId(user.getChatId()) == null) {
            userService().saveUser(user);}
        userService().updateUser(user);
    }

    public static void addUserRemind(User user, Remind remind){
        user.addRemind(remind);
        remindService().saveRemind(remind);
    }

    public static void deleteUserRemind(Remind remind){
        User user = remind.getUser();
        user.removeRemind(remind);
        remindService().deleteRemind(remind.getId());
        userService().saveOrUpdateUser(user);
    }
}

