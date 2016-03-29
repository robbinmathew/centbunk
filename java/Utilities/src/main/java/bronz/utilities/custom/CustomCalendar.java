package bronz.utilities.custom;

import java.util.Calendar;
import java.util.GregorianCalendar;

import bronz.utilities.general.DateUtil;

public class CustomCalendar extends GregorianCalendar
{
	private static final long serialVersionUID = 1L;
	
	public CustomCalendar( final int year, final int month , final int day )
	{
		super( year, month, day );
	}
	
	public CustomCalendar( final Calendar calendar )
	{
		super( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ),
				calendar.get( Calendar.DAY_OF_MONTH ) );
	}
	
	public CustomCalendar()
	{
		super();
	}



	public String toString()
	{
		return DateUtil.getDateStringWithDay( this );
	}
	
}
