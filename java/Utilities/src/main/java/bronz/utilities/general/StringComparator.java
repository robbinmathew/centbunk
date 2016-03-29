package bronz.utilities.general;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import java.text.ParseException;

public class StringComparator implements Comparator<String>
{
    public static enum CompareMode{ DATE };
    private final CompareMode mode;
    public StringComparator( final CompareMode mode )
    {
        this.mode = mode;
    }

    public int compare( final String o1, final String o2 )
    {
        final int compareResult;
        try
        {
            switch( mode )
            {
                case DATE:
                    final SimpleDateFormat formatter =
                        new SimpleDateFormat( "dd-mm-yyyy" );
                    final Date date1 = formatter.parse( o1 );
                    final Date date2 = formatter.parse( o2 );
                    compareResult = date1.compareTo( date2 );
                    break;
                default:
                    throw new UtilException( "Not implemented" );   
            }
        }
        catch ( final ParseException exception )
        {
            throw new UtilException( exception );
        }
        return compareResult;
    }
    

}
