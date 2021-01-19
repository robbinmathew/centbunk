package bronz.utilities.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractScheduledService
{
    private static final Logger LOG = LogManager.getLogger(
            AbstractScheduledService.class );
    
    protected boolean terminateService;
    protected final String serviceName;
    
    public AbstractScheduledService( final String serviceName )
    {
        super();
        this.terminateService = false;
        this.serviceName = serviceName;
    }
    public abstract void executeJob();

    public void stop()
    {
        LOG.info( "Stopping service:" + this.serviceName );
        this.terminateService = true;
        synchronized(this)
        {
           this.notify();
        }
    }
    public void start( final long initialSleepMillisecs,
            final long afterExecutionSleepMillisecs )
    {
        LOG.info( "Starting service:" + this.serviceName );
        LOG.info( "Start delay:" + initialSleepMillisecs );
        LOG.info( "Execution interval:" + afterExecutionSleepMillisecs );
        this.terminateService = false;
        haltService( initialSleepMillisecs );
        while( !terminateService )
        {
            LOG.info( "Executing job:" + this.serviceName );
            try
            {
                executeJob();
                haltService( afterExecutionSleepMillisecs );
            }
            catch( final Exception exception )
            {
                LOG.error( "Error in service:" + this.serviceName, exception );
            }
        }
        LOG.info( "Service:" + this.serviceName + " stopped" );
    }
    
    private void haltService( final long millisecs )
    {
        synchronized(this)
        {
            try
            {
               this.wait( millisecs );
            }
            catch( final InterruptedException ie)
            {
                LOG.error( "Wait interrupted in service:" + this.serviceName, ie );
            }
         }
    }
}
