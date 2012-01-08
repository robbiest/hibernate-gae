package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;

public class FileJdoDAO extends GenericJdoDAO {

  public File create(Long directoryId, String name, Long dataLength, Long modified, Long fileReadPointer, Long fileWritePointer) {
    PersistenceManager persistenceManager = getPersistenceManager();
    
    File file = new File();
    file.setDataLength(dataLength);
    file.setModified(modified);
    file.setName(name);
    file.setDirectoryId(directoryId);
    file.setFileReadPointer(fileReadPointer);
    file.setFileWritePointer(fileWritePointer);
    
    persistenceManager.makePersistent(file);
    persistenceManager.close();
    return file;
  }
  
  public File findById(Long fileId) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(File.class);
    query.setFilter("id == i");
    query.declareParameters("Long i");
    @SuppressWarnings("unchecked")
    List<File> files = (List<File>) query.execute(fileId);
    File file = files.size() == 1 ? files.get(0) : null;
    persistenceManager.close();
    return file;
  }
  
  public File findByDirectoryIdAndName(Long directoryId, String name) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(File.class);
    query.setFilter("name == n && directoryId == d");
    query.declareParameters("String n,Long d");
    @SuppressWarnings("unchecked")
    List<File> files = (List<File>) query.execute(name, directoryId);
    File file = files.size() == 1 ? files.get(0) : null;
    persistenceManager.close();
    return file;
  }
  
  public List<Long> listIdsByDirectoryId(Long directoryId) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery("select id from " + File.class.getName() + " where directoryId == " + directoryId);
    @SuppressWarnings("unchecked")
    List<Object> idObjects = (List<Object>) query.execute();
    List<Long> ids = new ArrayList<Long>(idObjects.size());
    for (Object idObject : idObjects) {
      ids.add(new Long(idObject.toString()));
    }
    
    persistenceManager.close();
    return ids;
  }
  
  public List<String> listNamesByDirectoryId(Long directoryId) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery("select name from " + File.class.getName() + " where directoryId == " + directoryId);
    @SuppressWarnings("unchecked")
    List<Object> nameObjects = (List<Object>) query.execute();
    List<String> names = new ArrayList<String>(nameObjects.size());
    for (Object nameObject : nameObjects) {
      names.add(nameObject.toString());
    }
    
    persistenceManager.close();
    return names;
  }

  public void updateModified(File file, long modified) {
    PersistenceManager persistenceManager = getPersistenceManager();
    file.setModified(modified);
    persistenceManager.makePersistent(file);
    persistenceManager.close();
  }

  public void updateDataLength(File file, Long dataLength) {
    PersistenceManager persistenceManager = getPersistenceManager();
    file.setDataLength(dataLength);
    persistenceManager.makePersistent(file);
    persistenceManager.close();
  }

  public void updateFileReadPointer(File file, Long fileReadPointer) {
    PersistenceManager persistenceManager = getPersistenceManager();
    file.setFileReadPointer(fileReadPointer);
    persistenceManager.makePersistent(file);
    persistenceManager.close();
  }

  public void updateFileWritePointer(File file, Long fileWritePointer) {
    PersistenceManager persistenceManager = getPersistenceManager();
    file.setFileWritePointer(fileWritePointer);
    persistenceManager.makePersistent(file);
    persistenceManager.close();
  }
  
  public void deleteById(Long fileId) {
    PersistenceManager persistenceManager = getPersistenceManager();
    
    Query query = persistenceManager.newQuery(File.class);
    query.setFilter("id == i");
    query.declareParameters("Long i");
    @SuppressWarnings("unchecked")
    List<File> files = (List<File>) query.execute(fileId);
    persistenceManager.deletePersistentAll(files);
    
    persistenceManager.close();
  }
  
  public void deleteAll() {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(File.class);
    @SuppressWarnings("unchecked")
    List<File> files = (List<File>) query.execute();
    persistenceManager.deletePersistentAll(files);
    persistenceManager.close();
  }
}
