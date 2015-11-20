package cz.tomasdvorak.beans;

import cz.tomasdvorak.dto.AuthenticationHeader;
import cz.tomasdvorak.exceptions.InvalidCredentialsException;
import cz.tomasdvorak.dto.Message;
import cz.tomasdvorak.entities.Tenant;
import cz.tomasdvorak.exceptions.UnknownRecipientException;
import cz.tomasdvorak.multitenancy.TenantWrapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;
import java.util.stream.Collectors;

@WebService
@Stateless
public class CommunicationServiceImpl implements CommunicationService {

    @Inject
    private MessageStorage storage;

    @Inject
    private TenantRegistry tenantRegistry;

    // another possible source of auth data  - WebServiceContext
    // depends on your security configuration in application server.
    // The wrapping with TenantWrapped could be then moved to an Interceptor, reading tenant name from ctx.
    // @Resource
    // private WebServiceContext ctx;

    @Override
    @WebMethod
    public void sendMessage(final String recipient, final String message) throws UnknownRecipientException {
        final Tenant tenant = tenantRegistry.getTenant(recipient).orElseThrow(() -> new UnknownRecipientException("Recipient '" + recipient + "' not found!"));
        getTenantStorage(tenant).saveMessage(message);
    }

    @Override
    @WebMethod
    public List<Message> readMessages(final AuthenticationHeader auth) throws InvalidCredentialsException {
        final Tenant tenant = getTenant(auth);
        return getTenantStorage(tenant)
                .getAllMessages()
                .stream()
                .map(entry -> new Message(entry.getMessage(), entry.getCreated())) // do not expose our LogEntry entities, convert them to Message objects!
                .collect(Collectors.toList());
    }

    private MessageStorage getTenantStorage(final Tenant tenant) {
        return TenantWrapper.wrap(tenant.getName(), MessageStorage.class, storage);
    }

    private Tenant getTenant(final AuthenticationHeader auth) throws InvalidCredentialsException {
        return tenantRegistry.getTenant(auth.getTenantName())
            .filter(tenant -> tenant.getPassword().equals(auth.getPassword()))
            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password!"));
    }

}
