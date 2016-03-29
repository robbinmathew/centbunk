package bronz.accounting.bunk.ui.party.model;

import java.math.BigDecimal;


import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=true, minNoOfRows=5, errorValidationMethod="errorValidation" )
public class EmpSalBean
{
    @DataTableField( columnName="EMPLOYEE", preferedWidth=22, allowNull=false, columnNum=0)
    private EmployeeMonthlyStatus employee;
    @DataTableField( columnName="<html>INCENTIVES <font size=-2>(TOTAL)</font></html>", preferedWidth=15, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal incentives = new CustomDecimal( 0 );
    @DataTableField( columnName="<html>SALARY PAID <font size=-2>(TILL DATE)</font></html>", preferedWidth=21, allowNull=false, columnNum=2, isEditable=false)
    private BigDecimal salPaid = new CustomDecimal( 0 );
    @DataTableField( columnName="<html>ADVANCE SAL <font size=-2>(FOR NEXT MONTH)</font></html>", displayName="ADVANCE SAL", preferedWidth=21, allowNull=false, columnNum=3, isEditable=true)
    private BigDecimal salAdvance = new CustomDecimal( 0 );
    @DataTableField( columnName="<html>ACTUAL SALARY <font size=-2>(THIS MONTH)</font></html>", preferedWidth=21, allowNull=false, columnNum=4, isEditable=false)
    private BigDecimal salThisMonth = new CustomDecimal( 0 );
    
    public EmpSalBean( final EmployeeMonthlyStatus employee )
    {
        super();
        setEmployee(employee);
    }
    
    public int getId()
    {
        return employee.getId();
    }

	public EmployeeMonthlyStatus getEmployee()
	{
		return employee;
	}

	public void setEmployee(EmployeeMonthlyStatus employee)
	{
		this.employee = employee;
		if (this.employee != null)
		{
			setIncentives(employee.getTolInceThisMonth());
			setSalPaid(employee.getTolSalThisMonth());
			setSalThisMonth(employee.getTolSalThisMonth());
		}
	}

	public BigDecimal getIncentives()
	{
		return incentives;
	}

	public void setIncentives(BigDecimal incentives)
	{
		this.incentives = incentives;
	}

	public BigDecimal getSalPaid()
	{
		return salPaid;
	}

	public void setSalPaid(BigDecimal salPaid)
	{
		this.salPaid = salPaid;
	}

	public BigDecimal getSalAdvance()
	{
		return salAdvance;
	}

	public void setSalAdvance(BigDecimal salAdvance)
	{
		this.salAdvance = salAdvance;
		if (this.salAdvance != null && this.salAdvance.compareTo(BigDecimal.ZERO) >= 0 )
		{
			setSalThisMonth(getSalPaid().subtract(this.salAdvance));
		}
	}

	public BigDecimal getSalThisMonth()
	{
		return salThisMonth;
	}

	public void setSalThisMonth(BigDecimal salThisMonth)
	{
		this.salThisMonth = salThisMonth;
	}
	
	public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( this.salThisMonth.compareTo(BigDecimal.ZERO) < 0 )
        {
        	errorMsg.append( "--Negative salary for employee " + this.employee.getName());
        }
        return errorMsg.toString();
    }
}
