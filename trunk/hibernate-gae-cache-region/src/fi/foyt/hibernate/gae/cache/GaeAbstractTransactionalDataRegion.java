package fi.foyt.hibernate.gae.cache;

import java.util.Properties;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;

public class GaeAbstractTransactionalDataRegion extends GaeAbstractCacheRegion implements TransactionalDataRegion {

  public GaeAbstractTransactionalDataRegion(String name, Properties properties, CacheDataDescription metadata) {
    super(name, properties);

    this.metadata = metadata;
  }
  
  public boolean isTransactionAware() {
    return false;
  }

  public CacheDataDescription getCacheDataDescription() {
    return metadata;
  }

  private CacheDataDescription metadata;
}
