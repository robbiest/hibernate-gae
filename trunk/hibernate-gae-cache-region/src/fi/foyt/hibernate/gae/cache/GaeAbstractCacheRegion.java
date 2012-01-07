package fi.foyt.hibernate.gae.cache;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.cache.spi.Region;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public abstract class GaeAbstractCacheRegion implements Region {

  public GaeAbstractCacheRegion(String name, Properties properties) {
    this.name = name;
    this.properties = properties;
  }

  public String getName() {
    return name;
  }

  public void destroy() throws org.hibernate.cache.CacheException {
  }

  public boolean contains(Object key) {
    return syncCache.contains(key);
  }

  public long getSizeInMemory() {
    return syncCache.getStatistics().getTotalItemBytes();
  }

  public long getElementCountInMemory() {
    return syncCache.getStatistics().getItemCount();
  }

  public long getElementCountOnDisk() {
    return 0;
  }

  @SuppressWarnings("rawtypes")
  public Map toMap() {
    return null;
  }

  public long nextTimestamp() { 
    return Timestamper.next();
  }

  public int getTimeout() {
    return (int) DateUtils.MILLIS_PER_MINUTE;
  }
  
  protected Properties getProperties() {
    return properties;
  }
  
  public MemcacheService getSyncCache() {
    return syncCache;
  }

  private String name;
  private Properties properties;
  private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
}