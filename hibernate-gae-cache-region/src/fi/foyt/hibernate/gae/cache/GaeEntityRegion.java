package fi.foyt.hibernate.gae.cache;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;

public class GaeEntityRegion extends GaeAbstractTransactionalDataRegion implements EntityRegion {

  public GaeEntityRegion(String name, Properties properties, CacheDataDescription metadata) {
    super(name, properties, metadata);
  }

  public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
    return new GaeEntityRegionAccessStrategy(this);
  }

  @Override
  public boolean isTransactionAware() {
    return false;
  }    
}
