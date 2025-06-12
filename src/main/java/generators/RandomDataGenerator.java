package generators;

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

    public static BigDecimal getRandomDepositAmount(){
        return getRandomAmount(0.01, 5000);
    }

    public static BigDecimal getRandomTransferAmount(){
        return getRandomAmount(0.01, 10000);
    }

    public static BigDecimal getRandomAmount(double from, double to){
        double random = new Random().nextDouble((to - from) + 1) + from;
        return BigDecimal.valueOf(random).setScale(2, RoundingMode.HALF_UP);
    }
}
