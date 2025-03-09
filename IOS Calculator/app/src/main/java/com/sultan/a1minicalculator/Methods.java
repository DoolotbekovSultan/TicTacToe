package com.sultan.a1minicalculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Methods {

    public static String getStringNumberWithUnderScope(String numberStr) {

        boolean hasDecimal = numberStr.contains(".");

        String[] parts = numberStr.split("\\.");
        long integerPart = Long.parseLong(parts[0]);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        String formattedInteger = formatter.format(integerPart);

        return hasDecimal && parts.length > 1 ? formattedInteger + "." + parts[1] : formattedInteger;

    }

    public static String getDecimalFormatString(Double number, String format) {

        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(number);

    }

}
