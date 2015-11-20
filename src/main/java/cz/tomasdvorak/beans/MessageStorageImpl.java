package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.MessageEntry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Message storage is simple stateless bean without any notion/logic of tenants. The link between tenant and storage
 * is managed purely by EntityManager, which is injected by CDI container and created by {@link cz.tomasdvorak.multitenancy.ProxyEntityManager}.
 */
@Stateless
public class MessageStorageImpl implements MessageStorage {

    /**
     * Injected, tenant aware, EntityManager.
     */
    @Inject
    private EntityManager entityManager;

    @Override
    public void saveMessage(final String message) {
        entityManager.persist(new MessageEntry(message));
    }

    @Override
    public List<MessageEntry> getAllMessages() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MessageEntry> q = cb.createQuery(MessageEntry.class);
        final Root<MessageEntry> c = q.from(MessageEntry.class);
        q.select(c);
        q.orderBy(cb.asc(c.get("created")));
        final TypedQuery<MessageEntry> query = entityManager.createQuery(q);
        return query.getResultList();
    }
}
