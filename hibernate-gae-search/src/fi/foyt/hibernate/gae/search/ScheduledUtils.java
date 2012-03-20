package fi.foyt.hibernate.gae.search;

import java.util.List;
import java.util.Properties;

import org.hibernate.search.SearchFactory;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.spi.BackendQueueProcessor;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.exception.ErrorHandler;
import org.hibernate.search.impl.MutableSearchFactory;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.indexes.impl.IndexManagerHolder;
import org.hibernate.search.indexes.serialization.spi.LuceneWorkSerializer;
import org.hibernate.search.indexes.spi.IndexManager;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.spi.InstanceInitializer;
import org.hibernate.search.spi.ServiceProvider;
import org.hibernate.search.spi.WorkerBuildContext;

import fi.foyt.hibernate.gae.search.persistence.dao.DirectoryDAO;
import fi.foyt.hibernate.gae.search.persistence.dao.WorkListDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.WorkList;

public class ScheduledUtils {

	public static void performWorkList(FullTextEntityManager fullTextEntityManager, WorkList workListEntity) {
  	WorkListDAO workListDAO = new WorkListDAO();
  	DirectoryDAO directoryDAO = new DirectoryDAO();

  	SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
  	
  	Directory directory = directoryDAO.findByKey(workListEntity.getKey().getParent());
  	
  	IndexManager indexManager = getIndexManager(searchFactory, directory.getName());
		LuceneWorkSerializer serializer = indexManager.getSerializer();
		BackendQueueProcessor queueProcessor = createInstanceBackendQueueProcessor(searchFactory, indexManager);
  	
		List<LuceneWork> luceneWorks = serializer.toLuceneWorks(workListEntity.getData());
		
		queueProcessor.applyWork(luceneWorks, null);
		
		workListDAO.delete(workListEntity);		
	}
	
	private static IndexManager getIndexManager(SearchFactory searchFactory, String indexName) {
	  MutableSearchFactory mutableSearchFactory = (MutableSearchFactory) searchFactory;
		IndexManagerHolder indexManagerHolder = mutableSearchFactory.getAllIndexesManager();
		return indexManagerHolder.getIndexManager(indexName);
	}

	private static BackendQueueProcessor createInstanceBackendQueueProcessor(SearchFactory searchFactory, IndexManager indexManager) {
	  MutableSearchFactory mutableSearchFactory = (MutableSearchFactory) searchFactory;
		WorkerBuildContext context = new WorkerBuildContextImpl(mutableSearchFactory.getErrorHandler());
		
		Properties properties = mutableSearchFactory.getConfigurationProperties();
		
		GaeInstantBackendQueueProcessor queueProcessor = new GaeInstantBackendQueueProcessor();
		queueProcessor.initialize(properties, context, (DirectoryBasedIndexManager) indexManager);
		
		return queueProcessor;
  }
	
	private static class WorkerBuildContextImpl implements WorkerBuildContext {

		public WorkerBuildContextImpl(ErrorHandler errorHandler) {
	    this.errorHandler = errorHandler;
    }
		
		public SearchFactoryImplementor getUninitializedSearchFactory() {
	    return null;
    }

		public String getIndexingStrategy() {
	    return null;
    }

		public <T> T requestService(Class<? extends ServiceProvider<T>> provider) {
	    return null;
    }

		public void releaseService(Class<? extends ServiceProvider<?>> provider) {
    }

		public IndexManagerHolder getAllIndexesManager() {
	    return null;
    }

    public ErrorHandler getErrorHandler() {
	    return errorHandler;
    }

    public boolean isTransactionManagerExpected() {
	    return false;
    }

    public InstanceInitializer getInstanceInitializer() {
	    return null;
    }
		
		public boolean isIndexMetadataComplete() {
	    return false;
    }

		private ErrorHandler errorHandler;

	}
}
