package bronz.accounting.bunk.ui.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.ui.party.model.ChequeTransWrapper;
import bronz.accounting.bunk.ui.party.model.EmpSalBean;
import bronz.accounting.bunk.ui.party.model.EmployeeTrxDetailBean;
import bronz.accounting.bunk.ui.party.model.OfficeCashDetailBean;
import bronz.accounting.bunk.ui.party.model.PartyDetailBean;
import bronz.accounting.bunk.ui.party.model.PartyWrapper;
import bronz.accounting.bunk.ui.product.model.FuelReceiptProdDetailBean;
import bronz.accounting.bunk.ui.product.model.FuelSalesProdDetailBean;
import bronz.accounting.bunk.ui.product.model.MeterSalesBean;
import bronz.accounting.bunk.ui.product.model.ProductWrapper;
import bronz.accounting.bunk.ui.product.model.TankReceiptDetailBean;
import bronz.accounting.bunk.ui.product.model.TankSaleDetailBean;

public class EntityConverter
{
    public static List<TankReceiptDetailBean> convertToTankReciept(
            final List<TankClosingStock> closingStocks )
    {
        final List<TankReceiptDetailBean> beans =
                new ArrayList<TankReceiptDetailBean>();
        for ( TankClosingStock tank : closingStocks )
        {
            beans.add( new TankReceiptDetailBean( tank ) );
        }
        return beans;
    }
    
    public static List<MeterSalesBean> convertToMeterSales( final List<MeterClosingReading> meters )
    {
        final List<MeterSalesBean> meterBeans = new ArrayList<MeterSalesBean>();
        for ( MeterClosingReading meterClosingReading : meters )
        {
            meterBeans.add( new MeterSalesBean( meterClosingReading ) );
        }
        return meterBeans;
    }
    
    public static List<OfficeCashDetailBean> convertToOfficeCashDetailBeans( final PartyClosingBalance officeCash,
            final PartyClosingBalance officeCheque, final int todayDate, final List<PartyDetailBean> partySaleBean,
            final List<PartyTransaction> pendingCheques )
    {
        final List<OfficeCashDetailBean> beans = new ArrayList<OfficeCashDetailBean>();
        beans.add( new OfficeCashDetailBean( officeCash, todayDate, null, null ) );
        beans.add( new OfficeCashDetailBean( officeCheque, todayDate, partySaleBean, pendingCheques ) );
        return beans;
    }
    
    public static List<FuelReceiptProdDetailBean> convertToFuelReciept(
            final List<ProductClosingBalance> fuelProducts,
            final List<TankReceiptDetailBean> tankList )
    {
        final List<FuelReceiptProdDetailBean> beans =
                new ArrayList<FuelReceiptProdDetailBean>();
        //Prepare product to tank map
        final Map<Integer, List<TankReceiptDetailBean>> productNameToTankMap =
                new HashMap<Integer, List<TankReceiptDetailBean>>();
        for ( TankReceiptDetailBean bean : tankList )
        {
            if ( !productNameToTankMap.containsKey( bean.getTank().getProductId() ) )
            {
                productNameToTankMap.put( bean.getTank().getProductId(),
                        new ArrayList<TankReceiptDetailBean>() );
            }
            productNameToTankMap.get( bean.getTank().getProductId() ).add( bean );
        }
        for ( ProductClosingBalance product : fuelProducts )
        {
            beans.add( new FuelReceiptProdDetailBean( product,
                    productNameToTankMap.get( product.getProductId() ) ) );
        }
        return beans;
    }
    
    public static List<FuelSalesProdDetailBean> convertToFuelSales(
            final List<ProductClosingBalance> fuelProducts,
            final List<TankSaleDetailBean> tankList )
    {
        final List<FuelSalesProdDetailBean> beans =
                new ArrayList<FuelSalesProdDetailBean>();
        //Prepare product to tank map
        final Map<Integer, List<TankSaleDetailBean>> productNameToTankMap =
                new HashMap<Integer, List<TankSaleDetailBean>>();
        for ( TankSaleDetailBean bean : tankList )
        {
            if ( !productNameToTankMap.containsKey( bean.getTank().getProductId() ) )
            {
                productNameToTankMap.put( bean.getTank().getProductId(),
                        new ArrayList<TankSaleDetailBean>() );
            }
            productNameToTankMap.get( bean.getTank().getProductId() ).add( bean );
        }
        for ( ProductClosingBalance product : fuelProducts )
        {
            beans.add( new FuelSalesProdDetailBean( product,
                    productNameToTankMap.get( product.getProductId() ) ) );
        }
        return beans;
    }
    
    public static List<EmpSalBean> convertToEmpSalBeans(
            final List<EmployeeMonthlyStatus> employeeMonthlyStatuses,
            final List<EmployeeTrxDetailBean> trxDetailBeans )
    {
        final List<EmpSalBean> beans = new ArrayList<EmpSalBean>();
        //Prepare product to tank map
        final Map<Integer, List<EmployeeTrxDetailBean>> empIdToTrxBeans =
                new HashMap<Integer, List<EmployeeTrxDetailBean>>();
        for ( EmployeeTrxDetailBean bean : trxDetailBeans )
        {
            if ( !empIdToTrxBeans.containsKey( bean.getParty().getId() ) )
            {
            	empIdToTrxBeans.put( bean.getParty().getId(),
                        new ArrayList<EmployeeTrxDetailBean>() );
            }
            empIdToTrxBeans.get( bean.getParty().getId() ).add( bean );
        }
        for ( EmployeeMonthlyStatus employee : employeeMonthlyStatuses )
        {
        	BigDecimal totalSalary = employee.getTolSalThisMonth();
        	BigDecimal totalIncentive = employee.getTolInceThisMonth();
        	//Add the salary and incentives paid today
        	if ( empIdToTrxBeans.containsKey( employee.getId() ) )
            {
        		for ( EmployeeTrxDetailBean bean : empIdToTrxBeans.get( employee.getId() ) )
        		{
        			totalSalary = totalSalary.add(bean.getSalary());
        			totalIncentive = totalIncentive.add(bean.getIncentive());
        		}
            }
        	employee.setTolInceThisMonth(totalIncentive);
        	employee.setTolSalThisMonth(totalSalary);
            beans.add( new EmpSalBean( employee ) );
        }
        return beans;
    }
    
    public static List<PartyDetailBean> convertToPartySales( final List<PartyClosingBalance> parties,
            final List<PartyTransaction> partyTransactions )
    {
        final List<PartyDetailBean> beans = new ArrayList<PartyDetailBean>();
        //Prepare party map
        final Map<Integer, PartyClosingBalance> partyMap = new HashMap<Integer, PartyClosingBalance>();
        for ( PartyClosingBalance party : parties )
        {
            partyMap.put( party.getId(), party );
        }
        
        for ( PartyTransaction transaction : partyTransactions )
        {
            if ( !transaction.getTransactionType().endsWith( "_S" ) )
            {
                final PartyDetailBean bean = new PartyDetailBean();
                bean.setEditableColList( new ArrayList<Integer>());
                bean.setUpdated( false );
                bean.setParty( partyMap.get( transaction.getPartyId() ) );
                if ( transaction.getTransactionType().startsWith( "CREDIT" ) )
                {
                    bean.setCredit( transaction.getAmount() );
                    bean.setCreditDetail( transaction.getTransactionDetail() );
                }
                else if ( transaction.getTransactionType().startsWith( "DEBIT" ) )
                {
                    bean.setDebit( transaction.getAmount() );
                    bean.setDebitDetail( transaction.getTransactionDetail() );
                }
                
                beans.add( bean );
            }
        }
        return beans;
    }
    
    public static List<TankSaleDetailBean> convertToTankSales(
            final List<TankClosingStock> closingTankStocks,
            final List<MeterSalesBean> meterSalesBeans )
    {
        final List<TankSaleDetailBean> beans =
                new ArrayList<TankSaleDetailBean>();
        //Prepare product to tank map
        final Map<Integer, List<MeterSalesBean>> productNameToTankMap =
                new HashMap<Integer, List<MeterSalesBean>>();
        for ( MeterSalesBean bean : meterSalesBeans )
        {
            if ( !productNameToTankMap.containsKey( bean.getMeter().getTankId() ) )
            {
                productNameToTankMap.put( bean.getMeter().getTankId(), new ArrayList<MeterSalesBean>() );
            }
            productNameToTankMap.get( bean.getMeter().getTankId() ).add( bean );
        }
        for ( TankClosingStock tank : closingTankStocks )
        {
            beans.add( new TankSaleDetailBean( tank,
                    productNameToTankMap.get( tank.getTankId() ) ) );
        }
        return beans;
    }
    
    public static List<ProductWrapper> convertToProductWrapper( final List<ProductClosingBalance> products )
    {
        final List<ProductWrapper> beans =
                new ArrayList<ProductWrapper>();
        for ( ProductClosingBalance product : products )
        {
            beans.add( new ProductWrapper( product ) );
        }
        return beans;
    }
    
    public static List<ChequeTransWrapper> convertToChequeTrans( final List<PartyTransaction> partyTrans )
    {
        final List<ChequeTransWrapper> beans = new ArrayList<ChequeTransWrapper>();
        for ( PartyTransaction trans : partyTrans )
        {
            beans.add( new ChequeTransWrapper( trans ) );
        }
        return beans;
    }
    
    public static void convertToPartyWrapper( final List<Party> parties, final List<PartyClosingBalance> partyClosingBalances,
            final List<PartyWrapper> employees,final List<PartyWrapper> banks, final List<PartyWrapper> creditParties )
    {
        final Map<Integer, Party> partyMap = new HashMap<Integer, Party>();
        for (Party party : parties )
        {
            partyMap.put( party.getPartyId(), party );
        }
        
        for (PartyClosingBalance party : partyClosingBalances )
        {
            if (PartyDao.MIN_EMP_ID <= party.getId() && PartyDao.MIN_BANK_ID > party.getId())
            {
                employees.add( new PartyWrapper(party, partyMap.get( party.getId() )) );
            }
            else if (PartyDao.MIN_BANK_ID <= party.getId() && PartyDao.MIN_PARTY_ID > party.getId())
            {
                banks.add( new PartyWrapper(party,partyMap.get( party.getId() )) );
            }
            else if (PartyDao.MIN_PARTY_ID <= party.getId())
            {
                creditParties.add( new PartyWrapper(party,partyMap.get( party.getId() )) );
            }
        }
    }
}
