package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.Tenant;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

@Singleton
@Startup
public class TenantLoader {

    @PersistenceContext
    private EntityManager entityManager;

    private final Set<Tenant> tenants = new HashSet<>();
    private final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();

    private static final Logger logger = Logger.getLogger(TenantLoader.class);

    @PostConstruct
    protected void startupTenants() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tenant> q = cb.createQuery(Tenant.class);
        Root<Tenant> c = q.from(Tenant.class);
        q.select(c);
        TypedQuery<Tenant> query = entityManager.createQuery(q);
        final List<Tenant> tenants = query.getResultList();
        logger.info(String.format("Loaded %d tenants.", tenants.size()));
        tenants.forEach(tenant -> {
            this.tenants.add(tenant);
            final EntityManagerFactory emf = createEntityManagerFactory(tenant);
            entityManagerFactories.put(tenant.getName(), emf);
            logger.info("Tenant " + tenant.getName() + " loaded!");
        });
        this.tenants.addAll(tenants);
    }

    private EntityManagerFactory createEntityManagerFactory(Tenant tenant) {
        final Map<String, String> props = new TreeMap<>();
        logger.debug("Creating entity manager factory on schema '" + tenant.getSchemaName() + "' for tenant '" + tenant.getName() + "'.");
        props.put("hibernate.default_schema", tenant.getSchemaName());
        return Persistence.createEntityManagerFactory("test", props);
    }

    public Optional<Tenant> getTenant(final String tenantName) {
        return tenants.stream().filter(tenant -> tenant.getName().equals(tenantName)).findFirst();
    }

    public EntityManagerFactory getEntityManagerFactory(String tenantName) {
        return entityManagerFactories.get(tenantName);
    }
}
