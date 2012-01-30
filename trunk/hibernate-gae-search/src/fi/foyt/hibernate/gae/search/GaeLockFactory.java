package fi.foyt.hibernate.gae.search;

import java.io.IOException;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockReleaseFailedException;

import fi.foyt.hibernate.gae.search.persistence.dao.LockDAO;

public class GaeLockFactory extends LockFactory {

  @Override
  public Lock makeLock(String lockName) {
    return new GaeLock(lockName);
  }

  @Override
  public void clearLock(String lockName) throws IOException {
    GaeLock gaeLock = new GaeLock(lockName);
    gaeLock.release();
  }

  class GaeLock extends Lock {
    
    public GaeLock(String lockName) {
      this.lockName = lockName;
    }

    @Override
    public boolean obtain() throws IOException {
      LockDAO lockDAO = new LockDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByName(lockName);
      
      if (lock != null) {
        return false;
      }
      
      lockDAO.create(lockName);  

      return true;
    }

    @Override
    public void release() throws LockReleaseFailedException {
      LockDAO lockDAO = new LockDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByName(lockName);
      if (lock != null)
        lockDAO.delete(lock);
    }

    @Override
    public boolean isLocked() {
      LockDAO lockDAO = new LockDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByName(lockName);
      return lock != null;
    }
    
    private String lockName;
  }
}
