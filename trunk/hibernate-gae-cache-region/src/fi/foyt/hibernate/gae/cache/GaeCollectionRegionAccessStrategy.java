package fi.foyt.hibernate.gae.cache;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

import com.google.appengine.api.memcache.MemcacheService;

public class GaeCollectionRegionAccessStrategy implements CollectionRegionAccessStrategy {

  public GaeCollectionRegionAccessStrategy(GaeCollectionRegion collectionRegion) {
    this.collectionRegion = collectionRegion;
  }
  
  public CollectionRegion getRegion() {
    return collectionRegion;
  }

  public Object get(Object key, long txTimestamp) throws CacheException {
    // TODO: Timestamp
    return getMemcacheService().get(key);
  }

  public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
    return putFromLoad(key, value, txTimestamp, version, false);
  }

  public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
    // TODO txTimestamp
    // TODO version
    // TODO minimalPutOverride

    getMemcacheService().put(key, value);
    return true;
  }

  public SoftLock lockItem(Object key, Object version) throws CacheException {
    return null;
  }

  public SoftLock lockRegion() throws CacheException {
    return null;
  }

  public void unlockItem(Object key, SoftLock lock) throws CacheException {
  }

  public void unlockRegion(SoftLock lock) throws CacheException {
  }

  public void remove(Object key) throws CacheException {
    getMemcacheService().delete(key);
  }

  public void removeAll() throws CacheException {
    getMemcacheService().clearAll();
  }

  public void evict(Object key) throws CacheException {
    getMemcacheService().delete(key);
  }

  public void evictAll() throws CacheException {
    getMemcacheService().clearAll();
  }

  private MemcacheService getMemcacheService() {
    return ((GaeEntityRegion) getRegion()).getSyncCache();
  }
  
  private GaeCollectionRegion collectionRegion;
}
