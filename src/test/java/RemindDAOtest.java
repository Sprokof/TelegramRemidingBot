import org.junit.*;
import org.mockito.Mockito;
import telegramBot.dao.RemindDAO;
import telegramBot.entity.Remind;

import static org.junit.Assert.assertEquals;

public class RemindDAOtest {

    @Test
    public void save(){
        RemindDAO remindDAO = Mockito.mock(RemindDAO.class);
        Remind remind = Mockito.mock(Remind.class);
        remind.setUserChatID(String.valueOf(Math.random()));
        remind.setMaintenance("something");
        remind.setRemindDate(null);
        assertEquals(false, remindDAO.save(remind));




    }
}
