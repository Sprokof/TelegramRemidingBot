package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.StorageDAO;
import telegramBot.dao.StorageDAOImpl;
import telegramBot.entity.Storage;

public class StorageServiceImpl implements StorageService{

    private StorageDAOImpl storageDAO;

    public StorageServiceImpl(){
        this.storageDAO = new StorageDAOImpl();
    }
    public static StorageServiceImpl storageService(){
        return new StorageServiceImpl();
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
        if((storage = storageService().getStorage()) == null) {
            storage = new Storage(currentMonth, datesToSend);
            storageService().saveStorage(storage);
        }

        if(currentMonth > storage.getCurrentMonth()){
            storage.setCurrentMonth(currentMonth);
            storage.setDatesToSend(datesToSend);
            storageService().updateStorage(storage);
        }
        return storage;

    }
}
