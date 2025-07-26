package common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Utils {

    public static String formatNumber(BigDecimal number){
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }
}
