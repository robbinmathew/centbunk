package bronz.accounting.bunk.ui.party.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=false, uniqueIdCol=true, minNoOfRows=10, errorValidationMethod="errorValidation" )
public class EmployeeTrxDetailBean
{
	public static final String SALARY_MSG = "SALARY";
	public static final String INCE_MSG = "INCEN";
	public static final List<String> ROWS = Arrays.asList(SALARY_MSG, INCE_MSG);
	
    @DataTableField( columnName="EMPLOYEE", preferedWidth=28, allowNull=false, columnNum=0)
    private EmployeeMonthlyStatus party;
    @DataTableField( columnName="DETAIL", preferedWidth=40, allowNull=false, columnNum=1)
    private Map<String, String> details = new HashMap<String, String>();
    @DataTableField( columnName="SALARY", preferedWidth=8, allowNull=false, columnNum=2)
    private BigDecimal salary = new CustomDecimal( 0 );
    @DataTableField( columnName="INCENTIVE", preferedWidth=8, allowNull=false, columnNum=3)
    private BigDecimal incentive = new CustomDecimal( 0 );
    @DataTableField( columnName="TOTAL(THIS MONTH)", preferedWidth=16, allowNull=false, columnNum=4, isEditable=false)
    private Map<String, String> totalThisMonth = new HashMap<String, String>();
    
    public EmployeeTrxDetailBean()
    {
        super();
    }

    /**
     * @return the party
     */
    public EmployeeMonthlyStatus getParty()
    {
        return party;
    }

    /**
     * @param party the party to set
     */
    public void setParty( EmployeeMonthlyStatus party )
    {
        this.party = party;
        if (this.party != null)
        {
        	this.totalThisMonth.put(SALARY_MSG, BunkUtil.setAsPrice(
        			party.getTolSalThisMonth()).toPlainString());
        	this.totalThisMonth.put(INCE_MSG, BunkUtil.setAsPrice(
        			party.getTolInceThisMonth()).toPlainString());
        }
    }

    
    public Map<String, String> getDetails()
    {
		return details;
	}
    
    public String getSalDetails()
    {
		return this.details.get(SALARY_MSG);
	}
    
    public String getInceDetails()
    {
		return this.details.get(INCE_MSG);
	}

	public void setDetails(Map<String, String> details)
	{
		if (details != null)
		{
			this.details = details;
		}
	}

	public BigDecimal getSalary()
	{
		return this.salary;
	}

	public void setSalary(BigDecimal salary)
	{
		this.salary = salary;
		if (this.party != null && this.salary != null)
        {
        	this.totalThisMonth.put(SALARY_MSG, BunkUtil.setAsPrice(
        			party.getTolSalThisMonth().add(salary)).toPlainString());
        }
	}

	public BigDecimal getIncentive()
	{
		return this.incentive;
	}

	public void setIncentive(BigDecimal incentive)
	{
		this.incentive = incentive;
		if (this.party != null && this.incentive != null)
        {
        	this.totalThisMonth.put(INCE_MSG, BunkUtil.setAsPrice(
        			party.getTolInceThisMonth().add(incentive)).toPlainString());
        }
	}

	public Map<String, String> getTotalThisMonth()
	{
		return totalThisMonth;
	}

	public void setTotalThisMonth(Map<String, String> totalThisMonth)
	{
		this.totalThisMonth = totalThisMonth;
	}

    public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( this.party != null &&
        		this.incentive.compareTo(BigDecimal.ZERO) > 0 &&
        		ValidationUtil.isNullOrEmpty(getInceDetails()) )
        {
        	errorMsg.append( "--Enter the details for the incentive paid for " + this.party.getName() );
        }
        return errorMsg.toString();
    }
}
