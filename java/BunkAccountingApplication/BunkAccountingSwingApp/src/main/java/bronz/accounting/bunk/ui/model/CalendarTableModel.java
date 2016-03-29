package bronz.accounting.bunk.ui.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.table.AbstractTableModel;

import bronz.accounting.bunk.ui.panel.CalendarPanel;
import bronz.utilities.custom.CustomCalendar;
import bronz.utilities.general.DateUtil;

public class CalendarTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	private final CalendarPanel panel;
	private final Calendar startDate;
	private Object[][] data;
	private String[] columnNames = { "", "", "", "", "", "", "" };
	
	public CalendarTableModel( final CalendarPanel panel ,
			final Calendar startDate )
	{
		this.panel = panel;
		this.startDate = startDate;
		this.data = new Object[ 7 ][ 7 ];
		loadDates();
	}
	
	private void loadDates()
	{
		this.data[ 0 ][ 0 ] = "SUN";
		this.data[ 0 ][ 1 ] = "MON";
		this.data[ 0 ][ 2 ] = "TUE";
		this.data[ 0 ][ 3 ] = "WED";
		this.data[ 0 ][ 4 ] = "THU";
		this.data[ 0 ][ 5 ] = "FRI";
		this.data[ 0 ][ 6 ] = "SAT";
		int a = DateUtil.getNoOfDayInMonth( (GregorianCalendar) startDate);
		startDate.set( Calendar.DATE, 1 );
	    int day = startDate.get( Calendar.DAY_OF_WEEK );
	    day--;
	      
		int x = 0;
		int y = 0;
	    for ( int i = 0; i < 42; i++)
	    {
	         if( i%7 == 0)
	         {
	        	 x++;
	        	 y = 0;
	         }
	         if( (i >= day) && i < ( a + day ) )
	         {
	        	this.data[ x ][ y ] = (i - day)+ 1;
	         }
	         y++;
	      }
	}
	
	public int getColumnCount()
	{
		return columnNames.length;
	}

	public int getRowCount()
	{
		return data.length;
    }

	public String getColumnName(int col)
	{
		return columnNames[ col ];
	}

	public Object getValueAt(int row, int col)
	{
		return data[ row ][ col ];
	}

	public boolean isCellEditable(int row, int col)
	{
		if ( this.data[ row ][ col ] instanceof Integer )
		{
			final int selectedDate = (Integer) this.data[ row ][ col ];
			final Calendar selectedCalendar = new CustomCalendar();
			selectedCalendar.set( Calendar.DATE , selectedDate );
			return panel.setSelectedDate( selectedCalendar );
		}
		else
		{
			return false;
		}
	}
	
	public void setValueAt( final Object value, final int row, final int col)
	{
    }
	
}
