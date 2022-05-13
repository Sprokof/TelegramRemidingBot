package telegramBot.service;

import telegramBot.entity.Storage;

public interface StorageService {
    void saveStorage(Storage storage);
    void updateStorage(Storage storage);
    Storage getStorage();
}
