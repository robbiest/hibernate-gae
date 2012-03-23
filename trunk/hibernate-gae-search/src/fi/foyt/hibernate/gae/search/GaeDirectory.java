package fi.foyt.hibernate.gae.search;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

import fi.foyt.hibernate.gae.search.persistence.dao.FileDAO;
import fi.foyt.hibernate.gae.search.persistence.dao.FileSegmentDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class GaeDirectory extends Directory implements Serializable {

  private static final long serialVersionUID = 1l;

  private static Logger LOG = Logger.getLogger(GaeDirectory.class.getName());
  
  public GaeDirectory(fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory directory) {
    this.directory = directory;
    
    try {
      setLockFactory(new GaeLockFactory(this.getDirectory()));
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Could not initialize lock factory", e);
    }
  }

  @Override
  public final String[] listAll() {
    ensureOpen();
    
    FileDAO fileDAO = new FileDAO();
    List<File> files = fileDAO.listByDirectory(getDirectory());
    String[] result = new String[files.size()]; 
        
    int i = 0;
    for (File file : files) {
      result[i++] = file.getName();
    }
    
    return result;
  }

  @Override
  public final boolean fileExists(String name) {
    ensureOpen();
    FileDAO fileDAO = new FileDAO();
    File file = fileDAO.findByDirectoryAndName(getDirectory(), name);
    return file != null;
  }

  /** 
   * Returns the time the named file was last modified.
   * 
   * @throws IOException if the file does not exist
   */
  @Override
  public final long fileModified(String name) throws IOException {
    ensureOpen();
    FileDAO fileDAO = new FileDAO();
    File file = fileDAO.findByDirectoryAndName(getDirectory(), name);
    if (file == null)
      throw new FileNotFoundException();
    
    return file.getModified();
  }

  @Override
  public void touchFile(String name) throws IOException {
    // Deprecated
  }

  /** 
   * Returns the length in bytes of a file in the directory.
   * 
   * @throws IOException if the file does not exist
   */
  @Override
  public final long fileLength(String name) throws IOException {
    ensureOpen();
    FileDAO fileDAO = new FileDAO();
    File file = fileDAO.findByDirectoryAndName(getDirectory(), name);
    if (file == null)
      throw new FileNotFoundException();

    return file.getDataLength();
  }
  
  /** 
   * Removes an existing file in the directory.
   * 
   * @throws IOException if the file does not exist
   */
  @Override
  public void deleteFile(String name) throws IOException {
    ensureOpen();
    
    FileDAO fileDAO = new FileDAO();
    FileSegmentDAO fileSegmentDAO = new FileSegmentDAO();

    File file = fileDAO.findByDirectoryAndName(getDirectory(), name);
    if (file != null) {
      List<FileSegment> segments = fileSegmentDAO.listByFile(file);
      for (FileSegment segment : segments) {
        fileSegmentDAO.delete(segment);
      }
      fileDAO.delete(file);      
      LOG.fine("Deleted search index file " + name + " from directory " + getDirectory().getName());
    } else {
      throw new FileNotFoundException();
    }
  }

  /** 
   * Creates a new, empty file in the directory with the given name. Returns a stream writing this file. 
   */
  @Override
  public IndexOutput createOutput(String name) throws IOException {
    ensureOpen();
    
    if (fileExists(name))
      deleteFile(name);
    
    GaeFile file = new GaeFile(name, this);
    return new GaeIndexOutput(file);
  }

  /** 
   * Returns a stream reading an existing file. 
   * 
   * @throws IOException if the file does not exist
   */
  @Override
  public IndexInput openInput(String name) throws IOException {
    ensureOpen();
    
    FileDAO fileDAO = new FileDAO();
    File file = fileDAO.findByDirectoryAndName(getDirectory(), name);
    if ((file == null) || (file.getDataLength() < 1))
      throw new FileNotFoundException(name);

    return new GaeIndexInput(new GaeFile(name, this));
  }

  @Override
  public void close() {
    isOpen = false;
  }

  public fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory getDirectory() {
    return directory;
  }
  
  private fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory directory;
}