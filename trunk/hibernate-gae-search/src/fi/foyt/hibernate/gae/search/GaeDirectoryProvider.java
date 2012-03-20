package fi.foyt.hibernate.gae.search;

import java.util.Properties;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Lock;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.spi.BuildContext;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.impl.DirectoryProviderHelper;

import fi.foyt.hibernate.gae.search.persistence.dao.DirectoryDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class GaeDirectoryProvider implements DirectoryProvider<GaeDirectory> {

  public void initialize(String directoryProviderName, Properties properties, BuildContext context) {
    this.indexName = directoryProviderName;

    DirectoryDAO directoryDAO = new DirectoryDAO();
    Directory dir = directoryDAO.findByName(this.indexName);
    if (dir == null) {
      dir = directoryDAO.create(indexName);
    }

    this.directory = new GaeDirectory(dir);
  }

  public void start(DirectoryBasedIndexManager indexManager) {
    DirectoryProviderHelper.initializeIndexIfNeeded(directory);
  }

  public GaeDirectory getDirectory() {
    return directory;
  }

  public void stop() {
    directory.close();
  }

  private GaeDirectory directory;
  private String indexName;
  
  static {
  	IndexWriterConfig.setDefaultWriteLockTimeout(Lock.LOCK_OBTAIN_WAIT_FOREVER);
  }
}
