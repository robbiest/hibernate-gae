package fi.foyt.hibernate.gae.utils;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.appengine.api.rdbms.AppEngineDriver;

public class HibernateUtils {
  
  public static EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }
  
  private static final EntityManagerFactory entityManagerFactory;
  
  static {
    try {
      DriverManager.registerDriver(new AppEngineDriver());
    } catch (SQLException e) {
      throw new ExceptionInInitializerError(e);
    }
    
    entityManagerFactory = Persistence.createEntityManagerFactory("myPU");
  }
}