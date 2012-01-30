package fi.foyt.hibernate.gae.search;

import java.lang.reflect.Constructor;
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
import org.hibernate.search.backend.impl.lucene.LuceneBackendResources;
import org.hibernate.search.backend.impl.lucene.SingleTaskRunnable;
import org.hibernate.search.backend.spi.BackendQueueProcessor;
import org.hibernate.search.exception.impl.ErrorContextBuilder;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.spi.WorkerBuildContext;

import fi.foyt.hibernate.gae.search.persistence.dao.LockDAO;

public class GaeBackendQueueProcessor implements BackendQueueProcessor {
  
  private static final Logger LOG = Logger.getLogger(GaeBackendQueueProcessor.class.getName());

  public void initialize(Properties props, WorkerBuildContext context, DirectoryBasedIndexManager indexManager) {
    this.workspace = new GaeWorkspace(indexManager, null);
    this.resources = initializeBackendResources(context, indexManager, props, workspace);
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

  private void doWork(List<LuceneWork> workList, IndexingMonitor monitor, boolean streaming) {
    Lock modificationLock = resources.getParallelModificationLock();
    
    modificationLock.lock();
    try {
      applyUpdates(workList, monitor, streaming);
    } catch ( InterruptedException e ) {
      LOG.log(Level.SEVERE, "Interrupted while waiting for index activity", e);
    } catch ( Exception e ) {
      LOG.log(Level.SEVERE, "Failed to execute requested LuceneWork", e);
    }
    finally {
      modificationLock.unlock();
    }
    
    /**
    
    LuceneBackendQueueTask luceneBackendQueueProcessor = new LuceneBackendQueueTask(workList, resources, monitor, streaming);
    luceneBackendQueueProcessor.run();
    
    **/
  }
  
  private void applyUpdates(List<LuceneWork> workList, IndexingMonitor monitor, boolean streaming) throws InterruptedException, ExecutionException {
    AbstractWorkspaceImpl workspace = resources.getWorkspace();
    
    ErrorContextBuilder errorContextBuilder = new ErrorContextBuilder();
    errorContextBuilder.allWorkToBeDone( workList );
    
    IndexWriter indexWriter = workspace.getIndexWriter( errorContextBuilder );
    if ( indexWriter == null ) {
      LOG.log(Level.SEVERE, "Cannot open index writer");
      return;
    }
    boolean someFailureHappened = false;
    try {
      List<LuceneWork> failedWorkds = new ArrayList<LuceneWork>();
      
      int queueSize = workList.size();
      for ( int i = 0; i < queueSize; i++ ) {
        SingleTaskRunnable task = new SingleTaskRunnable( workList.get( i ), resources, indexWriter, monitor );
        try {
          task.run();
        } catch (Exception e) {
          someFailureHappened = true;
          errorContextBuilder.errorThatOccurred( e.getCause() );
          failedWorkds.add(workList.get( i ));
        }
      }
      
      if (someFailureHappened) {
        errorContextBuilder.addAllWorkThatFailed(failedWorkds);
        resources.getErrorHandler().handle( errorContextBuilder.createErrorContext() );
      } else {
        if ( !streaming ) {
          workspace.optimizerPhase();
        }
      }
      
    }
    finally {
      resources.getWorkspace().afterTransactionApplied( someFailureHappened );
    }
  }
  

  public Lock getExclusiveWriteLock() {
    return lock;
  }

  private final Lock lock = new LockImpl("backendLock");
  private AbstractWorkspaceImpl workspace;
  private LuceneBackendResources resources;

  private LuceneBackendResources initializeBackendResources(WorkerBuildContext context, DirectoryBasedIndexManager indexManager, Properties props,
      AbstractWorkspaceImpl workspace) {
    Constructor<LuceneBackendResources> constructor;
    try {
      constructor = LuceneBackendResources.class.getDeclaredConstructor(WorkerBuildContext.class, DirectoryBasedIndexManager.class, Properties.class,
          AbstractWorkspaceImpl.class);
      constructor.setAccessible(true);
      return constructor.newInstance(context, indexManager, props, workspace);
    } catch (Exception e) {
      return null;
    }
  }

  private class LockImpl implements Lock {

    public LockImpl(String name) {
      this.name = name;
    }

    public void lock() {
      LockDAO lockDAO = new LockDAO();
      lockDAO.create(name);
    }

    public void lockInterruptibly() throws InterruptedException {
      lock();
    }

    public Condition newCondition() {
      return null;
    }

    public boolean tryLock() {
      LockDAO lockDAO = new LockDAO();

      if (lockDAO.findByName(name) == null) {
        lockDAO.create(name);
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
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByName(name);
      if (lock != null)
        lockDAO.delete(lock);      
    }

    private String name;
  }
}
