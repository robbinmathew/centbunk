package bronz.accounting.bunk.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.accounting.bunk.framework.dao.DBUtil;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;

public class EntityTransBalCorrectionHelper
{
	private static final Logger LOG = LogManager.getLogger(
			EntityTransBalCorrectionHelper.class );
	private final DBUtil dbUtil;
	
	public EntityTransBalCorrectionHelper(final DBUtil dbUtil)
    {
		this.dbUtil = dbUtil;
    }
	
	public void correctProdTransactions() throws BunkMgmtException
	{
		LOG.info("\nProduct balance correction :Start...");
		correctTransactionBalances(
     		   "PBMS_PRODUCT_TRANSACTIONS", "QUANTITY", "BALANCE", "PK_SLNO",
     		   "SELL_RECIEVE", "PRODUCT_ID", Arrays.asList( ProductTransaction.RECEIPT ), 3);
		LOG.info("Product balance correction :Done...\n");
	}
	
	public void correctPartyTransactions() throws BunkMgmtException
	{
		LOG.info("\n Party balance correction :Start...");
		correctTransactionBalances(
     		   "PBMS_PARTY_TRANSACTIONS", "AMOUNT", "BALANCE", "PK_SLNO",
     		   "CASH_CREDIT", "PARTY_ID", PartyTransaction.CREDIT_TRANS_TYPES, 3);
		LOG.info("Party balance correction :Done...\n");
	}
	
	public void correctTankTransactions() throws BunkMgmtException
	{
		LOG.info("\nTank balance correction :Start...");
		correctTransactionBalances(
     		   "PBMS_TANK_TRANSACTIONS", "QUANTITY", "BALANCE", "PK_SLNO",
     		   "TRANS_TYPE", "TANK_ID", TankTransaction.CREDIT_TRANS_TYPES, 3);
		LOG.info("Tank balance correction :Done...\n");
	}
    
    @SuppressWarnings("unused")
	private void correctTransactionBalances(final String tableName, final String quatityColName,
    		final String balColName, final String keyColName, final String transTypeColName,
    		final String entityIdColName, final List<String> creditTransTypes,
    		final int decimalCount) throws BunkMgmtException
    {
    	Connection connection = null;
    	try
    	{
    		connection = this.dbUtil.getConnection();
    		connection.setAutoCommit(false);
    		final Map<Integer, BigDecimal> entityBalances = new HashMap<Integer, BigDecimal>();
    		final Statement statement = connection.createStatement();
    		final String query = String.format( "SELECT %1$S AS KEY_COL, %2$S AS QUAN," +
    				"%3$S AS BAL,%4$S AS TYPE,%6$S AS ID FROM %5$S ORDER BY %1$S,%6$S",
    				keyColName, quatityColName,	balColName, transTypeColName,
    				tableName, entityIdColName);
    		LOG.info("Correct balance query:" + query);
    		final ResultSet result = statement.executeQuery(query);
    		while(result.next()) {
    			final Integer entityId = result.getInt("ID");
    			final BigDecimal quan = result.getBigDecimal("QUAN");
    			final BigDecimal bal = result.getBigDecimal("BAL");
    			final String type = result.getString("TYPE");
    			final Integer key = result.getInt("KEY_COL");
    			
    			if (!entityBalances.containsKey(entityId)) {
    				entityBalances.put(entityId, BigDecimal.ZERO);
    			}
    			BigDecimal currentBal = entityBalances.get(entityId);
    			final String updateQuery = "UPDATE " + tableName + " SET " + balColName + " = ? where " + keyColName + " = ?";
    			if (creditTransTypes.contains(type))
    			{
    				currentBal = truncate( currentBal.add( quan ), decimalCount );
    			}
    			else
    			{
    				currentBal = truncate( currentBal.subtract( quan ), decimalCount );
    			}
    			
    			if ( (currentBal.subtract(bal).compareTo(BigDecimal.ZERO) > 0 &&
    							currentBal.subtract(bal).compareTo(new BigDecimal("0.100")) > 0 ) ||
					(currentBal.subtract(bal).compareTo(BigDecimal.ZERO) < 0 &&
							bal.subtract(currentBal).compareTo(new BigDecimal("0.100")) > 0 ))
    			{
    				LOG.info( String.format( "Balance wrong. ID:%1$S ENT_ID:%2$S QUAN:%3$S TYPE:%4$S BAL:%5$S NEW BAL:%6$S",
    						key.toString(), entityId.toString(), quan.toPlainString(), type, bal.toPlainString(), currentBal.toPlainString()));
    				if(false){
    					final PreparedStatement co = connection.prepareStatement(updateQuery);
    					co.setBigDecimal(1, currentBal);
    					co.setInt(2, key);
    					co.executeUpdate();
    				}
    			}
    			else
    			{
    				LOG.debug( String.format( "Balance correct. ID:%1$S ENT_ID:%2$S QUAN:%3$S TYPE:%4$S BAL:%5$S NEW BAL:%6$S",
    						key.toString(), entityId.toString(), quan.toPlainString(), type, bal.toPlainString(), currentBal.toPlainString()));
    			}
    			
    			entityBalances.put(entityId, currentBal);
    			
    		}
    		connection.commit();
    	}
    	catch ( Exception exception )
    	{
    		LOG.error("Failed to correct transactions", exception);
    	}
    	finally
    	{
    		this.dbUtil.closeConnection(connection);
    	}
    }
    
    private BigDecimal truncate( final BigDecimal decimal, final int decimalCount )
    {
        return decimal.setScale( decimalCount, RoundingMode.HALF_UP );
    }
}
