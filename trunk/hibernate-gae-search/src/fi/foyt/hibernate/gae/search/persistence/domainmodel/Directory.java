package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import com.google.appengine.api.datastore.Entity;

public class Directory extends AbstractObject {
  
  public Directory() {
    super("DIRECTORY");
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Entity toEntity() {
    Entity entity = newEntity();
    
    entity.setProperty("name", getName());
    
    return entity;
  }

  @Override
  public void loadFromEntity(Entity entity) {
    if (entity.getKey() != null) {
      this.setKey(entity.getKey());
    }
    
    this.name = (String) entity.getProperty("name");
  }

  private String name;
}