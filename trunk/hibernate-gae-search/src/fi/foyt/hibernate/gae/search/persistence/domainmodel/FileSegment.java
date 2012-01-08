package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FileSegment {

  public Long getId() {
    return id;
  }
  
  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }
  
  public Long getFileId() {
    return fileId;
  }
  
  public void setFileId(Long fileId) {
    this.fileId = fileId;
  }
  
  public Integer getSegmentNo() {
    return segmentNo;
  }
  
  public void setSegmentNo(Integer segmentNo) {
    this.segmentNo = segmentNo;
  }

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;
  
  @Persistent
  private byte[] data;

  @Persistent
  private Long fileId;

  @Persistent
  private Integer segmentNo;
}
