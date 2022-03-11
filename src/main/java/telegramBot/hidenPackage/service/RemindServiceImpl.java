package telegramBot.hidenPackage.service;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.hidenPackage.dao.RemindDaoImpl;
import telegramBot.hidenPackage.entity.RemindDPer;

public class RemindServiceImpl implements RemindService {

    private RemindDaoImpl remindDAO;

    @Autowired
    public RemindServiceImpl(RemindDaoImpl remindDAO){
        this.remindDAO = remindDAO;}

    @Override
    public RemindDPer getRemindById(int id) {
        return this.remindDAO.getRemindById(id);

    }


    @Override
    public boolean updateRemindDateField(RemindDPer remindDPer, String nextDate) {
        remindDPer.setRemindDate(nextDate);
        return this.remindDAO.updateRemind(remindDPer);
    }

    @Override
    public boolean updateCountSendField(RemindDPer remindDPer, int count) {
        remindDPer.setCount_send(count);
        return this.remindDAO.updateRemind(remindDPer);
    }

    public static RemindService newRemindService(){
        return new RemindServiceImpl(new RemindDaoImpl());
    }
}


