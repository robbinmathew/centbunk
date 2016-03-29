package bronz.accounting.bunk.ui.party.model;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=false, minNoOfRows=5, errorValidationMethod="errorValidation",editableColumnListMethod="getEditableColList" )
public class PartyWrapper
{
    @DataTableField( columnName="PARTY NAME", preferedWidth=40, allowNull=false, columnNum=0)
    private String partyName;
    @DataTableField( columnName="DETAIL", preferedWidth=20, allowNull=false, columnNum=1)
    private String detail;
    @DataTableField( columnName="PHONE", preferedWidth=20, allowNull=false, columnNum=2)
    private String phone;
    @DataTableField( columnName="BALANCE", preferedWidth=10, allowNull=false, columnNum=3, isEditable=false)
    private BigDecimal balance;
    @DataTableField( columnName="STATUS", preferedWidth=10, allowNull=false, columnNum=4)
    private String status;
    
    
    private List<Integer> editableColList = null;
    private PartyClosingBalance partyClosingBalance;
    private Party party;
    
    public PartyWrapper( final PartyClosingBalance partyClosingBalance, final Party party )
    {
        super();
        setPartyClosingBalance( partyClosingBalance );
        setParty( party );
    }
    
    public PartyWrapper()
    {
        super();
        setPartyClosingBalance( null );
        setParty( null );
    }

    public String getPartyName()
    {
        return partyName;
    }

    public void setPartyName( String partyName )
    {
        this.partyName = partyName.toUpperCase().trim();
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail( String detail )
    {
        this.detail = detail.toUpperCase().trim();
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone( String phone )
    {
        this.phone = phone.trim();
    }

    public BigDecimal getBalance()
    {
        return balance;
    }

    public void setBalance( BigDecimal balance )
    {
        this.balance = balance;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    public PartyClosingBalance getPartyClosingBalance()
    {
        return partyClosingBalance;
    }
    
    public Party getParty()
    {
        return party;
    }
    
    private void setPartyClosingBalance(final PartyClosingBalance partyClosingBalance)
    {
        this.partyClosingBalance = partyClosingBalance;
        if (partyClosingBalance == null)
        {
            this.balance = CustomDecimal.ZERO;
        }
        else
        {
            this.balance = partyClosingBalance.getBalance();
        }
    }
    
    private void setParty(final Party party)
    {
        this.party = party;
        if (party == null)
        {
            this.partyName = "";
            this.detail = "";
            this.phone = "";
            this.status = "ACTIVE";
            this.editableColList = Arrays.asList( 0,1,2 );
        }
        else
        {
            this.partyName = party.getPartyName();
            this.detail = party.getPartyDetail();
            this.phone = party.getPartyPhone();
            this.status = party.getPartyStatus().replace( "_S", "" );
            if(party.getPartyStatus().endsWith( "_S"))
            {
                this.editableColList = new ArrayList<Integer>();
            }
            else
            {
                this.editableColList = null;
            }
        }
    }

    public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if (ValidationUtil.isNullOrEmpty( this.partyName ) )
        {
            errorMsg.append( "-- Please specify a party name." );
        }
        else if ("INACTIVE".equals( status ) && CustomDecimal.ZERO.compareTo( balance ) < 0 && (party == null ||
                (party != null && !party.getPartyStatus().endsWith( "_S" ))))
        {
            errorMsg.append( "-- " + this.partyName + " has outstanding balance and cannot be deactivated." );
        }
        return errorMsg.toString();
    }
    
    /**
     * @return the editableColList
     */
    public List<Integer> getEditableColList()
    {
        return editableColList;
    }
    
    public boolean isUpdated()
    {
        if (null != this.party && ( !this.party.getPartyName().equals( this.partyName ) ||
                !this.party.getPartyPhone().equals( this.phone ) ||
                !this.party.getPartyStatus().replaceAll( "_S", "" ).equals( this.status ) ||
                !this.party.getPartyDetail().equals( this.detail ) )) {
            return true;
        }
        return false;
    }
}
