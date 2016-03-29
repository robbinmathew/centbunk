package bronz.accounting.bunk.webservice;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;


/**
 * Created by pmathew on 1/16/16.
 */
public class BunkMgmtExceptionMapper implements ExceptionMapper<BunkMgmtException> {
    public Response toResponse(BunkMgmtException e)
    {
        return Response.ok().build();
    }
}
