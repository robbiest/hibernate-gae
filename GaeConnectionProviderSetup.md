## Installation ##

First you need to add hibernate-gae-connection-provider-VERSION.jar into WEB-INF/lib folder.

You can find jar from [Downloads](http://code.google.com/p/hibernate-gae/downloads/), build it from the [Source](http://code.google.com/p/hibernate-gae/source/) or use Maven dependecy management by adding following into your pom.xml file:

```
<dependencies>
  ...
  <dependency>
    <groupId>fi.foyt.hibernate.gae</groupId>
    <artifactId>connection-provider</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <type>jar</type>
  </dependency> 
  ...
</dependencies>
```

if you havent yet added this project's Maven repository into your pom.xml, you should do that first. Instructions for that can be found from [MavenRepository](MavenRepository.md)

## Setup ##

To enable connection pooler all you have to do is set hibernate.connection.provider\_class property to fi.foyt.hibernate.gae.connection.GAEConnectionProvider, hibernate.gae\_connection.instance to your cloud sql instance and hibernate.gae\_connection.database to your database.

## Example persistence.xml ##
```
<persistence-unit name="myPU" transaction-type="RESOURCE_LOCAL">
  <properties>
    ...
    <property name="hibernate.connection.provider_class" value="fi.foyt.hibernate.gae.connection.GAEConnectionProvider"/>
    <property name="hibernate.gae_connection.instance" value="instance:name"/>
    <property name="hibernate.gae_connection.database" value="database"/>
    ...
  </properties>
</persistence-unit>
```