package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;

public class WorkList extends AbstractObject {
	
	public WorkList() {
    super("WORKLIST");
  }
  
  public WorkList(Directory directory) {
    super("WORKLIST", directory.getKey());
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  @Override
  public Entity toEntity() {
    Entity entity = newEntity();
    
    if (data != null)
      entity.setProperty("data", new com.google.appengine.api.datastore.Blob(data));
    
    return entity;
  }

  @Override
  public void loadFromEntity(Entity entity) {
    if (entity.getKey() != null) {
      this.setKey(entity.getKey());
    }
    
    com.google.appengine.api.datastore.Blob dataBlob = (Blob) entity.getProperty("data"); 
    this.data = dataBlob != null ? dataBlob.getBytes() : null;
  }

  private byte[] data;
}
