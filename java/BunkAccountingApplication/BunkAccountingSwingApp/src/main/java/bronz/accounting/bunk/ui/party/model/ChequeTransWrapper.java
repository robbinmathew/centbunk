package bronz.accounting.bunk.ui.party.model;

import java.math.BigDecimal;

import java.util.List;

import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.util.EntityNameCache;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=true, minNoOfRows=5 )
public class ChequeTransWrapper
{
    public static final String CREDITED_TODAY = "CREDITED";
    public static final String STILL_PENDING = "PENDING";
    
    @DataTableField( columnName="SELECT", preferedWidth=8, allowNull=false, columnNum=4)
    private String selectedForCredit;
    @DataTableField( columnName="PARTY", preferedWidth=25, allowNull=false, columnNum=1, isEditable=false)
    private String partyName;
    @DataTableField( columnName="AMOUNT", preferedWidth=8, allowNull=false, columnNum=2, isEditable=false)
    private BigDecimal amount;
    @DataTableField( columnName="RECIEVED ON", preferedWidth=12, allowNull=false, columnNum=0, isEditable=false)
    private String recievedDate;
    @DataTableField( columnName="ORIG CHQ DETAIL", preferedWidth=27, allowNull=false, columnNum=3, isEditable=false)
    private String origChequeTransDetail;
    @DataTableField( columnName="CREDIT DETAIL", preferedWidth=20, allowNull=false, columnNum=5, isEditable=true)
    private String creditDetail;
    
    private List<Integer> editableColList = null;
    private PartyTransaction partyTransaction;
    
    public ChequeTransWrapper( final PartyTransaction partyTransaction )
    {
        super();
        this.partyTransaction = partyTransaction;
        this.amount = partyTransaction.getAmount();
        this.creditDetail = "";
        this.selectedForCredit = STILL_PENDING;
        this.partyName = EntityNameCache.getPartyName( partyTransaction.getPartyId() );
        this.origChequeTransDetail = this.partyTransaction.getTransactionDetail();
        this.recievedDate = this.partyTransaction.getDateText();
    }

    public String getSelectedForCredit()
    {
        return selectedForCredit;
    }

    public void setSelectedForCredit( String selectedForCredit )
    {
        this.selectedForCredit = selectedForCredit;
    }

    public String getPartyName()
    {
        return partyName;
    }

    public void setPartyName( String partyName )
    {
        this.partyName = partyName;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount( BigDecimal amount )
    {
        this.amount = amount;
    }

    public String getRecievedDate()
    {
        return recievedDate;
    }

    public void setRecievedDate( String recievedDate )
    {
        this.recievedDate = recievedDate;
    }

    public PartyTransaction getPartyTransaction()
    {
        return partyTransaction;
    }

    public String getOrigChequeTransDetail()
    {
        return origChequeTransDetail;
    }

    public void setOrigChequeTransDetail( String origChequeTransDetail )
    {
        this.origChequeTransDetail = origChequeTransDetail;
    }

    public String getCreditDetail()
    {
        return creditDetail;
    }

    public void setCreditDetail( String creditDetail )
    {
        this.creditDetail = creditDetail;
    }

    public String errorValidation()
    {
        return null;
    }
    
    /**
     * @return the editableColList
     */
    public List<Integer> getEditableColList()
    {
        return editableColList;
    }
    
}
