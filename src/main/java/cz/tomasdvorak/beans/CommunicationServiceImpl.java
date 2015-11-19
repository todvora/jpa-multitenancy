package cz.tomasdvorak.beans;

import cz.tomasdvorak.auth.AuthenticationHeader;
import cz.tomasdvorak.auth.InvalidCredentialsException;
import cz.tomasdvorak.dto.Message;
import cz.tomasdvorak.entities.LogEntry;
import cz.tomasdvorak.entities.Tenant;
import cz.tomasdvorak.multitenancy.TenantWrapper;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebService
@Stateless
public class CommunicationServiceImpl implements CommunicationService {

    @Inject
    private SafeStorage storage;

    @Inject
    private TenantLoader tenantLoader;

    @Resource
    private WebServiceContext ctx;

    @Override
    @WebMethod
    public void storeMessage(AuthenticationHeader auth, String message) throws InvalidCredentialsException {
        String tenantName = getTenant(auth);
        TenantWrapper.wrap(tenantName, SafeStorage.class, storage).saveMessage(message);
    }

    @Override
    @WebMethod
    public List<Message> readMessages(AuthenticationHeader auth) throws InvalidCredentialsException {
        String tenantName = getTenant(auth);
        // do not expose our LogEntry entities, convert them to Message objects!
        List<LogEntry> allMessages = TenantWrapper.wrap(tenantName, SafeStorage.class, storage).getAllMessages();
        return allMessages.stream().map(logEntry -> new Message(logEntry.getMessage(), logEntry.getCreated())).collect(Collectors.toList());
    }

    private String getTenant(final AuthenticationHeader auth) throws InvalidCredentialsException {
        System.out.println(auth);

        Optional<Tenant> tenant = tenantLoader.getTenant(auth.getTenantName());
        Tenant t = tenant.orElseThrow(() -> new InvalidCredentialsException("Username not found!"));
        if (!t.getPassword().equals(auth.getPassword())) {
            throw new InvalidCredentialsException("Password does not match!");
        }
        return t.getName();
    }

}
