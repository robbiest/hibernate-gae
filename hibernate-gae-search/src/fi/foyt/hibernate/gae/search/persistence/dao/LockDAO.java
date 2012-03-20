package fi.foyt.hibernate.gae.search.persistence.dao;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock;

public class LockDAO extends GenericDAO<Lock> {

  public LockDAO() {
    super("LOCK", true);
  }

  public Lock create(String name) {
    Lock lock = new Lock();
    lock.setName(name);
    return persist(lock);
  }
  
  public Lock findByName(String name) {
    Query query = new Query(getKind()).addFilter("name", FilterOperator.EQUAL, name);
    
    return getSingleObject(query);
  }
}
