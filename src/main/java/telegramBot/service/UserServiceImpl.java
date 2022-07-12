package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.RemindDAOImpl;
import telegramBot.dao.UserDAOImpl;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.manage.DateManage;

import static telegramBot.service.RemindServiceImpl.*;


import java.util.List;
import java.util.Map;

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


    public static void addUserRemind(RemindServiceImpl remindService, Remind remind){
        remindService.saveRemind(remind);
    }

    public User createUser(String chatId) {
        User user;
        if ((user = userService().getUserByChatId(chatId)) == null) {
            this.userDAO.saveUser(new User(chatId, true));
        }
        return user;
    }


    public static void deleteUserRemind(Remind remind){
        User user = remind.getUser();
        user.removeRemind(remind);
        userService().updateUser(user);
    }
}

