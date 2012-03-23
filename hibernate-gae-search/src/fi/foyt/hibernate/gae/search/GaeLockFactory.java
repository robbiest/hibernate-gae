package fi.foyt.hibernate.gae.search;

import java.io.IOException;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockReleaseFailedException;

import fi.foyt.hibernate.gae.search.persistence.dao.LockDAO;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;

public class GaeLockFactory extends LockFactory {

	public GaeLockFactory(Directory directory) {
	  this.directory = directory;
  }
	
  @Override
  public Lock makeLock(String lockName) {
    return new GaeLock(directory, lockName);
  }

  @Override
  public void clearLock(String lockName) throws IOException {
    GaeLock gaeLock = new GaeLock(directory, lockName);
    gaeLock.release();
  }
  
  private Directory directory;

  class GaeLock extends Lock {
    
		public GaeLock(Directory directory, String lockName) {
      this.lockName = lockName;
      this.directory = directory;
    }

    @Override
    public boolean obtain() throws IOException {
      LockDAO lockDAO = new LockDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByDirectoryAndName(directory, lockName);
      
      if (lock != null) {
        return false;
      }
      
      lockDAO.create(directory, lockName);  

      return true;
    }

    @Override
    public void release() throws LockReleaseFailedException {
      LockDAO lockDAO = new LockDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByDirectoryAndName(directory, lockName);
      if (lock != null)
        lockDAO.delete(lock);
    }

    @Override
    public boolean isLocked() {
      LockDAO lockDAO = new LockDAO();
      fi.foyt.hibernate.gae.search.persistence.domainmodel.Lock lock = lockDAO.findByDirectoryAndName(directory, lockName);
      return lock != null;
    }
    
    private Directory directory;
    private String lockName;
  }
}
