package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;

public class FileSegment extends AbstractObject {
  
	public FileSegment() {
    super("FILESEGMENT");
  }
	
  public FileSegment(File file) {
    super("FILESEGMENT", file.getKey());
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public Long getSegmentNo() {
    return segmentNo;
  }
  
  public void setSegmentNo(Long segmentNo) {
    this.segmentNo = segmentNo;
  }

  @Override
  public Entity toEntity() {
    Entity entity = newEntity();
    
    if (data != null)
      entity.setProperty("data", new com.google.appengine.api.datastore.Blob(data));
    entity.setProperty("segmentNo", segmentNo);
    
    return entity;
  }

  @Override
  public void loadFromEntity(Entity entity) {
    if (entity.getKey() != null) {
      this.setKey(entity.getKey());
    }
    
    com.google.appengine.api.datastore.Blob dataBlob = (Blob) entity.getProperty("data"); 
    this.data = dataBlob != null ? dataBlob.getBytes() : null;
    this.segmentNo = (Long) entity.getProperty("segmentNo");
  }

  private byte[] data;

  private Long segmentNo;
}
