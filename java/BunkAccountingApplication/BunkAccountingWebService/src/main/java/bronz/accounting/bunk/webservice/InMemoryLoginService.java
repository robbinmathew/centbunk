package bronz.accounting.bunk.webservice;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.util.security.Password;

public class InMemoryLoginService {
    private static final InMemoryLoginService INSTANCE = new InMemoryLoginService();

    private final HashLoginService loginService;
    private InMemoryLoginService() {
        loginService = new HashLoginService();
        loginService.putUser("robin", new Password("robin123"), new String[]{"user"});
        loginService.putUser("thomas", new Password("thomas123"), new String[]{"user"});
        loginService.putUser("robbin", new Password("Robbin@123"), new String[]{"user", "admin"});
        loginService.putUser("prabin", new Password("Prabin@123"), new String[]{"user", "admin"});
    }

    public HashLoginService getLoginService() {
        return loginService;
    }

    public static InMemoryLoginService getInstance() {
        return INSTANCE;
    }


}
