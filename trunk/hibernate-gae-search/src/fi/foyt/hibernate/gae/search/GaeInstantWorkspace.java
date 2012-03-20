package fi.foyt.hibernate.gae.search;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexWriter;
import org.hibernate.search.backend.impl.lucene.AbstractWorkspaceImpl;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.spi.WorkerBuildContext;

public class GaeInstantWorkspace extends AbstractWorkspaceImpl {
  
  private static final Logger LOG = Logger.getLogger(GaeInstantWorkspace.class.getName());

  public GaeInstantWorkspace(DirectoryBasedIndexManager indexManager, WorkerBuildContext context, Properties cfg) {
    super(indexManager, context, cfg);
    this.indexManager = indexManager;
  }

  @Override
  public void afterTransactionApplied(boolean someFailureHappened, boolean streaming) {
    if (someFailureHappened) {
      forceLockRelease();
    } else {
    	if (!streaming)
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

	public void flush() {
		commitIndexWriter();
  }

  private void commitIndexWriter() {
    IndexWriter indexWriter = getIndexWriter();

    if (indexWriter != null) {
      try {
        indexWriter.commit();
        IndexWriter.unlock(this.indexManager.getDirectoryProvider().getDirectory());
      } catch (IOException ioe) {
        LOG.log(Level.SEVERE, "Could not commit index writer", ioe);
      }
    }
  }

  private DirectoryBasedIndexManager indexManager;
}
