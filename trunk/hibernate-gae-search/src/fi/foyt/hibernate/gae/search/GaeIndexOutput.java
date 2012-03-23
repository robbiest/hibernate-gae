package fi.foyt.hibernate.gae.search;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.store.IndexOutput;

import fi.foyt.hibernate.gae.search.persistence.dao.FileSegmentDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class GaeIndexOutput extends IndexOutput {
  
  private static final Logger LOG = Logger.getLogger(GaeIndexInput.class.getName());
  
  public GaeIndexOutput(GaeFile f) {
    file = f;
    currentSegmentIndex = -1;
  }

  @Override
  public void close() throws IOException {
    flush();    
    LOG.fine("Index output writer closed");
  }

  @Override
  public void seek(long pos) throws IOException {
    // set the file length in case we seek back and flush() has not been called yet
    setFileLength();
    if (pos < segmentStart || pos >= segmentStart + GaeFile.SEGMENT_SIZE) {
      currentSegmentIndex = (int) (pos / GaeFile.SEGMENT_SIZE);
      switchCurrentSegment();
    }

    segmentPosition = (int) (pos % GaeFile.SEGMENT_SIZE);
  }

  @Override
  public long length() {
    return file.getLength();
  }

  @Override
  public void writeByte(byte b) throws IOException {
  	try {
      if ((currentSegmentIndex < 0) || (segmentPosition == GaeFile.SEGMENT_SIZE)) {
        currentSegmentIndex++;
        switchCurrentSegment();
      }
  
      FileSegmentDAO fileSegmentDAO = new FileSegmentDAO();
      
      FileSegment fileSegment = getCurrentSegment();
      byte[] segmentData = getSegmentData(fileSegment, segmentPosition + 1);
      segmentData[segmentPosition++] = b;
      fileSegmentDAO.updateData(fileSegment, segmentData);
  	} catch (Exception e) {
  		throw new IOException(e); 
  	}
  }

  @Override
  public void writeBytes(byte[] b, int offset, int len) throws IOException {
  	try {
      assert b != null;
      while (len > 0) {
        if ((currentSegmentIndex < 0) || (segmentPosition == GaeFile.SEGMENT_SIZE)) {
          currentSegmentIndex++;
          switchCurrentSegment();
        }
        
        FileSegmentDAO fileSegmentDAO = new FileSegmentDAO();
        FileSegment fileSegment = getCurrentSegment();
        int remainInSegment = GaeFile.SEGMENT_SIZE - segmentPosition;
        int bytesToCopy = len < remainInSegment ? len : remainInSegment;
        
        byte[] segmentData = getSegmentData(fileSegment, segmentPosition + bytesToCopy);
  
        System.arraycopy(b, offset, segmentData, segmentPosition, bytesToCopy);
        fileSegmentDAO.updateData(fileSegment, segmentData);
        
        offset += bytesToCopy;
        len -= bytesToCopy;
        segmentPosition += bytesToCopy;
      }
  	} catch (Exception e) {
  		throw new IOException(e);
  	}
  }

  private final void switchCurrentSegment() throws IOException {
    if (currentSegmentIndex == file.getFileSegmentsCount()) {
      currentSegment = file.getNewSegment();
      currentSegmentIndex = currentSegment.getSegmentNo().intValue();
    } else {
    	currentSegment = getCurrentSegment();
    }
    
    segmentPosition = 0;
    segmentStart = (long) GaeFile.SEGMENT_SIZE * (long) currentSegmentIndex;
  }

  private void setFileLength() {
    long pointer = segmentStart + segmentPosition;
    if (pointer > file.getLength()) {
      file.updateLength(pointer);
    }
  }

  @Override
  public void flush() throws IOException {
    file.setLastModified(System.currentTimeMillis());
    setFileLength();
    
    LOG.fine("Index output writer flushed");
  }

  @Override
  public long getFilePointer() {
    return currentSegmentIndex < 0 ? 0 : segmentStart + segmentPosition;
  }
  
  private byte[] getSegmentData(FileSegment fileSegment, int minLength) {
    byte[] segmentData = fileSegment.getData();
    if (segmentData == null) {
      file.updateLength(file.getLength() + minLength);
      return new byte[minLength];
    }
    
    if (segmentData.length < minLength) {
      file.updateLength(file.getLength() + (minLength - segmentData.length));
      byte[] newSegmentData = new byte[minLength];
      System.arraycopy(segmentData, 0, newSegmentData, 0, segmentData.length);
      return newSegmentData;
    }
    
    return segmentData;
  }
  
  private FileSegment getCurrentSegment() {
  	if (currentSegment == null || currentSegment.getSegmentNo() != currentSegmentIndex)
  		currentSegment = file.getFileSegment(currentSegmentIndex);

  	return currentSegment;
  }
  
  private GaeFile file;
  private int currentSegmentIndex;
  private FileSegment currentSegment = null;
  private int segmentPosition;
  private long segmentStart;
}
