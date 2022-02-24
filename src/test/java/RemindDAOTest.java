import org.junit.*;
import org.mockito.Mockito;
import telegramBot.dao.DAO;
import telegramBot.entity.Remind;

import static org.junit.Assert.assertEquals;

public class RemindDAOTest {

    @Test
    public void save(){
        DAO remindDAO = Mockito.mock(DAO.class);
        Remind remind = Mockito.mock(Remind.class);
        remind.setChatIdToSend(String.valueOf(Math.random()));
        remind.setMaintenance("something");
        remind.setRemindDate(null);
        assertEquals(false, remindDAO.save(remind));




    }
}
