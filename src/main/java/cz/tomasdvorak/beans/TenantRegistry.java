package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.Tenant;
import cz.tomasdvorak.entities.TodoEntry;
import cz.tomasdvorak.multitenancy.RuntimePersistenceGenerator;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.spi.PersistenceUnitTransactionType;
import java.util.*;

/**
 * Loads Tenants from DB, creates EntityManagerFactories for them.
 */
@Singleton
@Startup
public class TenantRegistry {

    /**
     * Default, container managed EntityManager
     */
    @PersistenceContext(unitName = "main")
    private EntityManager entityManager;

    private final Set<Tenant> tenants = new HashSet<>();
    private final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();

    private static final Logger logger = Logger.getLogger(TenantRegistry.class);

    @PostConstruct
    protected void startupTenants() {
        final List<Tenant> tenants = loadTenantsFromDB();
        logger.info(String.format("Loaded %d tenants from DB.", tenants.size()));
        tenants.forEach(tenant -> {
            this.tenants.add(tenant);
            final EntityManagerFactory emf = createEntityManagerFactory(tenant);
            entityManagerFactories.put(tenant.getName(), emf);
            logger.info("Tenant " + tenant.getName() + " loaded.");
        });
        this.tenants.addAll(tenants);
    }

    @PreDestroy
    protected void shutdownTenants() {
        entityManagerFactories.forEach((tenantName, entityManagerFactory) -> entityManagerFactory.close());
        entityManagerFactories.clear();
        tenants.clear();
    }

    private List<Tenant> loadTenantsFromDB() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tenant> q = cb.createQuery(Tenant.class);
        final Root<Tenant> c = q.from(Tenant.class);
        q.select(c);
        final TypedQuery<Tenant> query = entityManager.createQuery(q);
        return query.getResultList();
    }

    /**
     * Create new {@link EntityManagerFactory} using this tenant's schema.
     * @param tenant Tenant used to retrieve schema name
     * @return new EntityManagerFactory
     */
    private EntityManagerFactory createEntityManagerFactory(final Tenant tenant) {
        final RuntimePersistenceGenerator runtimePersistenceGenerator = new RuntimePersistenceGenerator(tenant.getSchemaName(), PersistenceUnitTransactionType.JTA, "jdbc/" + tenant.getSchemaName());
        runtimePersistenceGenerator.addAnnotatedClass(TodoEntry.class);
        runtimePersistenceGenerator.addProperty("hibernate.show_sql", "true");
        runtimePersistenceGenerator.addProperty("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform");
        runtimePersistenceGenerator.addProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        return runtimePersistenceGenerator.createEntityManagerFactory();
    }

    public Optional<Tenant> getTenant(final String tenantName) {
        return tenants.stream().filter(tenant -> tenant.getName().equals(tenantName)).findFirst();
    }

    /**
     * Returns EntityManagerFactory from the cache. EMF is created during tenant registration and initialization.
     * @see #startupTenants()
     */
    public EntityManagerFactory getEntityManagerFactory(final String tenantName) {
        return entityManagerFactories.get(tenantName);
    }
}
