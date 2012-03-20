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

  public Long getFileReadPointer() {
    return fileReadPointer;
  }
  
  public void setFileReadPointer(Long fileReadPointer) {
    this.fileReadPointer = fileReadPointer;
  }
  
  public Long getFileWritePointer() {
    return fileWritePointer;
  }
  
  public void setFileWritePointer(Long fileWritePointer) {
    this.fileWritePointer = fileWritePointer;
  }

  @Override
  public Entity toEntity() {
    Entity entity = newEntity();
    
     entity.setProperty("name", this.name);
     entity.setProperty("modified", this.modified);
     entity.setProperty("dataLength", this.dataLength);
     entity.setProperty("fileReadPointer", this.fileReadPointer);
     entity.setProperty("fileWritePointer", this.fileWritePointer);
     
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
    this.fileReadPointer = (Long) entity.getProperty("fileReadPointer");
    this.fileWritePointer = (Long) entity.getProperty("fileWritePointer");
  }

  private String name;
  
  private Long modified;

  private Long dataLength;

  private Long fileReadPointer;
  
  private Long fileWritePointer;
}