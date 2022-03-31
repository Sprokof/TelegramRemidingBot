import org.junit.Test;
import telegramBot.manage.DateManage;
import telegramBot.manage.RemindManage;

import static org.junit.Assert.assertEquals;

public class RemindManageTest {

    @Test
    public void toNextMonth() {
        String nextDate = "01.02.2022";
        assertEquals(nextDate, DateManage.toNextMonth("31.01.2022"));
        nextDate = "01.03.2022";
        assertEquals(nextDate, DateManage.toNextMonth("28.02.2022"));
    }

}



