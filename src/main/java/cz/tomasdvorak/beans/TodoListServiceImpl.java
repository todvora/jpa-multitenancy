package cz.tomasdvorak.beans;

import cz.tomasdvorak.dto.TodoItem;
import cz.tomasdvorak.exceptions.InvalidCredentialsException;
import cz.tomasdvorak.multitenancy.TenantInterceptor;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;
import java.util.stream.Collectors;

@WebService
@Stateless
@Interceptors(TenantInterceptor.class)
public class TodoListServiceImpl implements TodoListService {

    /**
     * Demonstrates that Tenant information is propagated to other EJBs too. The Entries service is not wrapped nor
     * anyhow uses the tenant information.
     */
    @Inject
    private EntriesStorage storage;

    @Override
    @WebMethod
    public void insertItem(final String todoItem) throws InvalidCredentialsException {
        // storage is already tenant-aware thanks to Interceptor configured on this WS
        storage.saveEntry(todoItem);
    }

    @Override
    @WebMethod
    public List<TodoItem> readItems() throws InvalidCredentialsException {
        // storage is already tenant-aware thanks to Interceptor configured on this WS
        return storage
            .getAllEntries()
            .stream()
            .map(entry -> new TodoItem(entry.getText(), entry.getCreated())) // do not expose our LogEntry entities, convert them to Message objects!
            .collect(Collectors.toList());
    }

}
