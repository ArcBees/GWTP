package com.arcbees.carsample.server.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.arcbees.carsample.server.dao.objectify.Ofy;
import com.arcbees.carsample.server.dao.objectify.OfyFactory;
import com.arcbees.carsample.shared.domain.BaseEntity;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

public class BaseDao<T extends BaseEntity> {
    // private final Provider<EntityManager> entityManagerProvider;
    // private final Class<T> clazz;
    //
    // public BaseDao(final Class<T> clazz, final Provider<EntityManager> entityManagerProvider) {
    // this.clazz = clazz;
    // this.entityManagerProvider = entityManagerProvider;
    // }
    //
    // public T find(int id) {
    // return entityManager().find(clazz, id);
    // }
    //
    // public T put(T entity) {
    // EntityManager entityManager = entityManager();
    //
    // try {
    // entity = entityManager.merge(entity);
    // entityManager.persist(entity);
    // } catch (PersistenceException e) {
    // handleException(e);
    // }
    //
    // return entity;
    // }
    //
    // public void delete(T entity) {
    // EntityManager entityManager = entityManager();
    //
    // try {
    // entity = entityManager.merge(entity);
    // entityManager.remove(entity);
    // } catch (PersistenceException e) {
    // handleException(e);
    // }
    // }
    //
    // @SuppressWarnings("unchecked")
    // public List<T> getAll() {
    // Query query = entityManager().createQuery("select c from " + clazz.getSimpleName() + " c");
    //
    // return (List<T>) query.getResultList();
    // }
    //
    // @SuppressWarnings("unchecked")
    // public List<T> getSome(Integer offset, Integer limit) {
    // Query query = entityManager().createQuery("select c from " + clazz.getSimpleName() + " c")
    // .setFirstResult(offset)
    // .setMaxResults(limit);
    //
    // return (List<T>) query.getResultList();
    // }
    //
    // public Integer countAll() {
    // Query query = entityManager().createQuery("select count(*) from " + clazz.getSimpleName() + " c");
    // return ((Long) query.getSingleResult()).intValue();
    // }
    //
    // protected EntityManager entityManager() {
    // return entityManagerProvider.get();
    // }
    //
    // private void handleException(RuntimeException exception) {
    // if (exception.getCause() instanceof PersistenceException) {
    // throw (PersistenceException) exception.getCause();
    // }
    //
    // throw exception;
    // }

    private final Class<T> clazz;

    @Inject
    OfyFactory ofyFactory;

    private Ofy lazyOfy;

    protected BaseDao(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> getAll() {
        return ofy().query(clazz).list();
    }

    public T put(T object) {
        ofy().save().entity(object).now();

        return object;
    }

    public Collection<T> put(Iterable<T> entities) {
        return ofy().save().entities(entities).now().values();
    }

    public T get(Key<T> key) {
        return ofy().get(key);
    }

    public T get(Long id) {
        // work around for objectify cacheing and new query not having the latest data
        ofy().clear();

        return ofy().get(clazz, id);
    }

    public Boolean exists(Key<T> key) {
        return get(key) != null;
    }

    public Boolean exists(Long id) {
        return get(id) != null;
    }

    public List<T> getSubset(List<Long> ids) {
        return new ArrayList<T>(ofy().query(clazz).ids(ids).values());
    }

    public Map<Long, T> getSubsetMap(List<Long> ids) {
        return new HashMap<Long, T>(ofy().query(clazz).ids(ids));
    }

    public void delete(T object) {
        ofy().delete().entity(object);
    }

    public void delete(Long id) {
        Key<T> key = Key.create(clazz, id);
        ofy().delete().entity(key);
    }

    public void delete(List<T> objects) {
        ofy().delete().entities(objects);
    }

    public List<T> get(List<Key<T>> keys) {
        return Lists.newArrayList(ofy().load().keys(keys).values());
    }

    protected Ofy ofy() {
        if (lazyOfy == null) {
            lazyOfy = ofyFactory.begin();
        }
        return lazyOfy;
    }

    protected LoadType<T> query() {
        return ofy().query(clazz);
    }
}
