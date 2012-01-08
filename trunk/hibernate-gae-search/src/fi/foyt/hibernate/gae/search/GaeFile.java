package fi.foyt.hibernate.gae.search;

import java.io.Serializable;

import fi.foyt.hibernate.gae.search.persistence.dao.FileJdoDAO;
import fi.foyt.hibernate.gae.search.persistence.dao.FileSegmentJdoDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class GaeFile implements Serializable {
  
  public static final int SEGMENT_SIZE = 500;
  private static final long serialVersionUID = 1l;

  public GaeFile(String fileName, GaeDirectory directory) {
    this.fileName = fileName;
    this.directory = directory;
  }

  public synchronized long getLength() {
    return getFile().getDataLength();
  }

  public synchronized long getLastModified() {
    return getFile().getModified();
  }

  protected synchronized void setLastModified(long lastModified) {
    FileJdoDAO fileJdoDAO = new FileJdoDAO();
    File file = getFile();
    fileJdoDAO.updateModified(file, lastModified);
  }
  
  protected synchronized void resetFile() {
    FileJdoDAO fileDAO = new FileJdoDAO();
    FileSegmentJdoDAO fileSegmentJdoDAO = new FileSegmentJdoDAO();

    File file = getFile();
    if (file != null) {
      fileSegmentJdoDAO.deleteByFileId(file.getId());
      fileDAO.updateDataLength(file, 0l);  
    }
  }
  
  public void updateLength(long length) {
    FileJdoDAO fileDAO = new FileJdoDAO();
    fileDAO.updateDataLength(getFile(), length);
  }
  
  protected synchronized int getFileSegmentsCount() {
    FileSegmentJdoDAO fileSegmentJdoDAO = new FileSegmentJdoDAO();
    return fileSegmentJdoDAO.listByFileId(getFile().getId()).size();
  }
  
  protected synchronized FileSegment getFileSegment(int index) {
    FileSegmentJdoDAO fileSegmentJdoDAO = new FileSegmentJdoDAO();
    return fileSegmentJdoDAO.findByFileIdAndSegmentNo(getFile().getId(), index);
  }
  
  protected synchronized int getNewSegment() {
    int newIndex = getFileSegmentsCount();
    FileSegmentJdoDAO fileSegmentJdoDAO = new FileSegmentJdoDAO();
    fileSegmentJdoDAO.create(getFile().getId(), newIndex, null);
    return newIndex;
  }
  
  private synchronized File getFile() {
    FileJdoDAO fileJdoDAO = new FileJdoDAO();
    File file = fileJdoDAO.findByDirectoryIdAndName(directory.getDirectoryId(), fileName);
    if (file == null) {
      file = fileJdoDAO.create(directory.getDirectoryId(), fileName, 0l, System.currentTimeMillis(), 0l, 0l);
    }
    
    return file;
  }

  private String fileName;
  private GaeDirectory directory;

}
