package fi.foyt.hibernate.gae.search;

import java.io.IOException;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockReleaseFailedException;

import fi.foyt.hibernate.gae.search.persistence.dao.LockJdoDAO;

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
      LockJdoDAO lockJdoDAO = new LockJdoDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockJdoDAO.findByName(lockName);
      
      if (lock != null) {
        return false;
      }
      
      lockJdoDAO.create(lockName);  

      return true;
    }

    @Override
    public void release() throws LockReleaseFailedException {
      LockJdoDAO lockJdoDAO = new LockJdoDAO();
      lockJdoDAO.deleteByName(lockName);
    }

    @Override
    public boolean isLocked() {
      LockJdoDAO lockJdoDAO = new LockJdoDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockJdoDAO.findByName(lockName);
      return lock != null;
    }
    
    private String lockName;
  }
}
