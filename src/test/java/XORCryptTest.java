import org.junit.Test;
import telegramBot.crypt.XORCrypt;

import static org.junit.Assert.assertEquals;

public class XORCryptTest {

    @Test
    public void code(){
        String FOR_TEST_STRING = "FOR_TEST_STRING";
        String key = XORCrypt.keyGenerate();
        String s = XORCrypt.encrypt(FOR_TEST_STRING, key);

        String result = XORCrypt.
               decrypt(XORCrypt.
                       stringToIntArray(s), key);
        assertEquals(true, FOR_TEST_STRING.equals(result));
    }

}
