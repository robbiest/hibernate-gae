package fi.foyt.hibernate.gae.search;

import java.util.Properties;

import org.hibernate.search.backend.impl.lucene.AbstractWorkspaceImpl;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.spi.WorkerBuildContext;

public class GaeScheduledWorkspace extends AbstractWorkspaceImpl {
  
  public GaeScheduledWorkspace(DirectoryBasedIndexManager indexManager, WorkerBuildContext context, Properties cfg) {
    super(indexManager, context, cfg);
  }

	public void flush() {
  }

	@Override
  public void afterTransactionApplied(boolean someFailureHappened, boolean streaming) {
  }

}
