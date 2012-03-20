package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class DirectoryDAO extends GenericDAO<Directory> {

  public DirectoryDAO() {
    super("DIRECTORY", true);
  }

  public Directory create(String name) {
    Directory directory = new Directory();
    directory.setName(name);
    return (Directory) persist(directory);
  }
  
  public Directory findById(Long id) {
  	return findObjectByKey(KeyFactory.createKey(getKind(), id));
  }

  public Directory findByKey(Key key) {
  	return findObjectByKey(key);
  }
  
  public Directory findByName(String name) {
    Query query = new Query(getKind()).addFilter("name", FilterOperator.EQUAL, name);
    return getSingleObject(query);
  }
    
  public List<Directory> list() {
    Query query = new Query(getKind());
    return getObjectList(query);
  }
}
