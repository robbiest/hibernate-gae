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
import com.google.appengine.api.datastore.Query;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.AbstractObject;

public class GenericDAO<T extends AbstractObject> {
  
  public GenericDAO(String kind) {
    this.kind = kind;
  }
  
  public String getKind() {
    return kind;
  }

  protected DatastoreService getDatastoreService() {
    return DatastoreServiceFactory.getDatastoreService();
  }
  
  protected Key persist(Entity entity) {
    return getDatastoreService().put(entity);
  }
  
  protected T persist(T object) {
    Key key = persist(object.toEntity());
    object.setKey(key);
    return object;
  }
  
  protected Entity findEntityByKey(Key key) {
    try {
      return getDatastoreService().get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }
  
  protected T findObjectByKey(Key key) {
    Entity entity = findEntityByKey(key);
    return createObjectFromEntity(entity);
  }
  
  protected Entity getSingleEntity(Query query) {
    List<Entity> entities = getDatastoreService().prepare(query).asList(FetchOptions.Builder.withLimit(1));
    if (entities.size() == 1)
      return entities.get(0);
    return null;
  }
  
  protected T getSingleObject(Query query) {
    Entity entity = getSingleEntity(query);
    return createObjectFromEntity(entity);    
  }
  
  protected List<T> listObjects(List<Entity> entities) {
    List<T> result = new ArrayList<T>();
    
    for (Entity entity : entities) {
      result.add(createObjectFromEntity(entity));
    }
    
    return result;
  }
  
  public void delete(T object) {
    if (object != null && object.getKey() != null)
      delete(object.getKey());
  }

  public void delete(Key key) {
    getDatastoreService().delete(key);
  }
  
  public void delete(Entity entity) {
    delete(entity.getKey());
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
}
