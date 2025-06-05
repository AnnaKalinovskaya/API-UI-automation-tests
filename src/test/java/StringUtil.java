import java.util.Random;

public class StringUtil {

    public static String generateRandomStringWithSpace(int length){
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
}
