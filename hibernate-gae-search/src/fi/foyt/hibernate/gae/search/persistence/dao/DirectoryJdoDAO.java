package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class DirectoryJdoDAO extends GenericJdoDAO {

  public Directory create(String name) {
    PersistenceManager persistenceManager = getPersistenceManager();
    
    Directory directory = new Directory();
    directory.setName(name);
    persistenceManager.makePersistent(directory);
    persistenceManager.close();
    return directory;
  }

  public Directory findById(Long directoryId) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(Directory.class);
    query.setFilter("id == i");
    query.declareParameters("Long i");
    @SuppressWarnings("unchecked")
    List<Directory> directories = (List<Directory>) query.execute(directoryId);
    Directory directory = directories.size() == 1 ? directories.get(0) : null;
    persistenceManager.close();
    return directory;
  }
  
  public Directory findByName(String name) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(Directory.class);
    query.setFilter("name == n");
    query.declareParameters("String n");
    @SuppressWarnings("unchecked")
    List<Directory> directories = (List<Directory>) query.execute(name);
    Directory directory = directories.size() == 1 ? directories.get(0) : null;
    persistenceManager.close();
    return directory;
  }
  
  public List<Long> listIds() {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery("select id from " + Directory.class.getName());
    @SuppressWarnings("unchecked")
    List<Object> idObjects = (List<Object>) query.execute();
    List<Long> ids = new ArrayList<Long>(idObjects.size());
    for (Object idObject : idObjects) {
      ids.add(new Long(idObject.toString()));
    }
    
    persistenceManager.close();
    return ids;
  }
  
  public void delete(Directory directory) {
    PersistenceManager persistenceManager = getPersistenceManager();
    persistenceManager.deletePersistent(directory);
    persistenceManager.close();
  }

  public void deleteAll() {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(Directory.class);
    @SuppressWarnings("unchecked")
    List<Directory> directories = (List<Directory>) query.execute();
    persistenceManager.deletePersistentAll(directories);
    persistenceManager.close();
  }
}
