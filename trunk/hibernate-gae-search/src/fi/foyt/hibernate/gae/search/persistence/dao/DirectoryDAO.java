package fi.foyt.hibernate.gae.search.persistence.dao;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class DirectoryDAO extends GenericDAO<Directory> {

  public DirectoryDAO() {
    super("DIRECTORY");
  }

  public Directory create(String name) {
    Directory directory = new Directory();
    directory.setName(name);
    return (Directory) persist(directory);
  }
  
  public Directory findByName(String name) {
    Query query = new Query(getKind()).addFilter("name", FilterOperator.EQUAL, name);
    return getSingleObject(query);
  }
}
