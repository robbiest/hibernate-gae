package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.Query;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.WorkList;

public class WorkListDAO extends GenericDAO<WorkList> {
  
  public WorkListDAO() {
    super("WORKLIST", true);
  }

  public WorkList create(Directory directory, byte[] data) {
    WorkList workList = new WorkList(directory);
    workList.setData(data);
    return persist(workList);
  }
  
  public WorkList findOldest() {
    Query query = new Query(getKind());
    return getSingleObject(query);
  }
  
	public List<WorkList> listByDirectory(Directory directory) {
    Query query = new Query(getKind(), directory.getKey());
    return getObjectList(query);
  }

  public void updateData(WorkList workList, byte[] data) {
    workList.setData(data);
    persist(workList); 
  }

}
