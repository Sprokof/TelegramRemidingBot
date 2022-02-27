import org.junit.Test;
import telegramBot.sendRemind.SendRemind;

import static org.junit.Assert.assertEquals;

public class SendRemindTest {

    @Test
    public void toNextMonth() {
        String nextDate = "01.02.2022";
        assertEquals(nextDate, SendRemind.toNextMonth("31.01.2022"));
        nextDate = "01.03.2022";
        assertEquals(nextDate, SendRemind.toNextMonth("28.02.2022"));
    }

}



