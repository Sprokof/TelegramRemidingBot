import org.junit.*;
import org.mockito.Mockito;
import telegramBot.dao.NoticeDAO;
import telegramBot.entity.Notice;

import static org.junit.Assert.assertEquals;

public class NoticeDAOtest {

    @Test
    public void save(){
        NoticeDAO noticeDAO = Mockito.mock(NoticeDAO.class);
        Notice notice = Mockito.mock(Notice.class);
        notice.setUserChatID(String.valueOf(Math.random()));
        notice.setMaintenance("something");
        notice.setNoticeDate(null);
        assertEquals(false, noticeDAO.save(notice));




    }
}
