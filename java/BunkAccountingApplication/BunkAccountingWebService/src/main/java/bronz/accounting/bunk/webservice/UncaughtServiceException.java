package bronz.accounting.bunk.webservice;

import bronz.accounting.bunk.framework.exceptions.BunkValidationException;
import org.apache.logging.log4j.LogManager;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UncaughtServiceException extends Throwable implements ExceptionMapper<Throwable>
{
    private static final long serialVersionUID = 1L;

    @Override
    public Response toResponse(Throwable exception)
    {
        LogManager.getLogger(UncaughtServiceException.class).error("UncaughtServiceException..", exception);
        if (exception instanceof BunkValidationException) {
            return Response.status(400).entity("Validation Error:" +  exception.getMessage()).type("text/plain").build();
        } else {
            EmailService.notifyError(exception, "UncaughtServiceException");
            return Response.status(500).entity("Something bad happened. Please try again !!" +  exception.getMessage()).type("text/plain").build();
        }
    }
}