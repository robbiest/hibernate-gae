Before you can use Maven artifacts located in this projects Maven repository you need to add following to your Maven pom.xml:

```
  <repositories>
    ...
    <repository>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
      <id>hibernate-gae</id>
      <name>Hibernate GAE repository</name>
      <url>https://hibernate-gae.googlecode.com/svn/repository</url>
    </repository>
    ...
  </repositories>
```