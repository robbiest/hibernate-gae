package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import com.google.appengine.api.datastore.Entity;

public class File extends AbstractObject {

	public File() {
		super("FILE");
  }
	
  public File(Directory directory) {
    super("FILE", directory.getKey());
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public Long getModified() {
    return modified;
  }
  
  public void setModified(Long modified) {
    this.modified = modified;
  }
  
  public Long getDataLength() {
    return dataLength;
  }
  
  public void setDataLength(Long dataLength) {
    this.dataLength = dataLength;
  }

  @Override
  public Entity toEntity() {
    Entity entity = newEntity();
    
     entity.setProperty("name", this.name);
     entity.setProperty("modified", this.modified);
     entity.setProperty("dataLength", this.dataLength);
     
     return entity;
  }

  @Override
  public void loadFromEntity(Entity entity) {
    if (entity.getKey() != null) {
      this.setKey(entity.getKey());
    }
    
    this.name = (String) entity.getProperty("name");
    this.modified = (Long) entity.getProperty("modified");
    this.dataLength = (Long) entity.getProperty("dataLength");
  }

  private String name;
  
  private Long modified;

  private Long dataLength;
}