package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.UserDAOImpl;
import telegramBot.entity.User;

import java.util.List;

public class UserServiceImpl implements UserService{

    private final UserDAOImpl userDAO;

    @Autowired
    public UserServiceImpl(UserDAOImpl userDAO){
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

    public static UserServiceImpl newUserService(){
        return new UserServiceImpl(new UserDAOImpl());
    }

    @Override
    public List<User> getAllUsers() {
        return this.userDAO.getAllActiveUser();
    }
}
