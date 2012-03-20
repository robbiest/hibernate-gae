package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class FileSegmentDAO extends GenericDAO<FileSegment> {
  
  public FileSegmentDAO() {
    super("FILESEGMENT", true);
  }

  public FileSegment create(File file, Long segmentNo, byte[] data) {
    FileSegment fileSegment = new FileSegment(file);
    fileSegment.setData(data);
    fileSegment.setSegmentNo(segmentNo);
    return persist(fileSegment);
  }

  public FileSegment findByFileAndSegmentNo(File file, Integer segmentNo) {
    Query query = new Query(getKind(), file.getKey())
      .addFilter("segmentNo", FilterOperator.EQUAL, segmentNo);
    
    return getSingleObject(query);
  }
  
  public List<FileSegment> listByFile(File file) {
    Query query = new Query(getKind(), file.getKey());
  
    return getObjectList(query);
  }

  public void updateData(FileSegment fileSegment, byte[] data) {
    fileSegment.setData(data);
    persist(fileSegment); 
  }
}
