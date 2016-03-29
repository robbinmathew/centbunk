package bronz.accounting.bunk.products.dao;

import java.util.List;

import bronz.accounting.bunk.products.exception.ProductDaoException;
import bronz.accounting.bunk.products.model.ProdRateChange;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.products.model.ReceiptSummary;
import bronz.accounting.bunk.products.model.StockVariation;

public interface ProductDao
{
    String ALL = "ALL_PRODUCTS";
    String PRIMARY = "PRIMARY_PRODUCTS";
    String SECONDARY = "SECONDARY_PRODUCTS";
    String FUEL_PRODUCTS = "FUEL_PRODUCTS";
    String OIL_PRODUCTS = "OIL_PRODUCTS";
    String LUBE_PRODUCTS = "LUBE_PRODUCTS";
    String ADDITIONAL_PRODUCTS = "ADDITIONAL_PRODUCTS";
    
    Integer MIN_OIL_PROD_ID = 10;
    Integer MIN_ADDITIONAL_PROD_ID = 1000;
    
    List<Product> getAllProducts() throws ProductDaoException;
    
    List<ProductClosingBalance> getProductList( final String productType, final int date )
        throws ProductDaoException;
    
    List<ProductClosingBalance> getAvailableProductList( final String productType, final int date )
        throws ProductDaoException;
    
    ProductClosingBalance getProduct( final int productId, final int date ) throws ProductDaoException;
    
    List<ProductTransaction> getAllProdTransByDate( final int startDate,
            int endDate ) throws ProductDaoException;
    
    List<ProductTransaction> getAllProdTransByDateAndType(
            final int startDate, final int endDate,
            final String transactionType ) throws ProductDaoException;
    
    List<ProductTransaction> getAllProductTransByDetail( final String transactionType )
            throws ProductDaoException;
    
    List<ProductClosingBalance> getProdClosingByDate( final int date )
            throws ProductDaoException;
    
    List<ReceiptSummary> getReceiptSummaries( final int date ) throws ProductDaoException;
    
    ReceiptSummary getReceiptSummary( final String invoiceNumber ) throws ProductDaoException;
    
    List<ProdRateChange> getProdRateChanges( final int date ) throws ProductDaoException;
    
    void saveProdRateChange( final ProdRateChange prodRateChange ) throws ProductDaoException;
    
    List<ReceiptSummary> getReceiptSummaries( final int startDate, final int endDate )
        throws ProductDaoException;
    
    void saveStockReceipt( final ReceiptSummary receiptSummary ) throws ProductDaoException;
    
    void saveProductTransactions( final List<ProductTransaction> transactions ) throws ProductDaoException;
    
    void saveProducts( final List<Product> products ) throws ProductDaoException;
    
    List<StockVariation> getStockVariation( final int date ) throws ProductDaoException;

}
