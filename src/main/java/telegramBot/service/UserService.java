package telegramBot.service;

import telegramBot.entity.Remind;
import telegramBot.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    User getUserByChatId(String chatId);
    void updateUser(User user);
    void deleteUser(User user);
    List<User> getAllUsers();
    User createUser(String chatId);
    void deleteUserRemind(Remind remind);
}
