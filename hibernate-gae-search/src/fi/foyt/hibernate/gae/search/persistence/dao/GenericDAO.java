package fi.foyt.hibernate.gae.search.persistence.dao;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.AbstractObject;

public class GenericDAO<T extends AbstractObject> {
  
  public GenericDAO(String kind, boolean cached) {
    this.kind = kind;
    this.cached = cached;
  }
  
  public String getKind() {
    return kind;
  }

  protected DatastoreService getDatastoreService() {
    return DatastoreServiceFactory.getDatastoreService();
  }
  
  public void delete(T object) {
    if (object != null && object.getKey() != null) {
      getDatastoreService().delete(object.getKey());
      if (cached) {
        purgeCachedEntity(object.getKey());
      }
    }
  }
  
  protected T persist(T object) {
    Key key = persist(object.toEntity());
    object.setKey(key);
    
    if (cached == true) {
      purgeCachedEntity(key);
    }
    
    return object;
  }

  protected T findObjectByKey(Key key) {
    Entity entity = findEntityByKey(key);
    return createObjectFromEntity(entity);
  }
  
  protected T getSingleObject(Query query) {
    Entity entity = getSingleEntity(query);
    if (entity == null)
    	return null;
    
    if (cached) {
      Entity cachedEntity = getCachedEntity(entity.getKey());
      if (cachedEntity != null) {
        entity = cachedEntity;
      } else {
        try {
          // cached entities fetch only keys so we need to load entities before returning them
          entity = getDatastoreService().get(entity.getKey());
          cacheEntity(entity);
        } catch (EntityNotFoundException e) {
          entity = null;
        }
      }
    } 
    
    return createObjectFromEntity(entity);    
  }

  protected List<T> getObjectList(Query query) {
    return listObjects(prepareQuery(query).asList(FetchOptions.Builder.withDefaults()));
  }
  
  private List<T> listObjects(List<Entity> entities) {
    List<T> result = new ArrayList<T>();
    
    if (cached) {
      for (Entity entity : entities) {
        Entity cachedEntity = getCachedEntity(entity.getKey());
        if (cachedEntity != null) {
          entity = cachedEntity;
        } else {
          try {
            // cached entities fetch only keys so we need to load entities before returning them
            entity = getDatastoreService().get(entity.getKey());
            cacheEntity(entity);
          } catch (EntityNotFoundException e) {
            entity = null;
          }
        }
        
        result.add(createObjectFromEntity(entity));
      }
    } else {
      for (Entity entity : entities) {
        result.add(createObjectFromEntity(entity));
      }
    }
    
    return result;
  }
  
  private PreparedQuery prepareQuery(Query query) {
    if (cached)
      return getDatastoreService().prepare(query.setKeysOnly());
    else
      return getDatastoreService().prepare(query);
  }
  
  private Entity getCachedEntity(Key key) {
    MemcacheService cache = getEntityCache();
    return (Entity) cache.get(key);
  }
  
  private Key persist(Entity entity) {
    return getDatastoreService().put(entity);
  }

  private Entity findEntityByKey(Key key) {
    try {
      if (cached == true) {
        Entity cachedEntity = getCachedEntity(key);
        if (cachedEntity == null) {
          Entity entity = getDatastoreService().get(key);
          cacheEntity(entity);
          return entity;
        } else {
          return cachedEntity;
        }
      } else {
        return getDatastoreService().get(key);
      }      
    } catch (EntityNotFoundException e) {
      return null;
    }
  }
  
  private Entity getSingleEntity(Query query) {
    List<Entity> entities = prepareQuery(query).asList(FetchOptions.Builder.withLimit(1));
    if (entities.size() == 1)
      return entities.get(0);

    return null;
  }

  private void cacheEntity(Entity entity) {
    MemcacheService cache = getEntityCache();    
    cache.put(entity.getKey(), entity);
  }
  
  private void purgeCachedEntity(Key key) {
    MemcacheService cache = getEntityCache();    
    cache.delete(key);
  }
  
  private MemcacheService getEntityCache() {
    return MemcacheServiceFactory.getMemcacheService();
  }
  
  @SuppressWarnings("unchecked")
  private T createObjectFromEntity(Entity entity) {
    if (entity != null) {
      try {
        T object = (T) getGenericTypeClass().newInstance();
        object.loadFromEntity(entity);
        return object;
      } catch (Exception e) {
        return null;
      }
    }
    
    return null;
  }
  
  @SuppressWarnings("unchecked")
  private Class<? extends AbstractObject> getGenericTypeClass() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return ((Class<? extends AbstractObject>) parameterizedType.getActualTypeArguments()[0]);
  }
  
  private String kind;
  private boolean cached;
}
