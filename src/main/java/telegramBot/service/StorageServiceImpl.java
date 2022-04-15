package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.dao.StorageDAOImpl;
import telegramBot.entity.Storage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StorageServiceImpl implements StorageService{

    private final StorageDAOImpl storageDAO;

    @Autowired
    public StorageServiceImpl(StorageDAOImpl storageDAO){
        this.storageDAO = storageDAO;
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
    public Storage getStorageById(int id) {
        return this.storageDAO.getStorageById(id);
    }

    public void fillStorage(Storage st) {
        String randomInts = st.getRandomInts();
        double d = (Math.random() * 9);
        int i = (int) d;
        if (i != 0) {
            randomInts += i;
            st.setRandomInts(randomInts);
            this.updateStorage(st);
        }
    }

    public void cleanStorage(Storage storage){
        storage.setRandomInts("");
        this.updateStorage(storage);
        }


    public int sum(String randomInts){
    List<Integer> ints = Arrays.stream(randomInts.split("")).
                map(Integer::parseInt).collect(Collectors.toList());
    int sum = 0;
    for(int i = 0; i < ints.size(); i++){
        sum += ints.get(0);
    }
    return sum;
    }




    public static StorageServiceImpl newStorageService(){
        return new StorageServiceImpl(new StorageDAOImpl());
    }
}
