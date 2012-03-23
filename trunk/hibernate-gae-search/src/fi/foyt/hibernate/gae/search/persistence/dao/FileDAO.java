package fi.foyt.hibernate.gae.search.persistence.dao;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.hibernate.gae.search.persistence.domainmodel.Directory;
import fi.foyt.hibernate.gae.search.persistence.domainmodel.File;

public class FileDAO extends GenericDAO<File> {

	private static final String DIRECTORY_NAME_LOOKUP = "File_DirectoryAndName";
	
  public FileDAO() {
    super("FILE", true);
  }

  public File create(Directory directory, String name, Long dataLength, Long modified) {
    File file = new File(directory);
    file.setDataLength(dataLength);
    file.setModified(modified);
    file.setName(name);
    file = persist(file);
    
  	String lookupKey = DIRECTORY_NAME_LOOKUP + directory.getKey() + "," + name;
  	putLookupKey(lookupKey, file.getKey());

  	return file;
  }
  
  public File findByDirectoryAndName(Directory directory, String name) {
  	String lookupKey = DIRECTORY_NAME_LOOKUP + directory.getKey() + "," + name;
  	
  	Key fileKey = getLookupKey(lookupKey);
  	if (fileKey != null) {
  		if (isNullLookupKey(fileKey))
  			return null;

  		return findObjectByKey(fileKey);
  	} 
  	
  	Query query = new Query(getKind(), directory.getKey())
      .addFilter("name", FilterOperator.EQUAL, name);
    
    File file = getSingleObject(query);
    if (file != null) {
      putLookupKey(lookupKey, file.getKey());
    } else {
    	putLookupKey(lookupKey, createNullLookupKey());
    }
    
    return file;
  }

  public List<File> listByDirectory(Directory directory) {
    Query query = new Query(getKind(), directory.getKey());
    return getObjectList(query);
  }

  public void updateModified(File file, long modified) {
    file.setModified(modified);
    persist(file);
  }

  public void updateDataLength(File file, Long dataLength) {
    file.setDataLength(dataLength);
    persist(file);
  }
  
  @Override
  public void delete(File file) {
  	String lookupKey = DIRECTORY_NAME_LOOKUP + file.getKey().getParent() + "," + file.getName();
  	removeLookupKey(lookupKey);
  	super.delete(file);
  }
}
