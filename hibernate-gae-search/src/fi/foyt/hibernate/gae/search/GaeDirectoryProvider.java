package fi.foyt.hibernate.gae.search;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.search.SearchException;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.spi.BuildContext;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.impl.DirectoryProviderHelper;

import fi.foyt.hibernate.gae.search.persistence.dao.DirectoryJdoDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class GaeDirectoryProvider implements DirectoryProvider<GaeDirectory> {

  public void initialize(String directoryProviderName, Properties properties, BuildContext context) {
    this.indexName = directoryProviderName;
    this.properties = properties;

    DirectoryJdoDAO directoryJdoDAO = new DirectoryJdoDAO();
    Directory dir = directoryJdoDAO.findByName(this.indexName);
    if (dir == null) {
      dir = directoryJdoDAO.create(indexName);
    }

    this.directory = new GaeDirectory(dir.getId());
  }

  public void start(DirectoryBasedIndexManager indexManager) {
    try {
      directory.setLockFactory(DirectoryProviderHelper.createLockFactory(null, properties));
      properties = null;
      DirectoryProviderHelper.initializeIndexIfNeeded(directory);
    } catch (IOException e) {
      throw new SearchException("Unable to initialize index: " + indexName, e);
    }
  }

  public GaeDirectory getDirectory() {
    return directory;
  }

  public void stop() {
    directory.close();
  }

  private GaeDirectory directory;
  private String indexName;
  private Properties properties;
}
