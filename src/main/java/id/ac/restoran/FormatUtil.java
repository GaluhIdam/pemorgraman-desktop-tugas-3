package id.ac.restoran;

import java.text.NumberFormat;
import java.util.Locale;

public final class FormatUtil {
    private FormatUtil() { }

    public static String persen(double nominal) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));
        formatter.setMaximumFractionDigits(2);
        return formatter.format(nominal) + "%";
    }

    public static String rupiah(double nominal) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(nominal);
    }
}
