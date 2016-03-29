package bronz.accounting.bunk.ui.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BunkUiUtil
{
    public static List<BigDecimal> getFuelReceiptQuantities()
    {
        final Set<BigDecimal> list = new TreeSet<BigDecimal>();
        for ( int i = 0; i <= 30000; i = i + 1000 )
        {
            list.add( new BigDecimal( i ) );
        }
        /*for ( int i = 5000; i <= 25000; i = i + 5000 )
        {
            list.add( new BigDecimal( i ) );
        }*/
        return new ArrayList<BigDecimal>( list );
    }

}
