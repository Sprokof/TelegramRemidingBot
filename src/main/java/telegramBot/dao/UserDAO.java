package telegramBot.dao;

import telegramBot.entity.User;

import java.util.List;

public interface UserDAO {
    void saveUser(User user);
    User getUserByChatId(String chatId);
    void updateUser(User user);
    void deleteUser(User user);
    List<User> getAllActiveUser();
}
