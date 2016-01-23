## Installation ##

First you need to add hibernate-gae-cache-region-VERSION.jar into WEB-INF/lib folder.

You can find jar from [Downloads](http://code.google.com/p/hibernate-gae/downloads/), build it from the [Source](http://code.google.com/p/hibernate-gae/source/) or use Maven dependecy management by adding following into your pom.xml file:

```
<dependencies>
  ...
  <dependency>
    <groupId>fi.foyt.hibernate.gae</groupId>
    <artifactId>hibernate-cache-region</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <type>jar</type>
  </dependency> 
  ...
</dependencies>
```

if you havent yet added this project's Maven repository into your pom.xml, you should do that first. Instructions for that can be found from [MavenRepository](MavenRepository.md)

## Setup ##

All you need to do is to set `hibernate.cache.region.factory_class` property to `fi.foyt.hibernate.gae.cache.GaeCacheRegionFactory` in persistence.xml otherwise cache is configured via standard hibernate cache settings.

## Example persistence.xml ##
```
<persistence-unit name="myPU" transaction-type="RESOURCE_LOCAL">
  <properties>
    ...
    <property name="hibernate.cache.region.factory_class" value="fi.foyt.hibernate.gae.cache.GaeCacheRegionFactory" />
    <property name="hibernate.cache.use_second_level_cache" value="true" />
    <property name="hibernate.cache.use_query_cache" value="true" />
    ...
  </properties>
</persistence-unit>
```