package bronz.utilities.swing.util;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.custom.ValidatableComponent;

public class SwingUtil
{
    /**
     * Validates the text fields of the given panel.
     *
     * @param panel The panel of which text field are to be validated.
     *
     * @throws UIValidationException If the value is not valid.
     */
    public static void validatePanelComponents( final JComponent panel )
            throws UIValidationException
    {
        validatePanelComponents( panel, new StringBuilder(), new StringBuilder() );
    }
    
    /**
     * Validates the text fields of the given panel.
     *
     * @param panel The panel of which text field are to be validated.
     *
     * @throws UIValidationException If the value is not valid.
     */
    public static void validatePanelComponents( final JComponent panel, final StringBuilder errorMessages,
            final StringBuilder warnMessages ) throws UIValidationException
    {
        try
        {
            validateComponents( panel.getComponents() );
        }
        catch ( UIValidationException exception )
        {
            appendMessage( errorMessages, exception.getMessage() );
        }
        catch ( UIValidationWarnException exception )
        {
            appendMessage( warnMessages, exception.getMessage() );
        }
        if ( errorMessages.length() > 0 )
        {
            throw new UIValidationException( errorMessages.toString() );
        }
        
        if ( warnMessages.length() > 0 && 0 != confirmUserChoice( panel, "Warning.!! \n" +
                warnMessages.toString() + "\n Are you sure you want to continue??" ))
        {
            throw new UIValidationException( "User aborted the opertation.");
        }
    }
    public static int confirmUserChoice( final Component sourceComponent, final String question )
    {
        final int selectedOption = JOptionPane.showOptionDialog( sourceComponent, question,
                "CONFIRM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null );
        return selectedOption;
    }
    private static void validateComponents( final Component[] components )
            throws UIValidationException, UIValidationWarnException
    {
    	ValidationUtil.checkIfNull( components , "ValidateTextFields:components" );
        final StringBuilder errorMessages = new StringBuilder();
        final StringBuilder warnMessages = new StringBuilder();
        for ( Component component : components )
        {
            if ( component instanceof ValidatableComponent  )
            {
                final String errorMessage =
                    ((ValidatableComponent) component).getValidationErrors();
                if ( !ValidationUtil.isNullOrEmpty( errorMessage ) &&
                        component.isEnabled() )
                {
                    appendMessage( errorMessages, errorMessage );
                }
                final String warnMessage =
                    ((ValidatableComponent) component).getValidationWarnings();
                if ( !ValidationUtil.isNullOrEmpty( warnMessage ) &&
                        component.isEnabled() )
                {
                    appendMessage( warnMessages, warnMessage );
                }
            }
            else if ( component instanceof JTable )
            {
                //TODO Avoid this validation. Edited tables should be programatically dealt with.  
                if ( ((JTable) component).isEditing() )
                {
                    appendMessage( errorMessages, "Table " +
                            component.getName() + " is being edited" );
                }
            }
            else if ( component instanceof JPanel )
            {
                try
                {
                	validateComponents( ((JPanel) component).getComponents() );
                }
                catch ( UIValidationException exception )
                {
                    appendMessage( errorMessages, exception.getMessage() );
                }
                catch ( UIValidationWarnException exception )
                {
                	appendMessage( warnMessages, exception.getMessage() );
                }
            }
            else if ( component instanceof JScrollPane )
            {
                try
                {
                    validateComponents( ((JScrollPane) component).getViewport().getComponents() );
                }
                catch ( UIValidationException exception )
                {
                    appendMessage( errorMessages, exception.getMessage() );
                }
                catch ( UIValidationWarnException exception )
                {
                	appendMessage( warnMessages, exception.getMessage() );
                }
            }
        }
        if ( errorMessages.length() > 0 )
        {
            throw new UIValidationException( errorMessages.toString() );
        }
        if ( warnMessages.length() > 0 )
        {
            throw new UIValidationWarnException( warnMessages.toString() );
        }
    }
    
    public static void appendMessage( final StringBuilder errorMessages,
            final String message )
    {
        if ( errorMessages.length() > 0 && ValidationUtil.isNotBlank( message ))
        {
            errorMessages.append( "\n" );
        }
        errorMessages.append( message );
    }
    
    
    public static void notifyInfoMsg( final JComponent component,
            final String message )
    {
        new Thread(){
            public void run()
            {
                JOptionPane.showMessageDialog( component, message,
                        "INFO", JOptionPane.INFORMATION_MESSAGE );
            }
        }.start();
        
    }

}
