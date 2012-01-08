package fi.foyt.hibernate.gae.search.persistence;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class JdoUtils {

  // TODO: Config
  
  private static final PersistenceManagerFactory PERSISTENCE_MANAGER_FACTORY = JDOHelper.getPersistenceManagerFactory("hibernate-search-persistence-manager-factory");

  public static PersistenceManagerFactory getPersistenceManagerFactory() {
    return PERSISTENCE_MANAGER_FACTORY;
  }
}