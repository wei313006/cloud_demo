package common.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeFormatUtils {

    private static final long EXTIME = 1000 * 60 * 60L;
    private static final SimpleDateFormat RFC399FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final SimpleDateFormat DATETIMEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");


    public static String formatCurrentDate() {
        return DATEFORMAT.format(System.currentTimeMillis());
    }

    public static String formatCurrentDateTime() {
        return DATETIMEFORMAT.format(System.currentTimeMillis());
    }

    public static String formatCurrentDateTimeToPST() {
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        DATETIMEFORMAT.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        return DATETIMEFORMAT.format(date);
    }

    public static String formatCurrentDateToPST() {
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        DATEFORMAT.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        return DATEFORMAT.format(date);
    }

    public static String oneHoursForRFC399(long nowTime) {
        return RFC399FORMAT.format(nowTime + EXTIME);
    }

    public static String parseRFC399ForDatetime(String RFC399) {
        String format;
        try {
            Date parse = RFC399FORMAT.parse(RFC399);
            format = DATETIMEFORMAT.format(parse.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return format;
    }

    public static long getTimeForSecond() {
        return (System.currentTimeMillis() / 1000);
    }

}
