package ow.micropos.client.desktop.service;


import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ComparatorUtils {
    private ComparatorUtils() {}


    public static final Comparator<String> tagComparator = new Comparator<String>() {

        private final Pattern stdPattern = Pattern.compile("([^\\d]*)(\\d+)");

        @Override
        public int compare(String o1, String o2) {
            Matcher matcher1 = stdPattern.matcher(o1);
            Matcher matcher2 = stdPattern.matcher(o2);

            if (matcher1.find() && matcher2.find()) {

                String o1Txt = matcher1.group(1);
                String o2Txt = matcher2.group(1);
                if (!o1Txt.equals(o2Txt)) {
                    return o1Txt.compareTo(o2Txt);
                }

                String o1Val = matcher1.group(2);
                String o2Val = matcher2.group(2);
                try {
                    return Integer.parseInt(o1Val) - Integer.parseInt(o2Val);
                } catch (NumberFormatException e) {
                    return o1.compareTo(o2);
                }

            } else {
                return o1.compareTo(o2);
            }
        }
    };

    public static final Comparator<String> idComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            try {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            } catch (NumberFormatException e) {
                return o1.compareTo(o2);
            }
        }
    };
}
