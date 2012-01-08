package fi.foyt.hibernate.gae.search.persistence.dao;

import javax.jdo.PersistenceManager;

import fi.foyt.hibernate.gae.search.persistence.JdoUtils;

public class GenericJdoDAO {

  protected PersistenceManager getPersistenceManager() {
    return JdoUtils.getPersistenceManagerFactory().getPersistenceManager();
  }

}
