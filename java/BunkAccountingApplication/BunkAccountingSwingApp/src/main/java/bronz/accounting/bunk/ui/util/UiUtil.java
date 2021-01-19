package bronz.accounting.bunk.ui.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.TableModelListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.accounting.bunk.framework.exceptions.BunkValidationException;
import bronz.accounting.bunk.ui.panel.CalendarButton;
import bronz.utilities.general.DateUtil;
import bronz.utilities.general.GeneralUtil;
import bronz.utilities.general.ValidationUtil;

/**
 * Class containing different UI util classes.
 */
public class UiUtil
{
	public enum ImageType
	{
        HOME_PAGE( "bronz/accounting/bunk/ui/resources/HomePage1.gif" ),
        TOP_NAV_HOME_PAGE( "bronz/accounting/bunk/ui/resources/HomePage_NavPanelOnTop.gif" ),
        PUBLIC_PANEL( "bronz/accounting/bunk/ui/resources/PublicPanel.gif" ),
        BRONZ_LOGO( "bronz/accounting/bunk/ui/resources/Bronz_icon.JPG" );
		private final String imagePath;
		
		/**
		 * Constructor made private to avoid instatiaton.
		 *
		 * @param imagePath The path where image is available.
		 */
		private ImageType( final String imagePath )
		{
			this.imagePath = imagePath;
		}
		
		/**
		 * Overrides the toString method.
		 */
		public String toString()
		{
			return this.imagePath;
		}
	}

	private static final Logger LOG = LogManager.getLogger(
	        UiUtil.class );
	
    public static Font LABEL_BOLD_FONT = new Font( "Verdana", Font.BOLD, 15 );
    public static Font LABEL_REGULAR_FONT = new Font( "Verdana", Font.PLAIN, 10 );
    public static Font LABEL_ITALLICS_FONT = new Font( "Verdana", Font.ITALIC, 10 );
    public static Font SUB_TITLE_BOLD_FONT = new Font( "Verdana", Font.ITALIC, 18 );
	/**
	 * Private constructor to avoid instantiation.
	 */
	private UiUtil()
	{
	}
    
    public static void showReportGeneratedAlert( final String fileName,
            final long launchTime, final Component parentPanel )
    {
        final String message = "Report named " + fileName +
            " generated successfully\n" + GeneralUtil.getTimeTaken( launchTime) +
            " Do you want to open it now?";
        if( 0 == JOptionPane.showOptionDialog( parentPanel, message,
            "INFO", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, null, null ))
        {
            openFile( fileName, parentPanel );
        }
    }
    
    public static void openFile( final String fileName,
            final Component parentPanel )
    {
        try
        {
            Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " +
                fileName );
        }
        catch ( Throwable exception )
        {
            exception.printStackTrace();
            JOptionPane.showMessageDialog( parentPanel , "Failed to open the" +
                    " file. Please open it manually", "INFO",
                    JOptionPane.INFORMATION_MESSAGE );
        }
    }
    
	
	/**
	 * Gets the image.
	 *
	 * @return ImageIcon.
	 */
	public static ImageIcon getImage( final ImageType imageType )
	{
		return new ImageIcon( UiUtil.class.getClassLoader().getResource(
            imageType.toString() ) );
	}
	
	/**
	 * Removes all action listeners of the given panel.
	 *
	 * @param panel The panel from which listeners are to be removed.
	 */
	public static void removeAllActionListeners( final JPanel panel )
	{
		ValidationUtil.checkIfNull( panel , "RemoveAllActionListeners:JPanel" );
		for ( Component component : panel.getComponents())
		{
			if ( component instanceof JButton  )
			{
				final ActionListener[] listeners = component.getListeners(
						ActionListener.class );
				for ( ActionListener listener : listeners )
				{
					((JButton) component).removeActionListener( listener );
				}
			}
			else if ( component instanceof JComboBox  )
			{
				final ActionListener[] listeners = component.getListeners(
						ActionListener.class );
				for ( ActionListener listener : listeners )
				{
					((JComboBox) component).removeActionListener( listener );
				}
			}
			else if ( component instanceof JRadioButton  )
			{
				final ActionListener[] listeners = component.getListeners(
						ActionListener.class );
				for ( ActionListener listener : listeners )
				{
					((JRadioButton) component).removeActionListener( listener );
				}
			}
			else if ( component instanceof JTable &&
					panel instanceof TableModelListener )
			{
				((JTable) component).getModel().removeTableModelListener(
							(TableModelListener) panel );
			}
            else if ( component instanceof JPanel && !(component instanceof CalendarButton) )
            {
                removeAllActionListeners( (JPanel) component );
            }
		}
	}
	
	public static String getDateString( final String label, final int todayInteger )
	{
		final StringBuilder dateString = new StringBuilder();
		dateString.append( label );
		dateString.append( " : " );
		if ( 0 == todayInteger )
		{
			dateString.append( "INVALID" );
		}
		else
		{
			dateString.append( DateUtil.getDateStringWithDay( todayInteger ) );
		}
		return dateString.toString();
	}
	
	/**
	 * Adds action listeners to the Buttons and comboBoxes of the given panel.
	 *
	 * @param panel The panel for which listeners are to be added.
	 */
	public static void addActionListeners( final JPanel panel )
	{
	    addActionListeners( panel, true );
	}
	
	/**
     * Adds action listeners to the Buttons and comboBoxes of the given panel.
     *
     * @param panel The panel for which listeners are to be added.
     */
    public static void addActionListeners( final JPanel panel, final boolean recursive )
    {
    	ValidationUtil.checkIfNull( panel , "AddActionListeners:JPanel" );
        if ( panel instanceof ActionListener )
        {
            addActionListeners( panel, (ActionListener) panel, recursive );
        }
        else
        {
            LOG.error( panel.getClass().getName() + " doesnt implement ActionListener" );
        }
    }
	
	private static void addActionListeners( final JComponent panel,
	        final ActionListener listener, final boolean recursive )
    {
        for ( Component component : panel.getComponents())
        {
            if ( component instanceof JButton  )
            {
                ((JButton) component).addActionListener( listener );
            }
            else if ( component instanceof JComboBox  )
            {
                ((JComboBox) component).addActionListener( listener );
            }
            else if ( component instanceof JRadioButton  )
            {
                ((JRadioButton) component).addActionListener( listener );
            }
            else if ( component instanceof JPanel && !(component instanceof CalendarButton) && recursive )
            {
                addActionListeners((JPanel) component, listener, recursive );
            }
            else if ( component instanceof JScrollPane && recursive )
            {
                for ( Component comp : ((JScrollPane) component).getComponents() )
                {
                    if ( comp instanceof JViewport )
                    {
                        addActionListeners(((JViewport) comp), listener, recursive );
                    }
                }
            }
        }
    }
	
    public static <T extends Component> T addToolTipDisplay( final T component,
    		final String text )
    {
    	ValidationUtil.checkIfNull( component , "AddToolTipDisplay:JComponent" );
        if ( component instanceof JComboBox  )
        {
            final JComboBox comboBox = (JComboBox) component;
            comboBox.setRenderer( new DefaultListCellRenderer()
            {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public Component getListCellRendererComponent(
                        final JList list, final Object value,
                        final int index, final boolean isSelected,
                        final boolean cellHasFocus )
                {
                    final JComponent component =
                        (JComponent) super.getListCellRendererComponent(
                            list, value, index, isSelected,
                            cellHasFocus );
                    component.setToolTipText( text );
                    return component;
                }
            });
        }
        return component;
    }
    
    public static void alertUnexpectedError( final Component sourceComponent,
            final Exception exception )
    {
    	if (exception instanceof BunkValidationException )
    	{
    		alertValidationError(sourceComponent, exception.getMessage());
    	}
    	else
    	{
	        LOG.error( "The application faced an unexpected error.!!", exception );
	        JOptionPane.showMessageDialog( sourceComponent, "Very sorry.." +
	        		" The application faced an unexpected error.!!\nCAUSE:" +
	        		exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE );
    	}
    }
    
    public static void alertFatalError( final Component sourceComponent,
            final Exception exception )
    {
        LOG.fatal( "The application faced a fatal error.!!", exception );
        JOptionPane.showMessageDialog( sourceComponent, "Very sorry.." +
        		" The application faced an fatal error. The application will exit now.!!\nCAUSE:" +
        		exception.getMessage(), "FATAL ERROR", JOptionPane.ERROR_MESSAGE );
        System.exit(1);
    }
    
    public static int confirmUserChoiceWithCustomOptions( final Component sourceComponent, final String question,
            final String defaultOption, final String... options )
    {
        final int selectedOption = JOptionPane.showOptionDialog( sourceComponent, question, "CONFIRM",
                JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, defaultOption );
        return selectedOption;
    }
    
    public static void alertOperationComplete( final Component sourceComponent, final String message )
    {
        LOG.info( "Operation complete. " + message );
        JOptionPane.showMessageDialog( sourceComponent,
                message, "SUCCESS", JOptionPane.INFORMATION_MESSAGE );
    }

    public static void flashInfoMessage( final Component sourceComponent, final String message, int durationInMilliSecs )
    {
        JOptionPane pane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE);
        final JDialog dialog = pane.createDialog(null, "Info message");

        Timer timer = new Timer(durationInMilliSecs, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
        dialog.dispose();
    }
    
    public static void alertOperationCompleteWithTime( final Component sourceComponent, final String message,
            final long launchTime )
    {
        alertOperationComplete( sourceComponent, message + "\n" + GeneralUtil.getTimeTaken( launchTime ) );
    }
    
    public static void alertValidationError( final Component sourceComponent,
            final String message )
    {
        JOptionPane.showMessageDialog( sourceComponent, "Hold on.." +
                " Few values seems to be incorrect.!! Please correct them..\n" +
                message, "VALIDATION FAILED", JOptionPane.ERROR_MESSAGE );
    }
}
