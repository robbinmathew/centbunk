package bronz.accounting.bunk.party.model;

public enum PartyTransType
{
    NEW( "NEW" ),
    CREDIT( "CREDIT" ),
    DEBIT( "DEBIT" ),
    RECIEPT( "RECIEPT" );
    
    private final String type;
    
    private PartyTransType( final String type )
    {
        this.type = type;
    }
    
    public String toString()
    {
        return this.type;
    }

}
