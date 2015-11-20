package cz.tomasdvorak.multitenancy;

import cz.tomasdvorak.beans.TenantRegistry;
import org.apache.log4j.Logger;

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
    private TenantRegistry tenantRegistry;

    private final EntityManager proxy;

    private static final Logger logger = Logger.getLogger(ProxyEntityManager.class);

    private ProxyEntityManager() {
        proxy = (EntityManager)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class },
            (proxy, method, args) -> method.invoke(getCurrentEntityManager(), args));
    }

    @Produces
    EntityManager getEntityManager() {
        return proxy;
    }

    private EntityManager getCurrentEntityManager() {
        final String currentTenant = TenantHolder.getCurrentTenant();
        if (currentTenant != null) {
            logger.debug("Returning connection for tenant " + currentTenant);
            final EntityManager em = tenantRegistry.getEntityManagerFactory(currentTenant).createEntityManager();
            em.joinTransaction();
            return em;
        }
        return entityManager;
    }
}
