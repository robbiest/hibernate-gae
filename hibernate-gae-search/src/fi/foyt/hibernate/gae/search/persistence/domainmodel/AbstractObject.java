package fi.foyt.hibernate.gae.search.persistence.domainmodel;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public abstract class AbstractObject {
  
  public AbstractObject(String kind) {
    this.kind = kind;
  }

  public AbstractObject(String kind, Key parent) {
    this.kind = kind;
    this.parent = parent;
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
  
  public void setParent(Key parent) {
	  this.parent = parent;
  }
  
  protected Entity newEntity() {
    if (getKey() != null) {
      return new Entity(getKey());
    } else {
    	if (parent != null)
    		return new Entity(getKind(), parent);
    	else
    		return new Entity(getKind());
    }
  }

  public abstract Entity toEntity();
  public abstract void loadFromEntity(Entity entity);

  private Key key;
  private Key parent;
  private String kind;
}
