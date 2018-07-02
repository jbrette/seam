package org.jboss.seam.example.webassoc.classified;

import java.util.List;

import javax.ejb.Local;

@Local
public interface ClassifiedHome
{

   // Methods from ClassifiedHomeImpl
   public Classified initClassified();

   public void ejbRemove();

   // Methods from EntityHome and Home
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

   // Used to build sponsor pages.
   public List<Classified> getFPClassifieds();

   // Images handling
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadImage();

   public String deleteImage();

}