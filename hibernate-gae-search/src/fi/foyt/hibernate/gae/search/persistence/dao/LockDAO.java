package fi.foyt.hibernate.gae.search.persistence.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock;

public class LockDAO extends GenericDAO<Lock> {
	
	private static final String NAME_LOOKUP = "Lock_Name";

  public LockDAO() {
    super("LOCK", true);
  }

  public Lock create(Directory directory, String name) {
    Lock lock = new Lock(directory);
    lock.setName(name);
    return persist(lock);
  }
  
  public Lock findByDirectoryAndName(Directory directory, String name) {
  	String lookupKey = NAME_LOOKUP + directory.getKey() + "," + name;
  	
  	Key fileKey = getLookupKey(lookupKey);
  	if (fileKey != null) {
  		if (isNullLookupKey(fileKey))
  			return null;

  		return findObjectByKey(fileKey);
  	} 
  	
    Query query = new Query(getKind(), directory.getKey())
      .addFilter("name", FilterOperator.EQUAL, name);
    
    Lock lock = getSingleObject(query);
    if (lock != null) {
      putLookupKey(lookupKey, lock.getKey());
    } else {
    	putLookupKey(lookupKey, createNullLookupKey());
    }
    
    return lock;
  }
  
  @Override
  public void delete(Lock lock) {
  	String lookupKey = NAME_LOOKUP + lock.getKey().getParent() + "," + lock.getName();
  	
  	removeLookupKey(lookupKey);
  	
  	super.delete(lock);
  }
}
