package fi.foyt.hibernate.gae.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GAEConnectionProvider implements ConnectionProvider, Configurable, Stoppable {

  private static final long serialVersionUID = 3012740396851170892L;

  private static final String DRIVER_CLASS = "com.google.appengine.api.rdbms.AppEngineDriver";
  private static final String INSTANCE_PROPERTY = "hibernate.gae_connection.instance";
  private static final String DATABASE_PROPERTY = "hibernate.gae_connection.database";
  private static final String CONNECTION_TEST_SQL = "select 12345 from dual";
  private static final int CONNECTION_TEST_RESULT = 12345;
  private static final Logger LOG = LoggerFactory.getLogger(GAEConnectionProvider.class);

  public boolean isUnwrappableAs(@SuppressWarnings("rawtypes") Class unwrapType) {
    return ConnectionProvider.class.equals(unwrapType) || DriverManagerConnectionProviderImpl.class.isAssignableFrom(unwrapType);
  }

  @SuppressWarnings({ "unchecked" })
  public <T> T unwrap(Class<T> unwrapType) {
    if (ConnectionProvider.class.equals(unwrapType) || DriverManagerConnectionProviderImpl.class.isAssignableFrom(unwrapType)) {
      return (T) this;
    } else {
      throw new UnknownUnwrapTypeException(unwrapType);
    }
  }

  public void configure(@SuppressWarnings("rawtypes") Map configurationValues) {
    LOG.info("Using AppEngine connection provider");
    try {
      // trying via forName() first to be as close to DriverManager's semantics
      Class.forName(DRIVER_CLASS);
    } catch (ClassNotFoundException cnfe) {
      try {
        ReflectHelper.classForName(DRIVER_CLASS);
      } catch (ClassNotFoundException e) {
        throw new HibernateException("Specified JDBC Driver " + DRIVER_CLASS + " class not found", e);
      }
    }

    poolSize = ConfigurationHelper.getInt(AvailableSettings.POOL_SIZE, configurationValues, 20); // default pool size 20
    LOG.info("AppEngine connection provider pool size: " + poolSize);

    autocommit = ConfigurationHelper.getBoolean(AvailableSettings.AUTOCOMMIT, configurationValues);
    LOG.info("AppEngine connection provider autocommit: " + autocommit);

    isolation = ConfigurationHelper.getInteger(AvailableSettings.ISOLATION, configurationValues);
    LOG.info("AppEngine connection provider isolation: " + isolation);

    url = getJdbcUrl(ConfigurationHelper.getString(INSTANCE_PROPERTY, configurationValues),
        ConfigurationHelper.getString(DATABASE_PROPERTY, configurationValues));

    connectionProps = ConnectionProviderInitiator.getConnectionProperties(configurationValues);
  }

  public void stop() {
    LOG.info("AppEngine connection provider cleaning up connection pool");

    for (Connection connection : pool) {
      try {
        connection.close();
      } catch (SQLException sqle) {
        LOG.error("Unable to closed pooled connection: " + sqle.getMessage());
      }
    }
    pool.clear();
    stopped = true;
  }

  public Connection getConnection() throws SQLException {
    // essentially, if we have available connections in the pool, use one...
    synchronized (pool) {
      while (!pool.isEmpty()) {
        int last = pool.size() - 1;
        Connection pooled = pool.remove(last);
        if (isolation != null) {
          pooled.setTransactionIsolation(isolation.intValue());
        }
        if (pooled.getAutoCommit() != autocommit) {
          pooled.setAutoCommit(autocommit);
        }

        if (checkConnectionHealth(pooled)) {
          return pooled;
        } else {
          LOG.warn("Pooled connection was invalid and thus dropped from pool");
        }

      }
    }

    // otherwise we open a new connection...

    LOG.debug("Opening new JDBC connection");
    Connection conn = DriverManager.getConnection(url, connectionProps);
    if (isolation != null) {
      conn.setTransactionIsolation(isolation.intValue());
    }
    if (conn.getAutoCommit() != autocommit) {
      conn.setAutoCommit(autocommit);
    }

    return conn;
  }

  public void closeConnection(Connection conn) throws SQLException {
    // add to the pool if the max size is not yet reached.
    synchronized (pool) {
      int currentSize = pool.size();
      if (currentSize < poolSize) {
        pool.add(conn);
        return;
      }
    }

    LOG.debug("Closing JDBC connection");
    conn.close();
  }

  @Override
  protected void finalize() throws Throwable {
    if (!stopped) {
      stop();
    }
    super.finalize();
  }

  public boolean supportsAggressiveRelease() {
    return false;
  }

  private boolean checkConnectionHealth(Connection connection) {
    try {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(CONNECTION_TEST_SQL);
      if (resultSet.next()) {
        return resultSet.getInt(1) == CONNECTION_TEST_RESULT;
      }
    } catch (Exception e) {
      return false;
    }

    return false;
  }

  private String getJdbcUrl(String instance, String database) {
    if (instance == null) {
      String msg = "Cloud SQL instance was not specified by property " + INSTANCE_PROPERTY;
      LOG.error(msg);
      throw new HibernateException(msg);
    }

    if (database == null) {
      String msg = "Cloud SQL database was not specified by property " + DATABASE_PROPERTY;
      LOG.error(msg);
      throw new HibernateException(msg);
    }

    return "jdbc:google:rdbms://" + instance + "/" + database;
  }

  private String url;
  private Properties connectionProps;
  private Integer isolation;
  private int poolSize;
  private boolean autocommit;
  private final ArrayList<Connection> pool = new ArrayList<Connection>();
  private boolean stopped;
}