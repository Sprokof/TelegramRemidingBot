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
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < input.length; i++) {
            sb.append((char) ((input[i] - 48) ^ (int) key.charAt(i % (key.length() - 1))));
        }

        return sb.toString();
    }

    public static String keyGenerate() {
        String[] keyArray = new String[7];
        int index = 0;
        while (index != keyArray.length) {
            double d = Math.random() * 9;
            int i = (int) (d);
            keyArray[index] = String.valueOf(i);
            index++;
        }

        String tempKey = Arrays.toString(keyArray).replaceAll("\\p{P}", "").
                replaceAll("\\s", "");

        return MD5.hash(tempKey);

    }

    public static Integer[] stringToIntArray(String s) {
        return Arrays.stream(s.split("\\/")).map(Integer::parseInt).
                collect(Collectors.toList()).toArray(Integer[]::new);
    }

}




