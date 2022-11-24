package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import telegramBot.dao.UserDAO;
import telegramBot.dao.UserDAOImpl;
import telegramBot.entity.Remind;
import telegramBot.entity.User;



import java.util.List;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RemindService service;


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

    @Override
    public List<User> getAllUsers() {
        return this.userDAO.getAllActiveUser();
    }


    public static void addUserRemind(RemindServiceImpl remindService, Remind remind){
        remindService.saveRemind(remind);
    }

    public User createUser(String chatId) {
        User user;
        if ((user = getUserByChatId(chatId)) == null) {
            this.userDAO.saveUser(new User(chatId, true));
        }
        return user;
    }


    public void deleteUserRemind(Remind remind){
        User user = remind.getUser();
        user.removeRemind(remind);
        service.deleteRemind(remind.getId());
        updateUser(user);

    }
}

