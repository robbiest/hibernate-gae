package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class File {

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Long getDirectoryId() {
    return directoryId;
  }
  
  public void setDirectoryId(Long directoryId) {
    this.directoryId = directoryId;
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
  
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String name;

  @Persistent
  private Long directoryId;
  
  @Persistent
  private Long modified;

  @Persistent
  private Long dataLength;

  @Persistent
  private Long fileReadPointer;
  
  @Persistent
  private Long fileWritePointer;
}