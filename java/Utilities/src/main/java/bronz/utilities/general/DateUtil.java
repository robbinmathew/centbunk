package bronz.utilities.general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bronz.utilities.custom.CustomCalendar;

public class DateUtil
{
    private static final Calendar START_DATE_CALENDAR =
            new GregorianCalendar( 2007 , Calendar.JUNE, 30);
    private static final int START_DATE_INTEGER = 733222;
    
    private static final String DATE_WITH_DAY_FORMATTER = "dd-MMM-yyyy : EEE";
    
    private static final String DATE_FORMATTER = "dd-MMM-yyyy";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String MONTH_YEAR_FORMATTER = "MMM-yyyy";
    
    public static String getDateString( final int date )
    {
        return new SimpleDateFormat(DATE_FORMATTER).format(getDateEquivalent( date ));
    }
    
    public static String getDateString( final Calendar calendar )
    {
        return new SimpleDateFormat(DATE_FORMATTER).format(calendar.getTime());
    }


    public static String getSimpleDateString( final int date )
    {
        return new SimpleDateFormat(DATE_FORMAT).format(getDateEquivalent(date));
    }

    public static int getDateFromSimpleDateString( final String dateText ) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat(DATE_FORMAT).parse(dateText));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date", e);
        }
        return getIntegerEquivalent(cal);
    }

    public static String getSimpleDateString( final Calendar calendar )
    {
        return new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime());
    }
   
    public static String getDateStringWithDay( final int date )
    {
        return new SimpleDateFormat(DATE_WITH_DAY_FORMATTER).format(getDateEquivalent(date));
    }
   
   /**
    * Gets the String equivalent of the date.
    *
    * @param calendar The calendar date.
    *
    * @return Date.
    */
   public static String getDateStringWithDay( final Calendar calendar )
   {
       return new SimpleDateFormat(DATE_WITH_DAY_FORMATTER).format(calendar.getTime());
   }
   
   public static String getDateYearMonthString( final int date )
   {
       return new SimpleDateFormat(MONTH_YEAR_FORMATTER).format(getDateEquivalent(date));
   }
   
   public static String getDateYearMonthString( final Calendar calendar )
   {
       return new SimpleDateFormat(MONTH_YEAR_FORMATTER).format(calendar.getTime());
   }
   
   public static int getIntegerEquivalent( final Calendar calendar )
   {
       int dateInteger;
       long diffMillis = 0;
       if (calendar.before( START_DATE_CALENDAR ))
       {
          diffMillis = START_DATE_CALENDAR.getTimeInMillis() -
                  calendar.getTimeInMillis();
          dateInteger = Integer.parseInt( new Long( START_DATE_INTEGER -
                  (diffMillis / (24 * 60 * 60 * 1000)) ).toString() );
          --dateInteger;
       }
       else if (calendar.equals( START_DATE_CALENDAR ))
       {
           dateInteger = START_DATE_INTEGER;
       }
       else
       {
          diffMillis = calendar.getTimeInMillis() -
                  START_DATE_CALENDAR.getTimeInMillis();
          dateInteger = Integer.parseInt( new Long( START_DATE_INTEGER +
                  (diffMillis / (24 * 60 * 60 * 1000)) ).toString() );
       }
       return dateInteger;
   }
   
   /**
    * Gets the String equivalent of the date and time.
    *
    * @param calendar The calendar date.
    *
    * @return Date.
    */
   public static String getDateWithTimeString( final Calendar calendar )
   {
       return new SimpleDateFormat("dd-MMM-yyyy : HH-mm-ss").format(calendar.getTime());
   }
   
   public static boolean isLastDayOfMonth( final int dateInteger )
   {
       final Calendar calendar = getCalendarEquivalent( dateInteger );
       final int lastDateOfMonth = getNoOfDayInMonth(
               new CustomCalendar( calendar ) );
       if ( lastDateOfMonth == calendar.get( Calendar.DATE ) )
       {
           return true;
       }
       else
       {
           return false;
       }
   }

   public static boolean isFirstDayOfMonth( final int dateInteger )
   {
       final Calendar givenDate = getCalendarEquivalent( dateInteger );
       givenDate.set( Calendar.DATE, 1);
       return getIntegerEquivalent( givenDate ) == dateInteger;
   }
   
   public static boolean isMondayOrThursday( final int dateInteger )
   {
       final Calendar givenDate = getCalendarEquivalent( dateInteger );
       return (givenDate.get( Calendar.DAY_OF_WEEK) == Calendar.MONDAY) ||
       		(givenDate.get( Calendar.DAY_OF_WEEK) == Calendar.THURSDAY);
   }

   
   public static int getFirstDateOfGivenMonth( final int date )
   {
       final Calendar givenDate = getCalendarEquivalent( date );
       givenDate.set( Calendar.DATE, 1);
       return getIntegerEquivalent( givenDate );
   }
   
   public static int getLastDateOfGivenMonth( final int date )
   {
       final Calendar givenDate = getCalendarEquivalent( date );
       givenDate.set( Calendar.DATE, getNoOfDayInMonth(
               (GregorianCalendar) givenDate ));
       return getIntegerEquivalent( givenDate );
   }
   
   public static int getNoOfDayInMonth( final GregorianCalendar calendar)
   {
      int maxDate = 0;
      boolean isLeapYear = calendar.isLeapYear( calendar.get( Calendar.YEAR ) );
      switch ( calendar.get( Calendar.MONTH ) )
      {
         case 0:
            maxDate = 31;
            break;
         case 1:
            if (isLeapYear)
            {
               maxDate = 29;
            }
            else
            {
               maxDate = 28;
            }
            break;
         case 2:
            maxDate = 31;
            break;
         case 3:
            maxDate = 30;
            break;
         case 4:
            maxDate = 31;
            break;
         case 5:
            maxDate = 30;
            break;
         case 6:
            maxDate = 31;
            break;
         case 7:
            maxDate = 31;
            break;
         case 8:
            maxDate = 30;
            break;
         case 9:
            maxDate = 31;
            break;
         case 10:
            maxDate = 30;
            break;
         case 11:
            maxDate = 31;
            break;

      }
      return maxDate;
   }

   
   public static Calendar getCalendarEquivalent( final int currentDateInteger )
   {
      Calendar today = new CustomCalendar( 2007 , Calendar.JUNE, 30);
      int daysPassed = currentDateInteger - START_DATE_INTEGER;
      today.add( Calendar.DATE, daysPassed );
      return today;
   }
   
   public static Date getDateEquivalent( final int currentDateInteger )
   {
      Calendar today = new CustomCalendar( 2007 , Calendar.JUNE, 30);
      int daysPassed = currentDateInteger - START_DATE_INTEGER;
      today.add( Calendar.DATE, daysPassed );
      return today.getTime();
   }
   
   public static boolean isWorkingDay( final int dateInteger )
   {
      Calendar today = getCalendarEquivalent( dateInteger );
      return today.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY;
   }

   public static Date nextByHourRounded(int hour) {
       Calendar calendar = new GregorianCalendar();
       if (hour < calendar.get(Calendar.HOUR_OF_DAY)) {
           //The hour passed, increment the date to get the hour the next day.
           calendar.add(Calendar.DATE, 1);
       }
       calendar.set(Calendar.HOUR_OF_DAY, hour);
       calendar.set(Calendar.MINUTE, 0);
       calendar.set(Calendar.SECOND, 0);
       return calendar.getTime();
   }
}
