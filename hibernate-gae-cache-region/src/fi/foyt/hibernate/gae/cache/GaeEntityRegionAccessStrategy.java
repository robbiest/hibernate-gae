package fi.foyt.hibernate.gae.cache;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

import com.google.appengine.api.memcache.MemcacheService;

public class GaeEntityRegionAccessStrategy implements EntityRegionAccessStrategy {

  public GaeEntityRegionAccessStrategy(EntityRegion entityRegion) {
    this.entityRegion = entityRegion;
  }

  public EntityRegion getRegion() {
    return this.entityRegion;
  }

  public Object get(Object key, long txTimestamp) throws CacheException {
    // TODO: Timestamp
    return getMemcacheService().get(key);
  }

  public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
    // TODO: Timestamp
    // TODO: Version
    getMemcacheService().put(key, value);
    return true;
  }

  public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
    // TODO: Timestamp
    // TODO: Version
    // TODO: minimalPutOverride
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

  public boolean insert(Object key, Object value, Object version) throws CacheException {
    // TODO: Timestamp
    // TODO: Version
    getMemcacheService().put(key, value);
    return true;
  }

  public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
    // TODO Implement?
    return false;
  }

  public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
    // TODO Implement
    return false;
  }

  public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
    // TODO Implement
    return false;
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
  
  private EntityRegion entityRegion;
}
