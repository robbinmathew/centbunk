package bronz.utilities.swing.custom;

import java.math.BigDecimal;

import javax.swing.JTextField;

import bronz.utilities.general.ValidationUtil;


public class ValidatableTextField extends JTextField implements ValidatableComponent
{
	private static final long serialVersionUID = 1L;
	
	private ValidationType valdationType;
	private String title;
	public enum ValidationType
	{
		DEFAULT, NOT_EMPTY, NUMERIC_NOT_EMPTY, POSITIVE_NUMERIC, DISABLED;
	}
	
	/**
	 * Default Constructor.
	 */
	public ValidatableTextField()
	{
		this.title = "";
		this.valdationType = ValidationType.DEFAULT;
	}
	
	public void setValidationFields( final String parameterTitle,
			final ValidationType validationType )
	{
		this.title = parameterTitle;
		this.valdationType = validationType;
		if ( validationType == ValidationType.DISABLED )
		{
		    this.setEditable( false );
		}
		else if ( validationType == ValidationType.NUMERIC_NOT_EMPTY ||
		        validationType == ValidationType.POSITIVE_NUMERIC )
		{
		    this.setText( "0" );
		}
	}

	/**
	 * Gets the text after validation.
	 *
	 * @return The validation error, if any.
	 */
	public String getValidationErrors()
	{
		final String text = getText();
		String validationError = null;
		switch ( this.valdationType )
		{
			case NOT_EMPTY :
				if( ValidationUtil.isNullOrEmpty( text ) )
				{
					validationError = this.title + " is not entered";
				}
				break;
			case NUMERIC_NOT_EMPTY :
				if( ValidationUtil.isNullOrEmpty( text ) )
				{
					validationError = this.title + " is not entered";
				}
				else if ( !ValidationUtil.isValidNumber( text ) )
				{
					validationError = "The value " + text +
							" is not valid for the field " + this.title;
				}
				break;
			case POSITIVE_NUMERIC :
                if( ValidationUtil.isNullOrEmpty( text ) )
                {
                    validationError = this.title + " is not entered";
                }
                else if ( !ValidationUtil.isValidNumber( text ) || (0 >= new BigDecimal( text ).floatValue())  )
                {
                    validationError = "The value " + text +
                            " is not valid for the field " + this.title;
                }
                break;
			default:
				break;
		}
		return validationError;
	}

    public String getValidationWarnings()
    {
        return null;
    }
}
