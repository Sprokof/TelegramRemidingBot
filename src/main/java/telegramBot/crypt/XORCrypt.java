package telegramBot.crypt;

import java.util.Arrays;
import java.util.stream.Collectors;

public class XORCrypt {

    public static String encrypt(String maintenance, String key) {
        Integer[] output = new Integer[maintenance.length()];
        for (int i = 0; i < maintenance.length(); i++) {
            int o = (((int) maintenance.charAt(i)) ^ (int) (key.charAt(i % (key.length() - 1)))) + '0';
            output[i] = o;
        }
        return Arrays.toString(output).replaceAll("\\p{P}", "").
                replaceAll("\\s", "\\/");
    }

    public static String decrypt(Integer[] input, String key) {
        String output = "";
        for (int i = 0; i < input.length; i++) {
            output += (char) ((input[i] - 48) ^ (int) key.charAt(i % (key.length() - 1)));
        }
        return output;
    }

    public static String keyGenerate() {
        String[] keyArray = new String[10];
        int index = 0;
        while (index != keyArray.length) {
            double d = Math.random() * 9;
            int i = (int) (d);
            keyArray[index] = String.valueOf(i);
            index++;
        }
        return Arrays.toString(keyArray).replaceAll("\\p{P}", "").
                replaceAll("\\s", "");

    }

    public static Integer[] stringToIntArray(String s) {
        return Arrays.stream(s.split("\\/")).map(Integer::parseInt).
                collect(Collectors.toList()).toArray(Integer[]::new);
    }

}




