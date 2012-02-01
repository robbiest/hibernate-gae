package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;

public class FileDAO extends GenericDAO<File> {

  public FileDAO() {
    super("FILE", false);
  }

  public File create(Long directoryId, String name, Long dataLength, Long modified, Long fileReadPointer, Long fileWritePointer) {
    File file = new File();
    file.setDataLength(dataLength);
    file.setModified(modified);
    file.setName(name);
    file.setDirectoryId(directoryId);
    file.setFileReadPointer(fileReadPointer);
    file.setFileWritePointer(fileWritePointer);
    
    return persist(file);
  }
  
  public File findByDirectoryIdAndName(Long directoryId, String name) {
    Query query = new Query(getKind())
      .addFilter("name", FilterOperator.EQUAL, name)
      .addFilter("directoryId", FilterOperator.EQUAL, directoryId);
    
    return getSingleObject(query);
  }

  public List<File> listByDirectoryId(Long directoryId) {
    Query query = new Query(getKind()).addFilter("directoryId", FilterOperator.EQUAL, directoryId);
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
