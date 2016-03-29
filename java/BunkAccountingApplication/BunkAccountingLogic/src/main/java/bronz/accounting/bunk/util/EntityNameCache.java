package bronz.accounting.bunk.util;

import java.util.HashMap;
import java.util.Map;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.tankandmeter.dao.TankAndMeterDao;
import bronz.accounting.bunk.tankandmeter.model.Tank;

public class EntityNameCache
{
    private static Map<Integer, String> PARTY_ID_TO_NAME_MAP =
            new HashMap<Integer, String>();
    private static Map<Integer, String> TANK_ID_TO_NAME_MAP =
            new HashMap<Integer, String>();
    private static Map<Integer, String> PRODUCT_ID_TO_NAME_MAP =
            new HashMap<Integer, String>();
    private EntityNameCache()
    {
        super();
    }

    public static void readNames( final PartyDao partyDao) throws BunkMgmtException
    {
        final Map<Integer, String> idToNameMap = new HashMap<Integer, String>();
        for ( Party party : partyDao.getAllParties() )
        {
            idToNameMap.put( party.getPartyId(), party.getPartyName());
        }
        PARTY_ID_TO_NAME_MAP = idToNameMap;
    }
    
    public static void readNames( final ProductDao productDao ) throws BunkMgmtException
    {
        final Map<Integer, String> idToNameMap = new HashMap<Integer, String>();
        for ( Product product : productDao.getAllProducts() )
        {
            idToNameMap.put( product.getProductId(),
                    product.getProductName() );
        }
        PRODUCT_ID_TO_NAME_MAP = idToNameMap;
    }
    public static void readNames( final TankAndMeterDao tankAndMeterDao ) throws BunkMgmtException
    {
        final Map<Integer, String> idToNameMap = new HashMap<Integer, String>();
        for ( Tank tank : tankAndMeterDao.getAllTanks() )
        {
            idToNameMap.put( tank.getTankId(), tank.getTankName() );
        }
        TANK_ID_TO_NAME_MAP = idToNameMap;
    }
    
    public static String getPartyName( final int partyId )
    {
        if (PARTY_ID_TO_NAME_MAP.containsKey( partyId )) 
        {
            return PARTY_ID_TO_NAME_MAP.get( partyId );
        }
        else
        {
            return "Name not found!!";
        }
    }
    
    public static String getTankName( final int tankId )
    {
        if (TANK_ID_TO_NAME_MAP.containsKey( tankId )) 
        {
            return TANK_ID_TO_NAME_MAP.get( tankId );
        }
        else
        {
            return "Name not found!!";
        }
    }
    
    public static String getProductName( final int productId )
    {
        if (PRODUCT_ID_TO_NAME_MAP.containsKey( productId )) 
        {
            return PRODUCT_ID_TO_NAME_MAP.get( productId );
        }
        else
        {
            return "Name not found!!";
        }
    }
    
}
