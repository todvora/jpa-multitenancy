package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.LogEntry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Stateless
public class SafeStorageImpl implements SafeStorage {

    @Inject
    EntityManager entityManager;

    @Override
    public void saveMessage(String message) {
        entityManager.persist(new LogEntry(message));
    }

    @Override
    public List<LogEntry> getAllMessages() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LogEntry> q = cb.createQuery(LogEntry.class);
        Root<LogEntry> c = q.from(LogEntry.class);
        q.select(c);
        TypedQuery<LogEntry> query = entityManager.createQuery(q);
        return query.getResultList();
    }
}
