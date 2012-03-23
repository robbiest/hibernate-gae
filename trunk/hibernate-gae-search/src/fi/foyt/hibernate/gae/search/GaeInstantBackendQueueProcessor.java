package fi.foyt.hibernate.gae.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexWriter;
import org.hibernate.search.backend.IndexingMonitor;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.impl.lucene.AbstractWorkspaceImpl;
import org.hibernate.search.backend.impl.lucene.works.LuceneWorkVisitor;
import org.hibernate.search.backend.spi.BackendQueueProcessor;
import org.hibernate.search.exception.ErrorHandler;
import org.hibernate.search.exception.impl.ErrorContextBuilder;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.spi.WorkerBuildContext;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;

import fi.foyt.hibernate.gae.search.persistence.dao.DirectoryDAO;
import fi.foyt.hibernate.gae.search.persistence.dao.LockDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class GaeInstantBackendQueueProcessor implements BackendQueueProcessor {

	private static final Logger LOG = Logger.getLogger(GaeInstantBackendQueueProcessor.class.getName());

	public void initialize(Properties props, WorkerBuildContext context, DirectoryBasedIndexManager indexManager) {
		this.workspace = new GaeInstantWorkspace(indexManager, context, props);
		this.visitor = new LuceneWorkVisitor(workspace);
		this.errorHandler = context.getErrorHandler();

		DirectoryDAO directoryDAO = new DirectoryDAO();
		Directory directory = directoryDAO.findByName(indexManager.getIndexName());
		readLock = new LockImpl(directory, "backendLock.parallelModificationLock");
		writeLock = new LockImpl(directory, "backendLock.exclusiveWriteLock");
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
		Lock modificationLock = getParallelModificationLock();

		modificationLock.lock();
		try {
			applyUpdates(workList, monitor, streaming);
		} catch (InterruptedException e) {
			LOG.log(Level.SEVERE, "Interrupted while waiting for index activity", e);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to execute requested LuceneWork", e);
		} finally {
			modificationLock.unlock();
		}
	}

	private void applyUpdates(List<LuceneWork> workList, IndexingMonitor monitor, boolean streaming) throws InterruptedException, ExecutionException {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

		ErrorContextBuilder errorContextBuilder = new ErrorContextBuilder();
		errorContextBuilder.allWorkToBeDone(workList);

		IndexWriter indexWriter = workspace.getIndexWriter(errorContextBuilder);
		if (indexWriter == null) {
			LOG.log(Level.SEVERE, "Cannot open index writer");
			return;
		}
		boolean someFailureHappened = false;
		try {
			List<LuceneWork> failedWorkds = new ArrayList<LuceneWork>();

			int queueSize = workList.size();
			LOG.info("Performing " + queueSize + " lucene works");

			for (int i = 0; i < queueSize; i++) {
				LuceneWork work = workList.get(i);
				try {
					long time = 0;
					if (LOG.isLoggable(Level.INFO)) {
						String entity = work.getEntityClass().toString();
						LOG.info("Performing lucene work for " + entity);
						time = System.currentTimeMillis();
					}

					Transaction transaction = datastoreService.beginTransaction();
					try {
						work.getWorkDelegate(getVisitor()).performWork(work, indexWriter, monitor);
						transaction.commit();
					} finally {
						if (transaction.isActive())
							transaction.rollback();
					}

					if (LOG.isLoggable(Level.INFO)) {
						String entity = work.getEntityClass().toString();
						LOG.info("Performed lucene work for " + entity + " in " + (System.currentTimeMillis() - time) + " ms");
					}
				} catch (Exception e) {
					someFailureHappened = true;
					errorContextBuilder.errorThatOccurred(e.getCause());
					failedWorkds.add(work);
				}
			}

			if (someFailureHappened) {
				errorContextBuilder.addAllWorkThatFailed(failedWorkds);
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

	private LuceneWorkVisitor getVisitor() {
		return visitor;
	}

	private ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	private Lock getParallelModificationLock() {
		return readLock;
	}

	public Lock getExclusiveWriteLock() {
		return writeLock;
	}

	private Lock readLock;
	private Lock writeLock;

	private AbstractWorkspaceImpl workspace;
	private LuceneWorkVisitor visitor;
	private ErrorHandler errorHandler;

	private class LockImpl implements Lock {
		public LockImpl(Directory directory, String name) {
			this.directory = directory;
			this.name = name;
		}

		public void lock() {
			LockDAO lockDAO = new LockDAO();
			lockDAO.create(directory, name);
		}

		public void lockInterruptibly() throws InterruptedException {
			lock();
		}

		public Condition newCondition() {
			return null;
		}

		public boolean tryLock() {
			LockDAO lockDAO = new LockDAO();
			if (lockDAO.findByDirectoryAndName(directory, name) == null) {
				lockDAO.create(directory, name);
				return true;
			}
			return false;
		}

		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			long millis = unit.toMillis(time);
			while (millis >= 0) {
				if (tryLock() == true) {
					return true;
				}
				Thread.sleep(10);
			}
			return false;
		}

		public void unlock() {
			LockDAO lockDAO = new LockDAO();
			fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByDirectoryAndName(directory, name);
			if (lock != null)
				lockDAO.delete(lock);
		}

		private String name;
		private Directory directory;
	}
}
