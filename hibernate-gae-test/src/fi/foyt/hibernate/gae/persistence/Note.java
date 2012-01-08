package fi.foyt.hibernate.gae.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Note {

  public Long getId() {
    return id;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public String getText() {
    return text;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.AUTO)
  private Long id;
  
  @Column (nullable=false)
  private String text;
}
