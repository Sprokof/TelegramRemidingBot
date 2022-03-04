import org.junit.Test;
import telegramBot.sendRemind.SendRemind;
import telegramBot.validate.Validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class ValidateTest {

    @Test
    public void date() {
        assertEquals(false, Validate.date("21", "13", "5"));
        assertEquals(false, Validate.date("kal", "9", "2019"));
    }

    @Test
    public void code(){
        String FOR_TEST = "FOR_TEST";
        String tempString  = Validate.codedMaintenance(FOR_TEST);
        String s2 = Validate.decodedMaintenance(tempString);
        assertEquals(true, s2.equals(FOR_TEST));
    }


}

