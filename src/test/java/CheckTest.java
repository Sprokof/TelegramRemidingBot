import org.junit.Test;
import telegramBot.check.Check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class CheckTest {

    @Test
    public void date(){
        assertEquals(false, Check.date("21", "13", "5"));
        assertEquals(false, Check.date("kal", "9", "2019"));
        assertEquals(true, Check.date("12", "11", "2021"));}


    @Test
    public void time(){
    assertEquals(false, Check.time("0l"));
    assertEquals(false, Check.time("212"));
    assertEquals(true, Check.time("22"));
        }
    }

