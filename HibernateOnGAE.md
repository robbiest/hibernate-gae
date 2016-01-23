# Example #

There is a example project in version control that you can use to test Hibernate in App Engine. You can found that project from http://code.google.com/p/hibernate-gae/source/browse/#svn%2Ftrunk%2Fhibernate-gae-test

# Installation #

_Btw. I'm assuming you are using Eclipse with Google App Engine plugin. If you are not, you probably need to figure some parts out yourself_

Datanucleus conflicts with Hibernate so you need to remove it from the program. You can do this by unchecking "Use Datanucleus JDO/JPA to access the datastore" from Project properties > Google > App Engine.

In the same view you should enable Google Cloud SQL and configure it.

Then its time to add Hibernate jars into the project. Easiest way to do this is to add org.hibernate:hibernate-core-4.0.0.Final and org.hibernate:hibernate-entitymanager-4.0.0.Final Maven artifacts to your project. You can also copy the jars to war/WEB-INF/lib folder if you wish.

Its optional but you may also want to add Maven artifact fi.foyt.hibernate.gae:hibernate-gae-connection-provider-0.0.1-SNAPSHOT or download hibernate-gae-connection-provider-XXX.jar from Downloads so you would get hibernate-gae version of connection provider to your application.

After all libraries have been set up you need to create persistence.xml

# Persistence.xml #

App Engine is not a Java EE application container so you cannot use JTA. Because of that we need to set transaction-type to RESOURCE\_LOCAL and hibernate.current\_session\_context\_class to thread. If you use GAEConnectionProvider you also need to set hibernate.connection.provider\_class, hibernate.gae\_connection.instance and hibernate.gae\_connection.database properties here.

hibernate.dialect property seems to be mandatory also.

Here's an example of persistence.xml:

```
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.0" xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">

  <persistence-unit name="myPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.ejb.HibernatePersistence</provider>
    <properties>
    
      <!-- Connection -->

      <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect" />
      <property name="hibernate.connection.provider_class" value="fi.foyt.hibernate.gae.connection.GAEConnectionProvider"/>
      <property name="hibernate.gae_connection.instance" value="testcom:testdb"/>
      <property name="hibernate.gae_connection.database" value="testdb"/>

      <!-- Session Management -->

      <property name="hibernate.current_session_context_class" value="thread" />
    </properties>
  </persistence-unit>

</persistence>
```

# Bootstrapping #

Again because Hibernate is not officially supported by App Engine you need to handle your EntityManagerFactory initialization your self. To do this all you need to do is to register Cloud SQL Driver and call Persistence.createEntityManagerFactory("myPU");

Here is a example class that does just that:

```
package fi.foyt.hibernate.gae.utils;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
```

# EntityManager #

App Engine does not officially support Hibernate or JNDI so you cannot obtain EntityManager from JNDI or by using @PersistenceContext annotation.

So unfortunately you have to call createEntityManager to get one and handle it all manually. Because App Engine does not support JTA either same applies to transactions also.

How you do it is up to you but i personally have used servlet filter to handle them automatically.

# Contribution #

If you have found a solution or workaround for some of the problems mentioned here drop me a mail to development (at) foyt.fi