package com.arcbees.carsample.server.dao;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.arcbees.carsample.shared.domain.BaseEntity;

public class BaseDao<T extends BaseEntity> {
    private final Provider<EntityManager> entityManagerProvider;
    private final Class<T> clazz;

    public BaseDao(final Class<T> clazz, final Provider<EntityManager> entityManagerProvider) {
        this.clazz = clazz;
        this.entityManagerProvider = entityManagerProvider;
    }

    public T find(int id) {
        return entityManager().find(clazz, id);
    }

    public T put(T entity) {
        EntityManager entityManager = entityManager();

        try {
            entity = entityManager.merge(entity);
            entityManager.persist(entity);
        } catch (PersistenceException e) {
            handleException(e);
        } catch (JDBCException e) {
            handleException(e);
        }

        return entity;
    }

    public void delete(T entity) {
        EntityManager entityManager = entityManager();

        try {
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        } catch (PersistenceException e) {
            handleException(e);
        } catch (JDBCException e) {
            handleException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        Query query = entityManager().createQuery("select c from " + clazz.getSimpleName() + " c");

        return (List<T>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<T> getSome(Integer offset, Integer limit) {
        Query query = entityManager().createQuery("select c from " + clazz.getSimpleName() + " c")
                .setFirstResult(offset)
                .setMaxResults(limit);

        return (List<T>) query.getResultList();
    }

    public Integer countAll() {
        Query query = entityManager().createQuery("select count(*) from " + clazz.getSimpleName() + " c");
        return ((Long) query.getSingleResult()).intValue();
    }

    protected EntityManager entityManager() {
        return entityManagerProvider.get();
    }

    private void handleException(RuntimeException exception) {
        if (exception.getCause() instanceof PersistenceException) {
            throw (PersistenceException) exception.getCause();
        }

        throw exception;
    }
}
