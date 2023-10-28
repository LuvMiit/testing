package org.niias.asrb.kn.util;

import org.niias.asrb.kn.model.Period;

import java.time.LocalDate;

public class CompletionUtil {

    public static boolean isLate(int year, int month, LocalDate date){
        if (date == null)
            date = LocalDate.now();
        if (year < date.getYear())
            return true;

        int closed = date.getDayOfMonth() >= 30 ? date.getMonth().ordinal() + 1 : date.getMonth().ordinal();
        return date != null && month < closed;
    }

    public static  int getPeriodLength(Period period){
        switch (period){
            case quarter:
                return 3;
            case halfYear:
                return 6;
            case year:
                return 12;
            default:
                return 1;
        }
    }

    public static int getFirstMonthOfPeriod(int month, Period period){
        switch (period){
            case quarter:
            case halfYear:
                return ((month - 1) / getPeriodLength(period)) * getPeriodLength(period)  + 1;
            case year:
                return 1;
            default:
                return month;
        }
    }

    public static int getLastMonthOfPeriod(int month, int periodLength) {
        return month % periodLength == 0 ? month : month + periodLength - month % periodLength;
    }

    public static int getLastMonthOfPeriod(int month, Period period){
        return getLastMonthOfPeriod(month, getPeriodLength(period));
    }

    public static int getSetMonthOfPeriod(int mark, int month, Period period){
        int first = getFirstMonthOfPeriod(month, period);
        for (int i = first; i < first + getPeriodLength(period); i++) {
            if (isMonthSet(mark, i))
                return i;
        }
        return 0;
    }

    public static int setMonth(int mark, int month){
        return mark | month2binary(month);
    }

    public static int unsetMonth(int mark, int month){
        return mark ^ month2binary(month);
    }

    public static boolean isMonthSet(int mark, int month){
        return (mark & month2binary(month)) > 0;
    }

    private static int month2binary(int month){
        return 1 << 12 - month;
    }


    public static String format12month(int mark){
        return String.format("%12s", Integer.toBinaryString(mark)).replace(' ', '0');
    }

}
