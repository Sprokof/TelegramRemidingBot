package telegramBot.service;

import telegramBot.dao.StorageDAOImpl;
import telegramBot.entity.Storage;

public class StorageServiceImpl implements StorageService{

    private StorageDAOImpl storageDAO;

    public StorageServiceImpl(){
        this.storageDAO = new StorageDAOImpl();
    }

    @Override
    public void saveStorage(Storage storage) {
        this.storageDAO.saveStorage(storage);
    }

    @Override
    public void updateStorage(Storage storage) {
        this.storageDAO.updateStorage(storage);
    }

    @Override
    public Storage getStorage() {
        return this.storageDAO.getStorage();
    }

    public static Storage createStorage(int currentMonth, String datesToSend){
        Storage storage;
        if((storage = new StorageDAOImpl().getStorage()) == null) {
            storage = new Storage(currentMonth, datesToSend);
            new StorageDAOImpl().saveStorage(storage);
        }

        if(currentMonth > storage.getCurrentMonth()){
            storage.setCurrentMonth(currentMonth);
            storage.setDaysToSend(datesToSend);
            new StorageDAOImpl().updateStorage(storage);
        }
        return storage;

    }
}
