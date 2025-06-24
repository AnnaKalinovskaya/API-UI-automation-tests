package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class RandomDataGenerator {
    private RandomDataGenerator(){};

    public static String getRandomStringWithSpace(int length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        sb.setCharAt(length / 2, ' ');
        return sb.toString();
    }

    public static String getRandomUserName(){
        return RandomStringUtils.randomAlphabetic(5)
                + RandomStringUtils.randomNumeric(5);
    }

    public static String getRandomPass() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase()
                + RandomStringUtils.randomAlphabetic(3).toLowerCase()
                + RandomStringUtils.randomNumeric(3)
                + "!";
    }

    public static BigDecimal getRandomDepositAmount(){
        return getRandomAmount(0.01, 5000);
    }

    public static BigDecimal getRandomAmount(double from, double to){
        double random = new Random().nextDouble((to - from) + 1) + from;
        return BigDecimal.valueOf(random).setScale(2, RoundingMode.HALF_UP);
    }
}
