package bronz.accounting.bunk.ui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;

import bronz.accounting.bunk.ui.model.CalendarTableModel;
import bronz.accounting.bunk.ui.model.CalendarUser;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.general.DateUtil;
import bronz.utilities.swing.custom.ListComboBoxModel;

public class CalendarPanel extends BasePanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final int START_YEAR = 2006;
	private final CalendarUser calendarUser;
	private JTable calendarTable;
    private JButton okButton;
	private final Calendar startDate;
	private JComboBox months;
	private JComboBox years;
    private JDialog dialog;
    
    private static final List<String> MONTH_NAMES = Arrays.asList("JAN", "FEB",
	        "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
    private static final List<String> YEARS = new ArrayList<String>();
    static
    {
    	for (int i = START_YEAR; i < 2030; i++ ){
    		YEARS.add( String.valueOf(i) );
		}
    }
    
    public CalendarPanel( final CalendarUser calendarUser, final int xLocation,
            final int yLocation, final Calendar todayDate, final JFrame frame,
            boolean isYearMonthAlone )
    {
        this( calendarUser, xLocation, yLocation,
                DateUtil.getIntegerEquivalent( todayDate ), frame,
                isYearMonthAlone );
    }
	
	public CalendarPanel( final CalendarUser calendarUser, final int xLocation,
            final int yLocation, final int todayDateInt, final JFrame frame,
            boolean isYearMonthAlone)
	{
        super( xLocation, yLocation, 240, 170 );
		this.calendarUser = calendarUser;
		this.startDate = DateUtil.getCalendarEquivalent( todayDateInt );
		
		this.months = new JComboBox( new ListComboBoxModel( MONTH_NAMES ) );
		this.years = new JComboBox( new ListComboBoxModel( YEARS ) );
        if ( isYearMonthAlone )
        {
            super.setBounds( xLocation, yLocation, 240, 75 );
            this.okButton = new JButton( "OK" );
            this.okButton.setBounds( 10, 40, 220, 20 );
            this.add( this.okButton );
        }
        else
        {
            super.setBounds( xLocation, yLocation, 240, 170 );
            this.calendarTable = new JTable(
                new CalendarTableModel( this, startDate ) );
            this.calendarTable.setBounds( 10, 40, 220, 115 );
            this.add( this.calendarTable );
        }
        
		this.months.setBounds( 30, 10, 70, 20 );
		this.years.setBounds( 150, 10, 60, 20 );
		
		this.months.setSelectedIndex( startDate.get( Calendar.MONTH ) );
		this.years.setSelectedIndex( startDate.get( Calendar.YEAR ) - START_YEAR );
		
		
		this.add( this.months );
		this.add( this.years );
		this.setLayout( null );
		this.setVisible( true );
        UiUtil.addActionListeners( this );
        
        if ( null != frame )
        {
            dialog = new JDialog( frame, "Calendar", true );
            dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
            if ( isYearMonthAlone )
            {
                this.setBounds( 0, 0, 240, 75 );
                dialog.setBounds( xLocation, yLocation, 240, 90 );
            }
            else
            {
                this.setBounds( 0, 0, 240, 170 );
                dialog.setBounds( xLocation, yLocation, 240, 180 );
            }
            dialog.add( this );
            dialog.setVisible( true );
            dialog.setResizable( false );
            dialog.setLayout( null );
        }
	}
	
	public boolean setSelectedDate( final Calendar calendar )
	{
		calendar.set( Calendar.MONTH, this.months.getSelectedIndex() );
		calendar.set( Calendar.YEAR, START_YEAR + this.years.getSelectedIndex() );
		
		this.calendarUser.setSelectedDate( calendar );
        UiUtil.removeAllActionListeners( this );
        if ( null != this.dialog )
        {
            this.dialog.dispose();
        }
		return false;
	}
	
	public void actionPerformed( final ActionEvent actionEvent )
	{
		if ( actionEvent.getSource().equals( this.months ) )
		{
			this.startDate.set( Calendar.MONTH, this.months.getSelectedIndex());
		}
		else if ( actionEvent.getSource().equals( this.years ) )
		{
			this.startDate.set( Calendar.YEAR,
					START_YEAR + this.years.getSelectedIndex() );
		}
        else if ( actionEvent.getSource().equals( this.okButton ) )
        {
            this.startDate.set( Calendar.DATE,
                    DateUtil.getNoOfDayInMonth( (GregorianCalendar) this.startDate ) );
            setSelectedDate( this.startDate );
        }
        if ( null != this.calendarTable )
        {
            this.calendarTable.setModel( new CalendarTableModel( this, startDate) );
        }
		this.validate();
		this.repaint();
	}
}
