package fi.foyt.hibernate.gae.search;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.FileSegment;

public class GaeIndexInput extends IndexInput implements Cloneable {

  private static final Logger LOG = Logger.getLogger(GaeIndexInput.class.getName());

  public GaeIndexInput(GaeFile f) throws IOException {
  	super(GaeIndexInput.class.toString());
    file = f;
    currentSegmentIndex = 0;
  }

  @Override
  public void close() {
    LOG.fine("Index input reader closed");
  }

  @Override
  public long length() {
    return file.getLength();
  }

  @Override
  public byte readByte() throws IOException {
    if (segmentPosition >= GaeFile.SEGMENT_SIZE) {
      currentSegmentIndex++;
      switchCurrentSegment(true);
    }
    return getCurrentSegment().getData()[segmentPosition++];
  }

  @Override
  public void readBytes(byte[] b, int offset, int len) throws IOException {
  	long time = System.currentTimeMillis();
  	int originalLen = len;
  	
  	while (len > 0) {
      if (segmentPosition >= GaeFile.SEGMENT_SIZE) {
        currentSegmentIndex++;
        switchCurrentSegment(true);
      }

      byte[] segmentData = getCurrentSegment().getData();

      int remainInBuffer = segmentData.length - segmentPosition;
      int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
      System.arraycopy(segmentData, segmentPosition, b, offset, bytesToCopy);
      offset += bytesToCopy;
      len -= bytesToCopy;
      segmentPosition += bytesToCopy;
    }
  	
  	LOG.fine("Read " + originalLen + " bytes in " + (System.currentTimeMillis() - time) + "ms");
  }

  private final void switchCurrentSegment(boolean enforceEOF) throws IOException {
    segmentStart = (long) GaeFile.SEGMENT_SIZE * (long) currentSegmentIndex;
    if (currentSegmentIndex >= file.getFileSegmentsCount()) {
      if (enforceEOF) {
        throw new IOException("Read past EOF");
      } else {
        currentSegmentIndex--;
        segmentPosition = GaeFile.SEGMENT_SIZE;
      }
    } else {
      segmentPosition = 0;
    }
  }

  @Override
  public void copyBytes(IndexOutput out, long numBytes) throws IOException {
    assert numBytes >= 0 : "numBytes=" + numBytes;

    long left = numBytes;
    while (left > 0) {
      if (segmentPosition == GaeFile.SEGMENT_SIZE) {
        ++currentSegmentIndex;
        switchCurrentSegment(true);
      }

      byte[] segmentData = getCurrentSegment().getData();

      final int bytesInBuffer = segmentData.length - segmentPosition;
      final int toCopy = (int) (bytesInBuffer < left ? bytesInBuffer : left);

      out.writeBytes(segmentData, segmentPosition, toCopy);
      segmentPosition += toCopy;
      left -= toCopy;
    }

    assert left == 0 : "Insufficient bytes to copy: numBytes=" + numBytes + " copied=" + (numBytes - left);
  }

  @Override
  public long getFilePointer() {
    return currentSegmentIndex < 0 ? 0 : segmentStart + segmentPosition;
  }

  @Override
  public void seek(long pos) throws IOException {
    FileSegment fileSegment = getCurrentSegment();

    if (fileSegment == null || pos < segmentStart || pos >= segmentStart + GaeFile.SEGMENT_SIZE) {
      currentSegmentIndex = (int) (pos / GaeFile.SEGMENT_SIZE);
      switchCurrentSegment(false);
    }

    segmentPosition = (int) (pos % GaeFile.SEGMENT_SIZE);
  }

  private FileSegment getCurrentSegment() {
    return file.getFileSegment(currentSegmentIndex);
  }

  private GaeFile file;
  private int currentSegmentIndex;
  private int segmentPosition;
  private long segmentStart;
}