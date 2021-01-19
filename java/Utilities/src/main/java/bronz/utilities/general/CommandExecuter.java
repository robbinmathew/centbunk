package bronz.utilities.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class StreamGobbler extends Thread
{
    private final InputStream inputStream;
    private final String type;
    private final Logger log;
    
    StreamGobbler(final InputStream inputStream, final String type, final Logger log)
    {
        this.inputStream = inputStream;
        this.type = type;
        this.log = log;
    }
    
    public void run()
    {
        try
        {
            final InputStreamReader streamReader = new InputStreamReader(this.inputStream);
            final BufferedReader reader = new BufferedReader(streamReader);
            String line=null;
            while ( (line = reader.readLine()) != null)
            	this.log.info( type + ">" + line);    
        } catch (final IOException ioe)
        {
        	this.log.error("Faced error while reading the line", ioe);  
        }
    }
}
public class CommandExecuter
{
	private static final Logger LOG = LogManager.getLogger( CommandExecuter.class );
	
    public static void executeWinCmd(final String commandName, final String command, final boolean printCmd)
    {
    	if (printCmd)
    	{
    		LOG.info("Executing command:" + command);
    	}
    	else
    	{
    		//The method should not print the command because it might contain passwords etc.
    		LOG.info("Executing command..");
    	}
        try
        {
        	final Runtime rt = Runtime.getRuntime();
            final Process proc = rt.exec(command);
            // any error message?
            final StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), commandName + ":ERROR", LOG);            
            
            // any output?
            final StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), commandName+ ":OUTPUT", LOG);
                          
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = proc.waitFor();
            LOG.info("Execution completed. Exit value:" + exitVal);        
        }
        catch (Throwable throwable)
        {
        	LOG.error("Failed to execute command", throwable);
        	throw new UtilException(throwable);
        }
    }
}
