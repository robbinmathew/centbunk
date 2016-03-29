package bronz.accounting.bunk.ui.panel;


import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.ui.HomePage;

public class SearchDetailsPanel extends BasePublicPanel
{
	private static final long serialVersionUID = 1L;
	
	public SearchDetailsPanel( final HomePage parentFrame ) throws BunkMgmtException
	{
	    super( parentFrame, "" );
	}
	
	@Override
    public String getTitleSuffix()
    {
        return "Summary";
    }
}
