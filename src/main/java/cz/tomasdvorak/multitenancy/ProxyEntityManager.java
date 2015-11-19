package cz.tomasdvorak.multitenancy;

import cz.tomasdvorak.beans.TenantLoader;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Proxy;

@RequestScoped
public class ProxyEntityManager {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    TenantLoader tenantLoader;

    private final EntityManager proxy;

    public ProxyEntityManager() {
        proxy = (EntityManager)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class },
            (proxy, method, args) -> method.invoke(getCurrentEntityManager(), args));
    }

    @Produces
    EntityManager getEntityManager() {
        return proxy;
    };

    private EntityManager getCurrentEntityManager() {
        final String currentTenant = TenantHolder.getCurrentTenant();
        if (currentTenant != null) {
            System.out.println("Returning connection for tenant " + currentTenant);
            final EntityManager em = tenantLoader.getEntityManagerFactory(currentTenant).createEntityManager();
            em.joinTransaction();
            return em;
        }
        System.out.println("Returning connection for default");
        return entityManager;
    }
}
