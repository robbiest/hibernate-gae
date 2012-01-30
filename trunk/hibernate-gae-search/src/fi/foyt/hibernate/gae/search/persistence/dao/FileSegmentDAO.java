package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class FileSegmentDAO extends GenericDAO<FileSegment> {
  
  public FileSegmentDAO() {
    super("FILESEGMENT");
  }

  public FileSegment create(Long fileId, Long segmentNo, byte[] data) {
    FileSegment fileSegment = new FileSegment();
    fileSegment.setData(data);
    fileSegment.setFileId(fileId);
    fileSegment.setSegmentNo(segmentNo);
    return persist(fileSegment);
  }

  public FileSegment findByFileIdAndSegmentNo(Long fileId, Integer segmentNo) {
    Query query = new Query(getKind())
      .addFilter("fileId", FilterOperator.EQUAL, fileId)
      .addFilter("segmentNo", FilterOperator.EQUAL, segmentNo);
    
    return getSingleObject(query);
  }
  
  public List<FileSegment> listByFileId(Long fileId) {
    Query query = new Query(getKind()).addFilter("fileId", FilterOperator.EQUAL, fileId);
  
    return listObjects(getDatastoreService().prepare(query).asList(FetchOptions.Builder.withDefaults()));
  }
  
  public void updateData(FileSegment fileSegment, byte[] data) {
    fileSegment.setData(data);
    persist(fileSegment); 
  }

}
