package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public abstract class AbstractObject {
  
  public AbstractObject(String kind) {
    this.kind = kind;
  }
  
  public Key getKey() {
    return key;
  }
  
  public void setKey(Key key) {
    this.key = key;
  }
  
  protected String getKind() {
    return kind;
  }
  
  protected Entity newEntity() {
    if (getKey() != null) {
      return new Entity(getKey());
    } else {
      return new Entity(getKind());
    }
  }

  public abstract Entity toEntity();
  public abstract void loadFromEntity(Entity entity);

  private Key key;
  private String kind;
}
