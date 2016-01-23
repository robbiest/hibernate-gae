### Tools for using Hibernate on Google App Engine, including: ###

  * Simple connection pooler
  * Memcache cache provider
  * Datastore Index store for Hibernate Search

### Requirements ###

In order to run Hibernate on Google App Engine you need:

  * Google Cloud SQL (http://code.google.com/intl/fi/apis/sql/)

### Limitations ###

There are some limitations on Google App Engine that i have not (yet) been able to solve, these are:

  * App Engine is not Java EE application server, so you need to boostrap your own EntityManagerFactory and handle creation and closing of EntityManagers your self (just like in a Java SE environment)
  * JTA is not supported
  * Resource injection is not supported

### Running Hibernate in App Engine ###

This project's wiki contains a document [HibernateGaeSearchSetup](HibernateGaeSearchSetup.md) that walks thought setting up Hibernate within Google App Engine.

### Connection Pooler ###

App Engine does not provide server provided JNDI datasource. C3P0, DBCP and Proxool all use threads and thus are unusable on App Engine.

Hibernate internal connection pooler works just fine but after a while it stops working because it does not check connection health of pooled connections and after awhile starts serving dead connections.

`fi.foyt.hibernate.gae.connection.GAEConnectionProvider` is basically a copy of Hibernate's default connection pooler that accepts Cloud SQL instance and database instead of jdbc settings and supports connection health checking.

If you are interested in making it better or doing completely new pooler drop me a mail to development (at) foyt.fi

Instructions how to setup GAEConnectionProvider can be found from: [GaeConnectionProviderSetup](GaeConnectionProviderSetup.md)

### Cache region ###

hibernate-gae-cache-region is a simple Hibernate cache region that uses App Engine's Memcahe service for caching.

Currently it supports only read-write strategy, does not do any locking and does not obey timestamp or version rules so in short its quite limited.

Instructions for setting up region cache can be found from: [GaeCacheRegionSetup](GaeCacheRegionSetup.md).

If you are interested in enhancing it, adding unsupported features or creating completely new cache region send me an email to development (at) foyt.fi

### Search ###

Hibernate search is a powerful search engine for Hibernate but it has two propeties that prevent it from working in App Engine:

a) It stores it's index data in filesystem which is not allowed in App Engine.<br />
b) It uses threads for task queueing

Hibernate-gae-search addresses these problems by storing indices into Google datastore and by implementing threadless queue processor.

Library is currently still heavily under development and probably will not work in production environment.


---


This project is part of Forge & Illusion (http://code.google.com/p/fni/ | http://www.forgeandillusion.net).