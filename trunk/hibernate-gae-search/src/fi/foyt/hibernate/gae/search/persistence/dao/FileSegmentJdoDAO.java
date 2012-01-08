package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class FileSegmentJdoDAO extends GenericJdoDAO {
  
  public FileSegment create(Long fileId, Integer segmentNo, byte[] data) {
    PersistenceManager persistenceManager = getPersistenceManager();
    FileSegment fileSegment = new FileSegment();
    fileSegment.setData(data);
    fileSegment.setFileId(fileId);
    fileSegment.setSegmentNo(segmentNo);
    persistenceManager.makePersistent(fileSegment);
    persistenceManager.close();
    return fileSegment;
  }

  public FileSegment findByFileIdAndSegmentNo(Long fileId, Integer segmentNo) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(FileSegment.class);
    query.setFilter("fileId == fId && segmentNo == sNo");
    query.declareParameters("Long fId,Integer sNo");
    @SuppressWarnings("unchecked")
    List<FileSegment> fileSegments = (List<FileSegment>) query.execute(fileId, segmentNo);
    FileSegment fileSegment = fileSegments.size() == 1 ? fileSegments.get(0) : null;
    persistenceManager.close();
    return fileSegment;
  }
  
  public List<FileSegment> listByFileId(Long fileId) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(FileSegment.class);
    query.setFilter("fileId == fId");
    query.declareParameters("Long fId");
    @SuppressWarnings("unchecked")
    List<FileSegment> fileSegments = (List<FileSegment>) query.execute(fileId);
    persistenceManager.close();
    return fileSegments;
  }
  
  public void updateData(FileSegment fileSegment, byte[] data) {
    PersistenceManager persistenceManager = getPersistenceManager();
    fileSegment.setData(data);
    persistenceManager.makePersistent(fileSegment);
    persistenceManager.close(); 
  }

  public void deleteByFileId(Long fileId) {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(FileSegment.class);
    query.setFilter("fileId == fId");
    query.declareParameters("Long fId");
    @SuppressWarnings("unchecked")
    List<FileSegment> fileSegments = (List<FileSegment>) query.execute(fileId);
    persistenceManager.deletePersistentAll(fileSegments);
    persistenceManager.close();
  }

  public void deleteAll() {
    PersistenceManager persistenceManager = getPersistenceManager();
    Query query = persistenceManager.newQuery(FileSegment.class);
    @SuppressWarnings("unchecked")
    List<FileSegment> fileSegments = (List<FileSegment>) query.execute();
    persistenceManager.deletePersistentAll(fileSegments);
    persistenceManager.close();
  }
  
}
