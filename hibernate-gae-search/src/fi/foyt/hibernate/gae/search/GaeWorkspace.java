package fi.foyt.hibernate.gae.search;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexWriter;
import org.hibernate.search.backend.impl.lucene.AbstractWorkspaceImpl;
import org.hibernate.search.exception.ErrorHandler;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;

public class GaeWorkspace extends AbstractWorkspaceImpl {
  
  private static final Logger LOG = Logger.getLogger(GaeWorkspace.class.getName());

  public GaeWorkspace(DirectoryBasedIndexManager indexManager, ErrorHandler errorHandler) {
    super(indexManager, errorHandler);
    this.indexManager = indexManager;
  }

  @Override
  public void afterTransactionApplied(boolean someFailureHappened) {
    if (someFailureHappened) {
      forceLockRelease();
    } else {
      commitIndexWriter();
    }
  }

  public void forceLockRelease() {
    IndexWriter indexWriter = getIndexWriter();
    try {
      try {
        if (indexWriter != null) {
          indexWriter.close();
        }
      } finally {
        indexWriter = null;
        IndexWriter.unlock(this.indexManager.getDirectoryProvider().getDirectory());
      }
    } catch (IOException ioe) {
      LOG.log(Level.SEVERE, "Could not force release index writer lock", ioe);
    }
  }

  private void commitIndexWriter() {
    IndexWriter indexWriter = getIndexWriter();

    if (indexWriter != null) {
      try {
        indexWriter.commit();
      } catch (IOException ioe) {
        LOG.log(Level.SEVERE, "Could not commit index writer", ioe);
      }
    }
  }

  private DirectoryBasedIndexManager indexManager;
}
