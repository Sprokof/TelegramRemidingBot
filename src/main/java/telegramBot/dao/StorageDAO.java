package telegramBot.dao;

import telegramBot.entity.Storage;

public interface StorageDAO {
    void saveStorage(Storage storage);
    void updateStorage(Storage storage);
    Storage getStorage();
}
