package bronz.accounting.bunk.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.products.model.ProductType;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.MeterTransaction;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.tankandmeter.model.TankStock;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;
import bronz.utilities.custom.CustomDecimal;

public class EntityTransactionBuilder
{
    private PartyTransactionBuilder partyTransactionBuilder;
    private ProductTransactionBuilder productTransactionBuilder;
    private TankTransactionBuilder tankTransactionBuilder;
    private MeterTransactionBuilder meterTransactionBuilder;
    private TankDipStockTransactionBuilder dipStockTransactionBuilder;
    
    private final BunkManager bunkManager;
    private final int date;
    
    public EntityTransactionBuilder(final BunkManager bunkManager, final int date)
    {
        this.bunkManager = bunkManager;
        this.date = date;
    }
    
    public PartyTransactionBuilder getPartyTransBuilder() throws BunkMgmtException
    {
        if (this.partyTransactionBuilder == null )
        {
            this.partyTransactionBuilder = new PartyTransactionBuilder( bunkManager.getPartyList( PartyDao.ALL ), date );
        }
        return partyTransactionBuilder;
    }
    
    public ProductTransactionBuilder getProdTransBuilder() throws BunkMgmtException
    {
        if (this.productTransactionBuilder == null )
        {
            this.productTransactionBuilder = new ProductTransactionBuilder( bunkManager.getProductList( date ), date );
        }
        return productTransactionBuilder;
    }
    
    public TankTransactionBuilder getTankTransBuilder() throws BunkMgmtException
    {
        if (this.tankTransactionBuilder == null )
        {
            this.tankTransactionBuilder = new TankTransactionBuilder( bunkManager.getTankList( date ), date );
        }
        return tankTransactionBuilder;
    }
    
    public TankDipStockTransactionBuilder getTankDipStockTransBuilder() throws BunkMgmtException
    {
        if (this.dipStockTransactionBuilder == null )
        {
            this.dipStockTransactionBuilder = new TankDipStockTransactionBuilder(  bunkManager.getTankList( date ), date );
        }
        return dipStockTransactionBuilder;
    }
    
    public MeterTransactionBuilder getMeterTransactionBuilder() throws BunkMgmtException
    {
        if (this.meterTransactionBuilder == null )
        {
            this.meterTransactionBuilder = new MeterTransactionBuilder( bunkManager.getMeterList(), date );
        }
        return meterTransactionBuilder;
    }

    public static class PartyTransactionBuilder extends AbstractEntityTransactionBuilder<PartyClosingBalance, PartyTransaction>
    {
        private static final int DECIMAL_COUNT = 2;
        public PartyTransactionBuilder( final List<PartyClosingBalance> entitiesList, final int date )
        {
            super( entitiesList, date, DECIMAL_COUNT, PartyTransaction.CREDIT_TRANS_TYPES );
        }
        
        public void addSwapTrans( final int debitEntityId, final int creditEntityId,
                final BigDecimal amount, final String debitDetail, final String creditDetail,
                final String debitTransType, final String creditTransType)
        {
            addTrans( debitEntityId, amount, debitDetail, debitTransType );
            addTrans( creditEntityId, amount, creditDetail, creditTransType );
        }

        protected Integer getEntityId( final PartyClosingBalance entity )
        {
            return entity.getId();
        }

        protected BigDecimal getEntityBalance( final PartyClosingBalance entity )
        {
            return entity.getBalance();
        }

        protected PartyTransaction createTransactionInstance( final PartyClosingBalance entity, final int date,
                final BigDecimal amount, final BigDecimal balance, final String detail, final String transType )
        {
            final PartyTransaction transaction = new PartyTransaction();
            transaction.setPartyId( entity.getId() );
            transaction.setAmount( amount );
            transaction.setBalance( balance );
            transaction.setDate( date );
            transaction.setTransactionDetail( detail );
            transaction.setTransactionType( transType );
            return transaction;
        }
    }
    
    public static class TankDipStockTransactionBuilder extends AbstractEntityTransactionBuilder<TankClosingStock, TankStock>
    {
        private static final int DECIMAL_COUNT = 3;
        private static final List<String> CREDIT_TRANS_TYPES = Arrays.asList( TankStock.DAY_CLOSING );
        public TankDipStockTransactionBuilder( final List<TankClosingStock> entitiesList, final int date )
        {
            super( entitiesList, date, DECIMAL_COUNT, CREDIT_TRANS_TYPES );
        }
        
        protected Integer getEntityId( final TankClosingStock entity )
        {
            return entity.getTankId();
        }

        protected BigDecimal getEntityBalance( final TankClosingStock entity )
        {
            return entity.getClosingStock();
        }

        protected TankStock createTransactionInstance( final TankClosingStock entity, final int date,
                final BigDecimal amount, final BigDecimal dip, final String detail, final String transType )
        {
            final TankStock transaction = new TankStock();
            transaction.setTankId( entity.getTankId() );
            transaction.setActualStock( amount );
            transaction.setDate( date );
            transaction.setDetail( detail );
            transaction.setDip( dip );
            transaction.setType( transType );
            return transaction;
        }
    }

    public static class MeterTransactionBuilder extends AbstractEntityTransactionBuilder<MeterClosingReading, MeterTransaction>
    {
        private static final int DECIMAL_COUNT = 2;
        private static final List<String> CREDIT_TRANS_TYPES = Arrays.asList( MeterTransaction.SALE );
        public MeterTransactionBuilder( final List<MeterClosingReading> entitiesList, final int date )
        {
            super( entitiesList, date, DECIMAL_COUNT, CREDIT_TRANS_TYPES );
        }
        
        protected Integer getEntityId( final MeterClosingReading entity )
        {
            return entity.getMeterId();
        }

        protected BigDecimal getEntityBalance( final MeterClosingReading entity )
        {
            return entity.getFinalReading();
        }

        protected MeterTransaction createTransactionInstance( final MeterClosingReading entity, final int date,
                final BigDecimal amount, final BigDecimal balance, final String detail, final String transType )
        {
            final MeterTransaction transaction = new MeterTransaction();
            transaction.setMeterId( entity.getMeterId() );
            transaction.setMeterSale( amount );
            transaction.setFinalReading( balance );
            transaction.setDate( date );
            transaction.setDetail( detail );
            return transaction;
        }
    }
    
    public static class TankTransactionBuilder extends AbstractEntityTransactionBuilder<TankClosingStock, TankTransaction>
    {
        private static final int DECIMAL_COUNT = 3;
        
        public TankTransactionBuilder( final List<TankClosingStock> entitiesList, final int date )
        {
            super( entitiesList, date, DECIMAL_COUNT, TankTransaction.CREDIT_TRANS_TYPES );
        }
        
        protected Integer getEntityId( final TankClosingStock entity )
        {
            return entity.getTankId();
        }

        protected BigDecimal getEntityBalance( final TankClosingStock entity )
        {
            return entity.getClosingStock();
        }

        protected TankTransaction createTransactionInstance( final TankClosingStock entity, final int date,
                final BigDecimal amount, final BigDecimal balance, final String detail, final String transType )
        {
            final TankTransaction transaction = new TankTransaction();
            transaction.setTankId( entity.getTankId() );
            transaction.setQuantity( amount );
            transaction.setBalance( balance );
            transaction.setDate( date );
            transaction.setDetail( detail );
            transaction.setDensity( new BigDecimal( 0 ) );
            transaction.setTransType( transType );
            return transaction;
        }
    }
    
    public static class ProductTransactionBuilder extends AbstractEntityTransactionBuilder<ProductClosingBalance, ProductTransaction>
    {
        private static final int DECIMAL_COUNT = 2;

        private int nextOilId = ProductDao.MIN_OIL_PROD_ID;
        private int nextAddionalProdId = ProductDao.MIN_ADDITIONAL_PROD_ID;
        private final List<Product> prodToBeUpdated = new ArrayList<Product>();

        public ProductTransactionBuilder( final List<ProductClosingBalance> entitiesList, final int date ) {
            super( entitiesList, date, DECIMAL_COUNT, ProductTransaction.CREDIT_TRANS_TYPES );
            for (ProductClosingBalance product : entitiesList) {
                if (product.getProductId() > nextOilId &&
                    product.getProductId() < ProductDao.MIN_ADDITIONAL_PROD_ID) {
                    nextOilId = product.getProductId();
                }
                if (product.getProductId() > nextAddionalProdId &&
                    product.getProductId() > ProductDao.MIN_ADDITIONAL_PROD_ID) {
                    nextAddionalProdId = product.getProductId();
                }
            }
        }

        public List<Product> getProdToBeUpdated() {
            return this.prodToBeUpdated;
        }
        
        public void addTransWithDiscount( final int entityId, final BigDecimal amount, final BigDecimal discount,
                final String transType)
        {
            String detail = "";
            final ProductTransaction transaction = addTrans( entityId, amount, detail, transType );
            if ( BigDecimal.ZERO.compareTo( discount) < 0 )
            {
                transaction.setDetail( "Discount per unit:" + discount);
            }
        }
        
        public void addTransWithMargin( final int entityId, final BigDecimal amount, final BigDecimal margin,
                final String transType, final String detailPrefix)
        {
            String detail = "";
            final ProductTransaction transaction = addTrans( entityId, amount, detail, transType );
            if ( transaction.getMargin().compareTo(margin) != 0 )
            {
                transaction.setDetail( detailPrefix + ":Margin updated from " + transaction.getMargin().toPlainString() +
                        " to " + margin.toPlainString());
            }
            else
            {
                transaction.setDetail( detailPrefix + ":Margin not changed");
            }
            transaction.setMargin( margin );
        }
        
        protected Integer getEntityId( final ProductClosingBalance entity )
        {
            return entity.getProductId();
        }

        protected BigDecimal getEntityBalance( final ProductClosingBalance entity )
        {
            return entity.getClosingStock();
        }

        protected ProductTransaction createTransactionInstance( final ProductClosingBalance entity, final int date,
                final BigDecimal amount, final BigDecimal balance, final String detail, final String transType ) {
            return createTransactionInstance(entity.getProductId(), date, amount, balance, detail, transType,
                entity.getUnitSellingPrice(), entity.getMargin());
        }

        protected ProductTransaction createTransactionInstance( final int entityId, final int date,
            final BigDecimal amount, final BigDecimal balance, final String detail, final String transType,
            final BigDecimal unitSellingPrice, final BigDecimal margin )
        {
            final ProductTransaction transaction = new ProductTransaction();
            transaction.setProductId( entityId );
            transaction.setQuantity( amount );
            transaction.setBalance( balance );
            transaction.setDate( date );
            transaction.setDetail( detail );
            transaction.setTransactionType( transType );
            transaction.setUnitPrice( unitSellingPrice );
            transaction.setMargin( margin );
            return transaction;
        }

        public void addNewProd(final String productName, final BigDecimal sellingPrice, final BigDecimal margin,
            final BigDecimal amount, final String detail, final String transType,
            final ProductType type) throws BunkMgmtException {
            final Product product = new Product();
            if(type == ProductType.ADDITIONAL_PRODUCTS) {
                product.setProductId( nextAddionalProdId++ );
            } else if(type == ProductType.LUBE_PRODUCTS) {
                product.setProductId( nextOilId++ );
            } else {
                throw new IllegalStateException("New products for " + type + " not handled");
            }
            product.setProductName(productName);
            prodToBeUpdated.add( product );

            final ProductTransaction productTransaction = createTransactionInstance(product.getProductId(),
                date, amount, amount, detail, transType, sellingPrice, margin);
            this.transactions.add( productTransaction );
        }
    }
    
    public static abstract class AbstractEntityTransactionBuilder<E,T>
    {
        protected final int date;
        private final int decimalCount;
        protected final Map<Integer, E> entities;
        private final Map<Integer, BigDecimal> entityBalances;
        protected final List<T> transactions;
        private final List<String> creditTransTypes;
        
        AbstractEntityTransactionBuilder( final List<E> entitiesList, final int date, final int decimalCount,
                final List<String> creditTransTypes )
        {
            super();
            this.entities = new HashMap<Integer, E>();
            this.transactions = new ArrayList<T>();
            this.creditTransTypes = creditTransTypes;
            this.date = date;
            this.decimalCount = decimalCount;
            this.entityBalances = new HashMap<Integer, BigDecimal>();
             for ( final E entity : entitiesList )
            {
                this.entities.put( getEntityId( entity ), entity );
                this.entityBalances.put( getEntityId( entity ), getEntityBalance( entity ) );
            }
        }
        
        public List<T> getTransactions()
        {
            return transactions;
        }
        
        public T addTrans( final int entityId, final BigDecimal amount, final String detail, final String transType)
        {
            BigDecimal balance = getBalanceForEntity( entityId );
            if ( this.creditTransTypes.contains( transType ) )
            {
                balance = truncate( balance.add( amount ) );
            }
            else
            {
                balance = truncate(balance.subtract( amount ));
            }
            this.entityBalances.put( entityId, balance );
            final T trans = createTransactionInstance( this.entities.get( entityId ),
                    this.date, amount, balance, detail, transType ); 
            this.transactions.add( trans );
            return trans;
        }
        
        public BigDecimal getBalanceForEntity(final Integer entityId)
        {
            if(!this.entityBalances.containsKey( entityId ))
            {
                this.entityBalances.put( entityId, new BigDecimal( 0 ) );
            }
            return this.entityBalances.get( entityId );
        }
        
        protected BigDecimal truncate( final BigDecimal decimal )
        {
            return decimal.setScale( this.decimalCount, RoundingMode.HALF_UP );
        }
        
        protected abstract Integer getEntityId(final E entity);
        protected abstract BigDecimal getEntityBalance(final E entity);
        protected abstract T createTransactionInstance( final E entity, final int date, final BigDecimal amount,
                final BigDecimal balance, final String detail, final String transType );
    }
}
