import org.junit.Test;
import telegramBot.validate.Validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class ValidateTest {

    @Test
    public void date() {
        assertEquals(false, Validate.date("21", "13", "5"));
        assertEquals(false, Validate.date("kal", "9", "2019"));
        assertEquals(true, Validate.date("12", "11", "2021"));
    }


}
