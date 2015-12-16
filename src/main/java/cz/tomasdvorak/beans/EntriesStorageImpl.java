package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.TodoEntry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Entries storage is simple stateless bean without any notion/logic of tenants. The link between tenant and storage
 * is managed purely by EntityManager, which is injected by CDI container and created by {@link cz.tomasdvorak.multitenancy.ProxyEntityManager}.
 */
@Stateless
public class EntriesStorageImpl implements EntriesStorage {

    /**
     * Injected, tenant aware EntityManager.
     */
    @Inject
    private EntityManager entityManager;

    @Override
    public void saveEntry(final String text) {
        entityManager.persist(new TodoEntry(text));
    }

    @Override
    public List<TodoEntry> getAllEntries() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<TodoEntry> q = cb.createQuery(TodoEntry.class);
        final Root<TodoEntry> c = q.from(TodoEntry.class);
        q.select(c);
        q.orderBy(cb.asc(c.get("created")));
        final TypedQuery<TodoEntry> query = entityManager.createQuery(q);
        return query.getResultList();
    }
}
