package bronz.accounting.bunk.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class BunkUtil
{
	public static final int ZERO_FILL_MODE = 0;
	public static final int NO_FILL_MODE = 1;
	//private static final BigDecimal PI = new BigDecimal(Math.PI);
	
	private static final Map<Integer, BigDecimal> TANK_ID_TO_LENGTH_MAP =
			new HashMap<Integer, BigDecimal>();
	private static final Map<Integer, BigDecimal> TANK_ID_TO_RADIUS_MAP =
			new HashMap<Integer, BigDecimal>();
	static
	{
		TANK_ID_TO_LENGTH_MAP.put(1, new BigDecimal(3.0));
		TANK_ID_TO_LENGTH_MAP.put(2, new BigDecimal(4.0));
		TANK_ID_TO_LENGTH_MAP.put(3, new BigDecimal(3.0));
		
		TANK_ID_TO_RADIUS_MAP.put(1, new BigDecimal(1.4));
		TANK_ID_TO_RADIUS_MAP.put(2, new BigDecimal(1.5));
		TANK_ID_TO_RADIUS_MAP.put(3, new BigDecimal(1.0));
	}
	
	private BunkUtil()
	{
		super();
	}
	
    public static BigDecimal setAsMeterReading( final BigDecimal decimal )
    {
        return decimal.setScale( 2, RoundingMode.HALF_UP );
    }

    public static BigDecimal setAsPrice( final BigDecimal decimal )
    {
        return decimal.setScale( 2, RoundingMode.HALF_UP );
    }
    
    public static BigDecimal setAsProdVolume( final BigDecimal decimal)
    {
        final int num = 3;
        return decimal.setScale( num, RoundingMode.HALF_UP );
    }
    
    public static BigDecimal setAsDip( final BigDecimal decimal)
    {
    	//Round off to multiples of .2
    	final BigDecimal dipValue = decimal.setScale( 1, RoundingMode.DOWN );
    	int intDipValue = dipValue.multiply( BigDecimal.TEN ).intValue();
    	if ((intDipValue % 2) != 0 )
    	{
    		intDipValue++;
    	}
    	
        return new BigDecimal(intDipValue).divide(BigDecimal.TEN, 1, RoundingMode.HALF_UP );
    }
    
    /*public static BigDecimal getDipStock(final Integer tankId, final BigDecimal dip)
    {
    	final BigDecimal length = TANK_ID_TO_LENGTH_MAP.get(tankId);
    	final BigDecimal radius = TANK_ID_TO_RADIUS_MAP.get(tankId);
    	if (length == null || radius == null )
    	{
    		return BigDecimal.ZERO;
    	}
    	return setAsProdVolume(getCylinderVol(radius, length,
    			dip.divide(new BigDecimal(100),1, RoundingMode.HALF_UP )));
    }*/
    
	/*private static BigDecimal getCylinderVol(final BigDecimal radius,
			final BigDecimal length, final BigDecimal level)
	{
		BigDecimal volume = BigDecimal.ZERO;
		if ((BigDecimal.ZERO.compareTo(length) >= 0) ||
				(BigDecimal.ZERO.compareTo(radius) >= 0) ||
				(BigDecimal.ZERO.compareTo(level) >= 0) )
		{
			volume = BigDecimal.ZERO;
		}
		
		final BigDecimal y = level.subtract(radius);
		final BigDecimal radiusSquare = radius.pow(2);
		
		if (y.compareTo(radius) >= 0)
		{
			//Max capacity
			volume = PI.multiply(radiusSquare).multiply(length);
		}
		else
		{
			//Circle area minus segment
			final BigDecimal circleAreaMinusSegment = radiusSquare.multiply(new BigDecimal(
					Math.acos(y.negate().divide(radius).doubleValue()))).add(
							y.multiply(new BigDecimal(Math.sqrt(radiusSquare.subtract(
									y.pow(2)).doubleValue()))));
			volume = circleAreaMinusSegment.multiply(length);
		}
		return volume.multiply(new BigDecimal(1000));
	}*/
	/*
	private static BigDecimal heightForVolume(final BigDecimal radius,
			final BigDecimal length, final BigDecimal volume)
	{
		BigDecimal level = BigDecimal.ZERO;
		if (volume.compareTo(BigDecimal.ZERO) <= 0)
		{
			level = BigDecimal.ZERO;
		}
		final BigDecimal maxHieght = radius.add(radius);
		final BigDecimal maxVol = getCylinderVol(radius, length, maxHieght);
		if (volume.compareTo(maxVol) >= 0)
		{
			level = maxHieght;
		}
		int n = 0;
		double dy = my;
		double y = dy * 0.5D;

		while ((n++ < 64)
				&& (Math.abs(dv = volumeForHeightCore(y) - v) > this.rootFinderEpsilon)) {
			double dv;
			dy *= 0.5D;
			y += ((dv < 0.0D) ? dy : -dy);
		}
		return y;
	} */
}
