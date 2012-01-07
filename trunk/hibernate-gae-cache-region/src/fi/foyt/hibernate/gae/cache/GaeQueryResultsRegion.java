package fi.foyt.hibernate.gae.cache;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.QueryResultsRegion;

public class GaeQueryResultsRegion extends GaeAbstractCacheRegion implements QueryResultsRegion {

  public GaeQueryResultsRegion(String name, Properties properties) {
    super(name, properties);
  }

  public Object get(Object key) throws CacheException {
    return getSyncCache().get(key);
  }

  public void put(Object key, Object value) throws CacheException {
    getSyncCache().put(key, value);
  }

  public void evict(Object key) throws CacheException {
    getSyncCache().delete(key);
  }

  public void evictAll() throws CacheException {
    getSyncCache().clearAll();
  }

}
