package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;

public class FileDAO extends GenericDAO<File> {

  public FileDAO() {
    super("FILE", true);
  }

  public File create(Directory directory, String name, Long dataLength, Long modified, Long fileReadPointer, Long fileWritePointer) {
    File file = new File(directory);
    file.setDataLength(dataLength);
    file.setModified(modified);
    file.setName(name);
    file.setFileReadPointer(fileReadPointer);
    file.setFileWritePointer(fileWritePointer);
    
    return persist(file);
  }
  
  public File findByDirectoryAndName(Directory directory, String name) {
    Query query = new Query(getKind(), directory.getKey())
      .addFilter("name", FilterOperator.EQUAL, name);
    
    return getSingleObject(query);
  }

  public List<File> listByDirectory(Directory directory) {
    Query query = new Query(getKind(), directory.getKey());
    return getObjectList(query);
  }

  public void updateModified(File file, long modified) {
    file.setModified(modified);
    persist(file);
  }

  public void updateDataLength(File file, Long dataLength) {
    file.setDataLength(dataLength);
    persist(file);
  }

  public void updateFileReadPointer(File file, Long fileReadPointer) {
    file.setFileReadPointer(fileReadPointer);
    persist(file);
  }

  public void updateFileWritePointer(File file, Long fileWritePointer) {
    file.setFileWritePointer(fileWritePointer);
    persist(file);
  }
}
