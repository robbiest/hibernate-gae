package fi.foyt.hibernate.gae.cache;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;

public class GaeCollectionRegion extends GaeAbstractTransactionalDataRegion implements CollectionRegion {

  public GaeCollectionRegion(String name, Properties properties, CacheDataDescription metadata) {
    super(name, properties, metadata);
  }

  public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
    return new GaeCollectionRegionAccessStrategy(this);
  }

}
