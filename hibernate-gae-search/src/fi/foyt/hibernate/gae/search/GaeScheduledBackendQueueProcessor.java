package fi.foyt.hibernate.gae.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.hibernate.search.backend.IndexingMonitor;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.impl.lucene.AbstractWorkspaceImpl;
import org.hibernate.search.backend.spi.BackendQueueProcessor;
import org.hibernate.search.exception.ErrorHandler;
import org.hibernate.search.exception.impl.ErrorContextBuilder;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.indexes.serialization.spi.LuceneWorkSerializer;
import org.hibernate.search.spi.WorkerBuildContext;

import fi.foyt.hibernate.gae.search.persistence.dao.DirectoryDAO;
import fi.foyt.hibernate.gae.search.persistence.dao.WorkListDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class GaeScheduledBackendQueueProcessor implements BackendQueueProcessor {

  private static final Logger LOG = Logger.getLogger(GaeScheduledBackendQueueProcessor.class.getName());

	public void initialize(Properties props, WorkerBuildContext context, DirectoryBasedIndexManager indexManager) {
		this.workspace = new GaeScheduledWorkspace(indexManager, context, props);
		this.errorHandler = context.getErrorHandler();
		this.indexManager = indexManager;
	}

	public void close() {
	}

	public void applyWork(List<LuceneWork> workList, IndexingMonitor monitor) {
		doWork(workList, monitor, false);
	}

	public void applyStreamWork(LuceneWork singleOperation, IndexingMonitor monitor) {
		List<LuceneWork> workList = new ArrayList<LuceneWork>();
		workList.add(singleOperation);
		doWork(workList, monitor, true);
	}

	public void indexMappingChanged() {

  }

	private void doWork(List<LuceneWork> workList, IndexingMonitor monitor, boolean streaming) {
		applyUpdates(workList, monitor, streaming);
	}

	private void applyUpdates(List<LuceneWork> workList, IndexingMonitor monitor, boolean streaming) {
		ErrorContextBuilder errorContextBuilder = new ErrorContextBuilder();
		errorContextBuilder.allWorkToBeDone(workList);
		
		LuceneWorkSerializer serializer = indexManager.getSerializer();
		
		DirectoryDAO directoryDAO = new DirectoryDAO();
		WorkListDAO workListDAO = new WorkListDAO();
		
		Directory directory = directoryDAO.findByName(indexManager.getIndexName());

		boolean someFailureHappened = false;
		try {
			
			try {
				workListDAO.create(directory, serializer.toSerializedModel(workList));
				LOG.info("Scheduled " + workList.size() + " works in " + directory.getName() + " directory to be performed in future");
			} catch (Exception e) {
				someFailureHappened = true;
				errorContextBuilder.errorThatOccurred(e.getCause());
			}
			
			if (someFailureHappened) {
				errorContextBuilder.addAllWorkThatFailed(workList);
				getErrorHandler().handle(errorContextBuilder.createErrorContext());
			} else {
				if (!streaming) {
					workspace.optimizerPhase();
				}
			}

		} finally {
			workspace.afterTransactionApplied(someFailureHappened, streaming);
		}
	}

	private ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public Lock getExclusiveWriteLock() {
		return reentrantLock;
	}

	private AbstractWorkspaceImpl workspace;
	private ErrorHandler errorHandler;
	private final ReentrantLock reentrantLock = new ReentrantLock();
	private DirectoryBasedIndexManager indexManager;
}
