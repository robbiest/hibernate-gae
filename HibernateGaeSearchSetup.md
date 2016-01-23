## Installation ##

First you need to add hibernate-gae-search-VERSION.jar into WEB-INF/lib folder.

You can find jar from [Downloads](http://code.google.com/p/hibernate-gae/downloads/), build it from the [Source](http://code.google.com/p/hibernate-gae/source/) or use Maven dependecy management by adding following into your pom.xml file:

```
<dependencies>
  ...
  <dependency>
    <groupId>fi.foyt.hibernate.gae</groupId>
    <artifactId>hibernate-gae-search</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <type>jar</type>
  </dependency> 
  ...
</dependencies>
```

if you havent yet added this project's Maven repository into your pom.xml, you should do that first. Instructions for that can be found from [MavenRepository](MavenRepository.md)

## Setup ##

Search configuration is a little bit trickier than configuration other modules in this project.

First you need to jdoconfig.xml into your project's src/META-INF folder with following contents:

```
<?xml version="1.0" encoding="utf-8"?>
<jdoconfig xmlns="http://java.sun.com/xml/ns/jdo/jdoconfig" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://java.sun.com/xml/ns/jdo/jdoconfig">
  <persistence-manager-factory name="hibernate-search-persistence-manager-factory">
    <property name="javax.jdo.PersistenceManagerFactoryClass" value="org.datanucleus.api.jdo.JDOPersistenceManagerFactory" />
    <property name="javax.jdo.option.ConnectionURL" value="appengine" />
    <property name="javax.jdo.option.NontransactionalRead" value="true" />
    <property name="javax.jdo.option.NontransactionalWrite" value="true" />
    <property name="javax.jdo.option.RetainValues" value="true" />
    <property name="datanucleus.appengine.autoCreateDatastoreTxns" value="true" />
  </persistence-manager-factory>

</jdoconfig>
```

This will add new JDO persistence manager factory into your project. If you already use JDO in your project you should just add another persistence manger factory below existing configurations.

Next thing you need to do is to add search settings into persistence.xml. Required properties are:

hibernate.search.default.directory\_provider which shold be se to fi.foyt.hibernate.gae.search.GaeDirectoryProvider and
hibernate.search.default.worker.backend which should be fi.foyt.hibernate.gae.search.GaeBackendQueueProcessor

## Example persistence.xml ##
```
<persistence-unit name="myPU" transaction-type="RESOURCE_LOCAL">
  <properties>
    ...
      <property name="hibernate.search.default.optimizer.operation_limit.max" value="1000" /> 
      <property name="hibernate.search.default.optimizer.transaction_limit.max" value="100" />
      <property name="hibernate.search.default.directory_provider" value="fi.foyt.hibernate.gae.search.GaeDirectoryProvider"/>
      <property name="hibernate.search.default.worker.backend" value="fi.foyt.hibernate.gae.search.GaeBackendQueueProcessor"/>
    ...
  </properties>
</persistence-unit>
```