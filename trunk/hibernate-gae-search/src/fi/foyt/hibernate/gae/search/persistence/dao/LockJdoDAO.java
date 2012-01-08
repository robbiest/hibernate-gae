package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock;

public class LockJdoDAO extends GenericJdoDAO {

  public Lock create(String name) {
    PersistenceManager persistenceManager = getPersistenceManager();
    
    Lock lock = new Lock();
    lock.setName(name);
    persistenceManager.makePersistent(lock);
    persistenceManager.close();
    return lock;
  }
  
  public Lock findByName(String name) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(Lock.class);
    query.setFilter("name == n");
    query.declareParameters("String n");
    @SuppressWarnings("unchecked")
    List<Lock> locks = (List<Lock>) query.execute(name);
    Lock file = locks.size() == 1 ? locks.get(0) : null;
    persistenceManager.close();
    return file;
  }
  
  public void deleteByName(String name) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(Lock.class);
    query.setFilter("name == n");
    query.declareParameters("String n");
    @SuppressWarnings("unchecked")
    List<Lock> locks = (List<Lock>) query.execute(name);
    persistenceManager.deletePersistentAll(locks);
    persistenceManager.close();
  }

  public void deleteAll() {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(Lock.class);
    @SuppressWarnings("unchecked")
    List<Lock> locks = (List<Lock>) query.execute();
    persistenceManager.deletePersistentAll(locks);
    persistenceManager.close();
  }
}
