package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class FileSegmentDAO extends GenericDAO<FileSegment> {
	
	private static final String FILE_SEGMENTNO_LOOKUP = "FileSegment_FileSegmentNo";
	private static final String COUNT_FILE_KEY = "FileSegment_CountFile";
  
  public FileSegmentDAO() {
    super("FILESEGMENT", true);
  }

  public FileSegment create(File file, Long segmentNo, byte[] data) {
    FileSegment fileSegment = new FileSegment(file);
    fileSegment.setData(data);
    fileSegment.setSegmentNo(segmentNo);
    persist(fileSegment);
    
    String lookupKey = FILE_SEGMENTNO_LOOKUP + file.getKey() + "," + segmentNo;
    putLookupKey(lookupKey, fileSegment.getKey());
    updateCountCache(file, countByFile(file) + 1);
 
    return fileSegment;
  }

  public FileSegment findByFileAndSegmentNo(File file, Integer segmentNo) {
  	String lookupKey = FILE_SEGMENTNO_LOOKUP + file.getKey() + "," + segmentNo;
  	
  	Key fileSegmentKey = getLookupKey(lookupKey);
  	if (fileSegmentKey != null) {
  		if (isNullLookupKey(fileSegmentKey))
  			return null;
  		
  		return findObjectByKey(fileSegmentKey);
  	} 
  	
    Query query = new Query(getKind(), file.getKey())
      .addFilter("segmentNo", FilterOperator.EQUAL, segmentNo);
    
    FileSegment fileSegment = getSingleObject(query);

    if (fileSegment != null) {
      putLookupKey(lookupKey, fileSegment.getKey());
    } else {
      putLookupKey(lookupKey, createNullLookupKey());
    }
    
    return fileSegment;
  }
  
  public List<FileSegment> listByFile(File file) {
    Query query = new Query(getKind(), file.getKey());
  
    return getObjectList(query);
  }
  
	public int countByFile(File file) {
	  try {
  	  return (Integer) getCustomCache().get(COUNT_FILE_KEY + file.getKey());
  	} catch (Exception e) {
  	}
	  
	  int count = listByFile(file).size();
	  updateCountCache(file, count);
	  return count;
  }

  public void updateData(FileSegment fileSegment, byte[] data) {
    fileSegment.setData(data);
    persist(fileSegment); 
  }  
  
  @Override
  public void delete(FileSegment fileSegment) {
  	Key fileKey = fileSegment.getKey().getParent();
  	String lookupKey = FILE_SEGMENTNO_LOOKUP + fileKey + "," + fileSegment.getSegmentNo();
  	removeLookupKey(lookupKey);
  	clearCountCache(fileKey);

  	super.delete(fileSegment);
  }
  
  private void updateCountCache(File file, int count) {
  	try {
  	  getCustomCache().put(COUNT_FILE_KEY + file.getKey(), count);
  	} catch (Exception e) {
  	}
  }

  private void clearCountCache(Key fileKey) {
  	try {
    	getCustomCache().delete(COUNT_FILE_KEY + fileKey);
  	} catch (Exception e) {
  	}
  }
}
