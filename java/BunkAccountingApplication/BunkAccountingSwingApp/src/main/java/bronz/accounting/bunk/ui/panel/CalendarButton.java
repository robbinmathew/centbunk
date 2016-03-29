package bronz.accounting.bunk.ui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;

import bronz.accounting.bunk.ui.model.CalendarUser;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.general.DateUtil;

public class CalendarButton extends BasePanel
        implements ActionListener,CalendarUser
{
	private static final long serialVersionUID = 1L;
	private Calendar selectedDate;
	private JButton launch;
    private String fieldName;
    private boolean isYearMonthAlone;
    private final JFrame frame;
    
    public CalendarButton( final int xLocation, final int yLocation,
    		final JFrame frame, final int todayInteger, final String fieldName,
    		final boolean isYearMonthAlone )
    {
        this( xLocation, yLocation, DateUtil.getCalendarEquivalent(
            todayInteger ), fieldName, frame, isYearMonthAlone );
    }
	
	public CalendarButton( final int xLocation, final int yLocation,
            final Calendar calendar, final String fieldName, final JFrame frame,
            final boolean isYearMonthAlone )
	{
        super( xLocation, yLocation, 250, 20 );
        
		this.selectedDate = calendar;
		this.frame = frame;
        this.fieldName = fieldName;
        this.isYearMonthAlone = isYearMonthAlone;
        this.launch = new JButton();
		
        this.launch.setBounds( 0, 0, 250, 20 );
        
        setSelectedDate( this.selectedDate );
        
		this.add( this.launch );
		this.setLayout( null );
		this.setVisible( true );
        UiUtil.addActionListeners( this );
	}
    
    public void setIsYearMonthAlone( final boolean isYearMonthAlone )
    {
        this.isYearMonthAlone = isYearMonthAlone;
        setSelectedDate( this.selectedDate );
    }
    
	/**
     * Gets selectedDate.
     *
     * @return selectedDate
     */
    public Calendar getSelectedDate()
    {
        return selectedDate;
    }

    /**
     * Sets fieldName.
     *
     * @param fieldName the fieldName to set
     */
    public void setFieldName( String fieldName)
    {
        this.fieldName = fieldName;
        setSelectedDate( this.selectedDate );
    }

    public void setSelectedDate( final Calendar calendar )
	{
        this.selectedDate = calendar;
        if ( isYearMonthAlone )
        {
            this.launch.setText( fieldName + ':' +
                    DateUtil.getDateYearMonthString( calendar ) );
        }
        else
        {
            this.launch.setText( fieldName + ':' +
                    DateUtil.getDateString( calendar ) );
        }
	}
	
	public void actionPerformed( final ActionEvent actionEvent )
	{
		if ( actionEvent.getSource().equals( this.launch ) )
		{
            new CalendarPanel( this, 500 , 350, selectedDate,
            		frame, isYearMonthAlone );
		}
		this.validate();
		this.repaint();
	}
}
